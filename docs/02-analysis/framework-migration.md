# Framework Migration: Playwright + Java + JUnit 6

## Thesis

This migration replaces legacy XPath‑based, Selenium‑driven Java tests with modern Playwright web automation using Java bindings under Maven. JUnit 6 (or JUnit 5 as fallback) supersedes JUnit 4 for lifecycle control and assertion clarity. Curl wrappers are retired in favor of Playwright's native APIRequestContext, allowing network API tests to run alongside UI tests in the same suite.

## Change 1: JUnit 4 → JUnit 6 / JUnit 5

**Context:** The current suite uses JUnit 4. Java test lifecycle is managed via `@Before`, `@After`, `@BeforeClass`, `@AfterClass` with no per‑class test isolation control.

**Recommendation:** Move to JUnit 5 or JUnit 6 with `@BeforeAll`, `@AfterAll`, `@TestInstance(TestInstance.Lifecycle.PER_CLASS)` for shared Playwright/API request contexts.

**Why:**
- JUnit 6 is actively maintained (JUnit 6.0 released Q4 2025).
- JUnit 5+ supports parameterized tests, dynamic tests, and better assertion API (assertEquals with message, assertThrows, etc.).
- JUnit 4 is maintenance mode.

**Fact:** JUnit 6 release notes document new `@Template` injection API for parameterized test support, improved TestExecutionListener extension model, and Kotlin coroutine support (baseline JUnit 5.14+). Maven artifact: `org.junit.jupiter:junit-jupiter:5.14.0+` or `org.junit.platform:junit-platform-launcher:1.12.0+`.

**Risk:** JUnit 6 migration requires Java 17+. Verify CI image matches before commit.

---

## Change 2: Selenium 4 WebDriver → Playwright Java

**Context:** Current suite drives browsers via Selenium WebDriver with 100% XPath locators. Helper methods hide verifications in page objects, making failures hard to debug.

**Recommendation:** Replace WebDriver with Playwright Java (`com.microsoft.playwright:playwright`). Use Playwright's locator API instead of raw XPath.

**Why (facts):**
- Playwright locators are lazy, auto‑waiting, and re‑query on every action. This eliminates most `Thread.sleep()` / implicit wait boilerplate.
- Auto‑wait covers: attached to DOM, visible, stable, actionable, enabled.
- Playwright supports Chromium, Firefox, WebKit from the same Java API, enabling cross‑browser parity tests without browser‑driver version juggling (no chromedriver/geckodriver version pins in Maven).
- Playwright's web‑first assertions (`expect(locator).toBeVisible()`) retry until condition met or timeout, reducing flaky false negatives.

**Locator ladder (preference order):**
1. `getByRole('role', { name: 'X' })` — matches accessibility tree, survives CSS refactors
2. `getByLabel('Label')` — form controls
3. `getByText('text')` — visible copy
4. `getByTestId('test-id')` — custom widgets
5. CSS selectors — structural fallback
6. XPath — last resort only

**Risk:** Custom components without correct ARIA roles return no match for `getByRole`. Mitigation: add `data-testid` attributes during UI refactor, or run Playwright accessibility checker during dev.

---

## Change 3: Assertions in Tests, Not Helpers

**Context:** Current POM abstracts verifications into helper methods (`verifyLoginSuccess()`, `assertCartTotal()`). Tests delegate assertion logic, so failure stack traces point into fixtures rather than the test intent.

**Recommendation:** Keep assertions (verifications) in test methods. Move only **operations** (login flow, navigation, data setup) into helper/page objects.

**Pattern:**
```java
// GOOD: assertion in test, operation in page object
LoginPage loginPage = new LoginPage(page);
DashboardPage dashboard = loginPage.login(user, password); // operation
assertTrue(dashboard.isWelcomeBannerVisible(), "Welcome banner should appear after login"); // assertion
```

**Fact:** JUnit 5/6 assertion API supports custom messages as the last argument, making failure output self‑documenting.

---

## Change 4: Curl Wrapper → Playwright APIRequestContext

**Context:** Java code wraps `curl` via `ProcessBuilder`/`Runtime.exec()` to hit REST APIs, then parses JSON responses manually. This adds a shell dependency, escaping issues, and no shared auth state with UI tests.

**Recommendation:** Replace all curl wrappers with Playwright `APIRequestContext`. Use it for:
- API calls inside UI tests (setup/login via API, then browser open with shared auth)
- Standalone API tests (no browser launched)

**Fact:** `APIRequestContext` supports GET/POST/PUT/PATCH/DELETE/HEAD with headers, query params, form data, multipart, and JSON bodies. It reuses cookies/auth state with browser contexts — you can log in via API, capture storageState, then open a page already authenticated.

**Risk:** `APIRequestContext` requires Playwright binary downloads (browsers). CI must allow network access for `mvn test` to install browsers on first run, or pre‑install via `mvn playwright:install-browsers`.

---

## Change 5: Page Object Model Modernization

**Context:** Current POM mixes locators and verifications, uses XPath strings scattered in page classes.

**Recommendation:**
- One page object per page/component, exposing **locators as fields** and **actions as methods**.
- Never assert inside a page object method; return state instead (`isElementVisible()`, `getText()`).
- Store locators using Playwright's `Locator` objects (lazy, auto‑waiting) rather than raw `By` strings.
- Use `getByTestId` for custom widgets owned by the team; add `data-testid` to component library where missing.

---

## Migration Risks & Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Playwright browser binary not in offline CI | Build fails | Pre‑install browsers in Docker image; commit `playwright.config.yaml` with `browsers: ["chromium"]` |
| Custom components lack ARIA roles | `getByRole` returns nothing | Add `data-testid`; run Playwright accessibility checker during dev |
| JUnit 6 requires Java 17 | CI image too old | Upgrade CI JDK or pin JUnit 5.14 (Java 8+) |
| Team knows XPath, not role‑based locators | Adoption friction | 1‑day locator workshop; lint rule banning `xpath=` in new tests |
| Flaky tests during migration | False confidence | Parallel run old + new suite; compare failure rates before cutover |