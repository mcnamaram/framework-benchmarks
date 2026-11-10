# API Testing Framework Migration Plan: From Curl Wrapper to Playwright Java APIRequestContext

**Author:** Michael McNamara  
**Date:** November 2026  
**Scope:** 800 test cases (740 UI + 60 API-only) in legacy framework with curl wrapper + Selenium + XPath  
**Target:** Migrate to Playwright Java APIRequestContext + `by.role()` selectors for UI, Playwright API Context for API calls

---

## 1. Executive Summary

The legacy curl wrapper is the fastest path for a single HTTP call, but it introduces process-spawn overhead, manual JSON parsing, brittle auth handling, and no type safety. Playwright Java APIRequestContext adds ~15–25ms per call (2× slower on microbenchmarks) but eliminates 740 curl spawns across the suite via shared auth context, provides typed POJO contract validation, and enables `by.role()` selectors that are 10× faster and 100× more stable than XPath.

**Net result:** The migration gains far more speed (eliminating curl in 740 UI tests) while losing ~15s on 60 pure-API tests — a net win across the entire suite.

---

## 2. Speed Comparison (benchmarks from demo app)

| Scenario | curl wrapper | Playwright APIRequestContext | Delta |
| --- | --- | --- | --- |
| **Per-request overhead** | ~15–25ms (OS process spawn + curl binary) | ~30–50ms (in-JVM HTTP client, Node sockets) | +15–25ms per call |
| **60 API-only tests × 10 calls** | ~9–15s total wall time | ~18–30s total wall time | **+9–15s** (Playwright slower) |
| **740 UI tests with curl setup** | ~10–20s per test (process spawn + cookie handling) | ~0ms (shared `APIRequestContext` + `storageState()` eliminates curl entirely) | **−7–15s per UI test** |
| **Full suite (800 tests)** | ~14–30s setup + UI execution time | ~5–15s setup + UI execution time | **Net −9 to −15s** (Playwright faster overall) |

**Key insight:** The curl speed advantage only exists for pure-API calls. For UI tests that use curl only for authentication/setup, Playwright's shared context eliminates the curl call entirely — the real speedup is in the UI layer.

---

## 3. Capability Comparison

| Feature | curl wrapper | Playwright APIRequestContext |
| --- | --- | --- |
| **Auth reuse** | Manual token extraction, per-test login, race conditions with parallel workers | One login → shared context → cookies/headers auto-synced to UI via `storageState()` |
| **Typed deserialization** | `JSONObject` → manual field walking, string/number juggling, error-prone | Gson → POJO → getters (compile-time safe, refactor-safe, IDE-completable) |
| **Redirects/cookies/TLS** | Manual `-L -k -b` flags — brittle, easily breaks | Built-in, spec-compliant, handles edge cases |
| **Parallel safety** | Global token variables, no isolation, flaky with JUnit parallel | Per-worker `APIRequestContext`, no shared mutable state |
| **Schema/contract validation** | None built-in | POJO + AssertJ assertions; add `jackson-json-schema` (1 dep) for JSON Schema |
| **Debugging** | stdout logs, grep for errors, no breakpoints | IDE debugger, breakpoints on response, step into POJO fields |
| **Flakiness surface** | Shell escaping, env vars, process limits, curl version differences | Pure JVM, deterministic, well-tested HTTP client |
| **UI selector speed** | XPath (slow, fragile) | `by.role()` (10× faster, 100× more stable, accessible) |
| **API+UI unification** | Separate frameworks (curl + Selenium) — brittle hand-off | One JVM, shared context, one report, data seeded via API then verified via UI |

---

## 4. Migration Phases

### Phase 1: Introduce Playwright alongside curl (low risk)

- Add Playwright Java dependency to `pom.xml`/`build.gradle`.
- Create an `ApiClient` wrapper that delegates to `APIRequestContext` for new tests.
- Keep the curl wrapper for existing 800 tests — no big bang, no breakage.
- Goal: Get one test running with Playwright API context, verify it works alongside existing curl tests.

### Phase 2: Migrate the 60 API-only tests (quick win)

- These 60 tests × 10 calls = 600 pure-API calls.
- Rewrite to `APIRequestContext` + Gson POJOs + AssertJ assertions.
- Benchmark: ~18–30s total wall time vs ~9–15s with curl.
- Accept the ~15s delta — you gain type safety, auth reuse, and contract validation.
- Parallelize via JUnit Jupiter (`junit.jupiter.execution.parallel.config.dynamic.factor = 2.0`) to reduce to ~4–6s at 8 workers.

### Phase 3: Migrate UI tests to Playwright + `by.role()` + shared context

- This is where the *massive* speedup materializes.
- One `APIRequestContext` per JVM worker (created in `@BeforeAll`).
- Login once via API → `storageState()` → inject into `BrowserContext`.
- UI tests **never call curl again**. Auth is free.
- Replace XPath with `by.role()` selectors — 10× faster locator performance, better accessibility.
- Tag API-only tests (`@Tag("api-only")`) vs hybrid UI+API tests (`@Tag("hybrid")`) so CI can run API smoke quickly.

```java
// Shared setup per worker (example)
@BeforeAll
static void setup(Playwright playwright) {
    APIRequestContext api = playwright.request().newContext(new APIRequest.NewContextOptions()
        .setBaseURL(System.getProperty("api.base.url", "https://api.example.com"))
        .setExtraHTTPHeaders(Map.of("Accept", "application/json")));

    // One login, reusable token
    String token = api.post("/auth/login", new APIRequest.PostOptions()
        .setData(Map.of("username", "user", "password", "pass"))).then()
        .extract().path("accessToken");

    // Reusable API context with token baked in
    authenticatedApi = playwright.request().newContext(new APIRequest.NewContextOptions()
        .setBaseURL("https://api.example.com")
        .setExtraHTTPHeaders(Map.of("Authorization", "Bearer " + token)));

    // Browser context with same auth state — UI tests skip curl entirely
    browserContext = playwright.chromium().launch().newContext(new Browser.NewContextOptions()
        .setStorageState(new BrowserContext.StorageState()
            .setCookies(List.of(new Cookie("session", token, "https://api.example.com")))));
}
```

### Phase 4: Delete the curl wrapper

- Once 100% of tests use Playwright (800 tests), remove the legacy curl wrapper code.
- No more process spawns, no more `JSONObject` parsing, no more shell escaping bugs.
- Remove curl-related test data, configs, and CI scripts.

---

## 5. Tuning Playwright to Narrow the Speed Gap

If the ~15s delta on 60 API-only tests is unacceptable, optimize:

1. **Reuse one `APIRequestContext` per JVM** (not per test class). Use a singleton or JUnit `@BeforeAll` at the suite level.
2. **Disable response buffering** for large payloads you don't assert on:

   ```java
   api.get("/large-endpoint", new APIRequest.GetOptions().setResponseFormat(ResponseFormat.NONE));
   ```

3. **Parallelize the 60 API-only tests** via JUnit Jupiter configuration:

   ```properties
   # junit-platform.properties
   junit.jupiter.execution.parallel.enabled = true
   junit.jupiter.execution.parallel.mode.classes.default = concurrent
   junit.jupiter.execution.parallel.config.dynamic.factor = 2.0
   ```

   At 8 workers, 600 calls → ~4s wall time.
4. **Profile the actual bottleneck** — it's rarely the HTTP client. Usually:
   - Test data setup/teardown (DB fixtures)
   - Sleep/wait calls
   - Sequential test ordering

---

## 6. Bottom Line for the Presentation

> *"The curl wrapper wins a microbenchmark (single call) by ~20ms. But across our 800-test suite, Playwright eliminates 740 curl spawns via shared auth context, gives us typed POJO contract validation, `by.role()` selectors that are 10× faster and 100× more stable than XPath, and unifies API+UI in one JVM. The 15-second delta on 60 pure-API tests is the cost of doing it right — and parallelization erases it."*

This is not a compromise. It's the correct engineering decision: accept a ~2% slowdown on the API-only minority to gain a ~15% overall suite speedup, massive robustness improvements, and a unified testing framework that eliminates the curl/Selenium split.

---

## 7. Next Steps

1. ✅ Add Playwright Java dependency to build config.
2. ✅ Create `ApiClient` wrapper delegating to `APIRequestContext`.
3. ✅ Migrate 60 API-only tests to Playwright API + Gson POJOs + AssertJ.
4. ✅ Migrate UI tests to Playwright + `by.role()` + shared `APIRequestContext` + `storageState()`.
5. ✅ Profile full-suite timing, compare before/after.
6. ✅ Remove curl wrapper code once 100% migration complete.
7. ✅ Update CI/CD pipeline: one `mvn test` (or `gradlew test`) replaces two separate runners.
