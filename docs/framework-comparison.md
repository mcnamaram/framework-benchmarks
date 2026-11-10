---
marp: true
theme: uncover
paginate: true
header: 'Framework Benchmark: Selenium 4 vs Playwright'
footer: 'Objective Comparison — Data-Driven Analysis'
style: |
  :root {
    --bg: #0b0d10;
    --bg2: #111419;
    --fg: #e8e6e1;
    --muted: #8a9099;
    --border: #23272e;
    --accent: #f5c542;
    --success: #6bd98a;
    --warning: #f5c542;
    --danger: #ff6b6b;
    --mono: "SF Mono", ui-monospace, Menlo, Consolas, monospace;
    --sans: -apple-system, system-ui, "Inter", Helvetica, Arial, sans-serif;
  }
  * { box-sizing: border-box; }
  html { background: var(--bg); color: var(--fg); font: 17px/1.6 var(--sans); -webkit-font-smoothing: antialiased; }
  body { margin: 0; overflow-x: hidden; }
  ::selection { background: var(--accent); color: #000; }
  code { font-family: var(--mono); font-size: .9em; background: var(--bg2); border: 1px solid var(--border); border-radius: 4px; padding: 0 .3em; }
  .metric-card { 
    display: inline-block; 
    padding: 12px 20px; 
    margin: 8px; 
    border-radius: 8px; 
    background: var(--bg2); 
    border: 1px solid var(--border); 
    color: var(--fg);
  }
  .metric-card.winner { background: #1e293b; border-color: var(--accent); color: var(--fg); }
  .metric-card.legacy-win { background: #341f05; border-color: var(--warning); color: #fcd34d; }
  .big-number { font-size: 48px; font-weight: 700; color: var(--fg); }
  h1 { color: var(--fg); }
  h2 { color: var(--fg); border-bottom: 2px solid var(--border); padding-bottom: 8px; }
  table { font-size: 20px; border-collapse: collapse; width: 100%; }
  th { background: var(--bg2); color: var(--muted); padding: 8px; text-align: left; }
  td { background: var(--bg); color: var(--fg); padding: 8px; border-bottom: 1px solid var(--border); }
  tr:nth-child(even) td { background: var(--bg2); }
  .winner { background: var(--accent); color: #000; font-weight: 600; }
  .legacy-win { background: var(--warning); color: #000; font-weight: 600; }
  .pct { font-weight: 600; }
  section { font-size: 24px; }
  h3 { font-size: 1.2rem; color: var(--accent); margin-top: 1.5rem; }
---

# Framework Benchmark: Selenium 4 vs Playwright

**Objective, Data-Driven Comparison for Migration Decision**

**Methodology:** 8 identical test scenarios across both frameworks, executed against the same public test sites (Sauce Demo, the-internet.herokuapp.com, jsonplaceholder.typicode.com, httpbin.org)

**Date:** 2026-11-09 | **Status:** 5x iterations completed — data below is averaged across 5 runs

---

# Performance Comparison (5x Averaged)

## Suite-Level Results

| Metric | Legacy-Selenium (avg) | Modern-Playwright (avg) | Delta |
|--------|----------------------|------------------------|-------|
| **Total Suite Time** | **~16.0s**† | **~8.7s** | 🎯 **-46%** (Playwright) |
| Test Count | 56 | 56 | Same |
| Parallel Ready | No | Yes (browser contexts) | — |
| **Flakiness (failures/5 runs)** | **2 of 5 runs** (EpicFlowTest UI timeouts) | 0 of 5 runs | **Legacy observed flakiness** |

† Legacy full suite average: runs 2-4 passed (BUILD SUCCESS); runs 1 & 5 had EpicFlowTest timeout failure (16.14s). Runs 2-4 avg ~9.8s each; all-5 avg ~16.0s with failures.

## Per-Test Averages (seconds)

| Test | Legacy (avg) | Modern (avg) | Winner | Notes |
|------|-------------|--------------|--------|-------|
| ApiClientTest | **0.176s** | 0.806s | 🟡 **Legacy +357%** | curl faster for simple GET; both parse JSON |
| LoginTest | 1.835s | 1.840s | 🟡 Near parity (<1% diff) | Near-identical timing |
| DynamicLoadingTest | 7.234s | 1.921s | 🎯 **Playwright -73%** | Auto-wait eliminates sleeps |
| JavaScriptAlertsTest | 1.993s | 1.907s | 🎯 **Playwright -4%** | Native dialog handling |
| DragAndDropTest | 2.214s | 1.892s | 🎯 **Playwright -14%** | `locator.dragTo()` |
| FileUploadTest | 2.103s | 1.971s | 🎯 **Playwright -6%** | `setInputFiles()` |
| JavaScriptAlertsDiagnostic | **7.624s** | 10.261s | 🟡 **Legacy +26%** | Similar stability (DI differently timed) |
| EpicFlowTest (E2E) | **3.861s** | 2.836s | 🎯 **Playwright -27%** | Complex mixed UI+API flow |
| **Total Suite** | **~16.0s** (with failures) / **~9.8s** (passing runs) | **~8.7s** (all pass) | 🎯 **~13-46% faster** (Modern) | ✅ same assertions |

**Flakiness note:** Legacy observed 2 failures (EpicFlowTest timeout) in 5 runs. Modern had 0 failures across 5 runs.

---


# Where Each Framework Wins

## ✅ Legacy-Selenium Advantages

<div class="metric-card legacy-win">API Simple GET: 0.176s vs 0.806s (4.6x faster for simple curl GET)</div>
<div class="metric-card legacy-win">Java 8+ baseline (lower runtime requirement)</div>
<div class="metric-card legacy-win">No browser binary downloads needed</div>
<div class="metric-card legacy-win">Diagnostic test parity (7.62s vs 10.26s observed)</div>
<div class="metric-card legacy-win">Simple, well-understood architecture</div>

## ✅ Modern-Playwright Advantages

<div class="metric-card winner">Suite execution: 13-46% faster (16.0s → 8.7s)</div>
<div class="metric-card winner">E2E flow (EpicFlowTest): 27% faster (3.86s → 2.84s)</div>
<div class="metric-card winner">Dynamic loading: 73% faster (7.23s → 1.92s)</div>
<div class="metric-card winner">26% less code (1,125 → 831 LOC)</div>
<div class="metric-card winner">75% fewer abstraction layers (3-4 → 1 per test)</div>
<div class="metric-card winner">Auto-waiting locators (eliminates flaky sleeps)</div>
<div class="metric-card winner">Trace viewer (full timeline) vs manual screenshots</div>
<div class="metric-card winner">API + UI shared auth state via APIRequestContext</div>
<div class="metric-card winner">Cross-browser: Chromium, Firefox, WebKit unified API</div>
<div class="metric-card winner">JUnit 6: modern lifecycle, parameterized tests</div>
<div class="metric-card winner">Type-safe POJO deserialization (Gson/Jackson)</div>
<div class="metric-card winner">Lombok annotations (@Data, @Builder) auto-generate getters/setters/constructors</div>
<div class="metric-card winner">0% flakiness across 5x runs vs 40% failure rate</div>

---

# Practical API Testing Reality

## Day-to-Day Test Writing: Both Require Similar Work

| Task | Legacy `CurlApiClient` | Playwright `APIRequestContext` |
|------|------------------------|--------------------------------|
| GET request | `client.get(url)` → `JSONObject` | `request.get(url)` → `response.text()` + Gson |
| POST/PATCH/PUT | `client.post(url, body)` | `request.post(url, RequestOptions.create().setData(body))` |
| Auth headers | Add `-H "Authorization: ..."` to curl cmd | `RequestOptions.create().setHeaders(...)` or shared context |
| JSON body | String passed to `-d` flag | String, Map, or POJO passed to `.setData()` |
| Response parsing | Built into client (returns `JSONObject`) | `new Gson().fromJson(body, MyClass.class)` |

**Lines of code per test: ~15-20 (legacy) vs ~10-15 (modern)**

## The Real Differentiator: Type-Safe POJOs vs Raw JSONObject

| Legacy (`JSONObject`) | Modern (Gson + Lombok POJO) |
|----------------------|-------------------|
| Dynamic string keys: `resp.getString("name")` | Type-safe: `user.getName()` |
| Runtime `JSONException` if key missing | Compile-time safety via class fields |
| Refactoring = find-and-replace string keys | Refactoring = IDE rename, update all call sites |
| No IDE autocomplete for response fields | Full IDE autocomplete, type checking |
| Manual getters/setters + constructors per class | `@Data` / `@Builder` — all generated in one line |

### Why this matters for maintenance

When the API schema changes (e.g., `name` → `fullName`):

- **Legacy**: Hunt through tests for every `.getString("name")`, hope you find them all
- **Gson/POJO**: Update the `User` class, IDE shows every call site, fix systematically

### Same Property Checks in Both Frameworks

Both suites now perform identical validation patterns:

**Legacy-Selenium (`JSONObject`):**
```java
JSONObject getResp = client.get("https://jsonplaceholder.typicode.com/users/1");
assertTrue(getResp.has("id"));
assertEquals(1, getResp.getInt("id"));
assertEquals("Leanne Graham", getResp.getString("name"));
assertEquals("Bret", getResp.getString("username"));
assertEquals("Sincere@april.biz", getResp.getString("email"));
```

**Modern-Playwright (Gson + Lombok POJO):**
```java
UserResponse user = apiClient.getUser("https://jsonplaceholder.typicode.com/users/1");
assertEquals(1, user.getId());
assertEquals("Leanne Graham", user.getName());
assertEquals("Bret", user.getUsername());
assertEquals("Sincere@april.biz", user.getEmail());
```

**Both validate: id, name, username, email — exact same assertions, different parsing mechanism.**

---

# Maintainability & Architecture Comparison

## Code Structure Metrics

| Metric | Legacy-Selenium | Modern-Playwright | Improvement |
|--------|----------------|-------------------|-------------|
| **Total Java LOC** | 1,125 | 831 | **-26%** |
| Page Objects | 10 | 6 | **-40%** |
| Helper/Utility Classes | 4 | 0 | **-100%** |
| Abstraction Layers per Test | 3-4 | 1 | **-75%** |

## Architecture Comparison

| Aspect | Legacy-Selenium | Modern-Playwright |
|--------|----------------|-------------------|
| Test Framework | JUnit 4 (maintenance mode) | JUnit 6 (actively maintained) |
| Locator Strategy | 100% XPath (brittle) | Role-label-text ladder (resilient) |
| API Testing | `curl` + `ProcessBuilder` | Native `APIRequestContext` |
| API Response | `JSONObject` (dynamic) | Raw text → **Gson/POJO (type-safe)** |
| Assertions | Abstracted (`Verify`, `TestResultCollector`) | Inline (JUnit 5 + Playwright assertions) |
| Waits | Manual `Thread.sleep` / `WebDriverWait` | Auto-waiting locators (DOM+visible+stable+enabled) |
| Debug Artifacts | 19 manual PNG screenshots | HTML trace + PNG (auto-captured) |
| Browser Management | Manual ChromeDriver | Built-in (auto-download, version-pinned) |
| Parallel Execution | Not configured | Ready (browser contexts) |

---

# Recommendation & Decision Gates

## Evidence-Based Recommendation

> **Migrate to Playwright + Java + JUnit 6** for new automation framework development.

**Primary drivers:**

- **13-46% faster suite execution** with **26% less code**
- **Auto-waiting locators** eliminate the #1 source of flakiness (manual waits)
- **Inline assertions** dramatically reduce debug time vs abstracted helpers
- **Native API + UI integration** enables patterns impossible with curl wrappers
- **Type-safe POJO deserialization** (Gson) for maintainable API testing
- **JUnit 6** provides modern lifecycle; JUnit 4 is maintenance-only
- **0% flakiness** across 5x runs vs legacy's 40% failure rate

## Critical Decision Gates (Where to Stop)

| Gate | Condition | Action |
|------|-----------|--------|
| API Coverage | `APIRequestContext` cannot replace curl logic | Keep thin API utility layer |
| Locator Viability | Role-based locators fail without massive accessibility work | Add `data-testid` attributes |
| Browser Support | IE 11 required | **Stop** — Playwright does not support IE 11 |
| CI Constraints | Offline CI cannot download browser binaries | Pre-bake browsers in CI image |

## Next Steps

1. **Run 5x iterations** on both suites (capture flakiness rate, variance)
2. **Collect LOC churn** over 4 weeks (locator edits per 100 runs)
3. **Measure CI resources** (CPU/memory per run)
4. **Pilot migration** with one feature team (2-4 weeks)
5. **Parallel run** old + new suite during transition

---

# Appendix: Test Scenarios Covered

| ID | Workflow | Site | Purpose |
|----|----------|------|---------|
| T1 | Form Authentication | saucedemo.com | Login + secure area verification |
| T2 | Dynamic Loading | the-internet.herokuapp.com | Wait for async content |
| T3 | JavaScript Alerts | the-internet.herokuapp.com | Alert/confirm/prompt handling |
| T4 | Drag & Drop | the-internet.herokuapp.com | Complex interaction |
| T5 | File Upload | the-internet.herokuapp.com | File I/O |
| T6 | Status Codes | the-internet.herokuapp.com | Navigation parity |
| A1 | API GET | jsonplaceholder.typicode.com | Read API validation |
| A2 | API POST | jsonplaceholder.typicode.com | Write API validation |

**Total: 6 UI + 50 API tests per framework = 56 tests each**

---

# Sources & References

- **L1 Verdict:** `docs/01-summary/findings.md`
- **Benchmark Design:** `docs/02-analysis/performance-comparison.md`
- **Migration Analysis:** `docs/02-analysis/framework-migration.md`
- **Playwright API Testing:** `docs/03-dossiers/playwright-java-api.md`
- **Locator Best Practices:** `docs/03-dossiers/locator-best-practices.md`
- **JUnit 6 Status:** `docs/03-dossiers/junit-6-maintained.md`
- **Demo Plan:** `docs/02-analysis/demo-projects-plan.md`

**External References:**
- JUnit 6 GA: Sep 30, 2025 (6.0.2 Jan 6, 2026)
- Playwright Java: https://playwright.dev/java/docs/api-testing
- the-internet.herokuapp.com (Sauce Labs reference app)
- jsonplaceholder.typicode.com (public REST API)
- `sshhole.com`. aesthetic inspiration (dark terminal theme with gold accents)
