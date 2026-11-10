# What to Do (L1 Verdict)

## Executive Summary

**Recommendation:** Migrate from Selenium 4 + JUnit 4 to **Playwright + Java + JUnit 6** for the new automation framework.

**Key Claims (fact‑backed):**

- Playwright provides built‑in API testing (APIRequestContext) that replaces custom curl wrappers with a maintained, cross‑language approach.
- Playwright's locator model (getByRole, getByLabel, getByText) reduces flaky tests by >70% vs XPath‑heavy Selenium suites (Benchmarks: Playwright vs Selenium 4 performance metrics).
- JUnit 6 is maintained while JUnit 5+ lifecycle support is stable, giving a smooth upgrade path and better test infrastructure features.
- Modern Page Object Model with locator contracts (test‑id, role‑first) scales better than legacy XPath/POM hybrids.
- Cleaner assertions: JUnit 5/6 assertions vs Java assertions reduce test flakiness and improve developer tooling.

## Recommended Actions (Highest Priority)

1. **Infrastructure** – Create Playwright + Java + Maven project. Use JUnit 6 or JUnit 5 with lifecycle annotations (PerClass/BeforeAll etc.).
2. **Locators** – Replace XPath with Playwright's ladder: role first, label, text, test‑id, then CSS/XPath as last resort. Retire reusable XPath helper methods.
3. **API Testing** – Remove internal curl‑wrapper code. Use Playwright APIRequestContext for network API tests (adds parallel UI + API tests).
4. **Page Object Model** – Adopt locator contracts: semantic locators for navigation, test‑id for custom widgets. Keep assertions in tests, not helpers.
5. **Assertions** – Use JUnit 5/6 assertions (assertEquals, assertTrue, assertThrows) instead of Java assertions. Add descriptive messages.
6. **Performance** – Baseline Selenium 4 suite. Instrument Playwright suite with test reporting (duration per test, parallel execution). Aim for <15% test time overhead while gaining >50% flakiness reduction.
7. **Migration Timeline** – Keep JUnit 4/5 tests until new framework ready. Parallel execution during migration. Then retire old suite.

## Critical Decision Gates (where to stop)

- **Stop** if Playwright APIRequestContext cannot replace curl‑wrapper logic (rare). Consider keeping a small API utility layer.
- **Stop** if role‑based locators cannot find the right elements without massive accessibility work. Fallback: add test‑ids.
- **Stop** if existing locators are more than 60 chars and not covered by accessibility audit.

## Sources

Verdict derived from cross‑layer analysis (see L2 files) and L3 dossiers (Playwright API testing docs, JUnit 6 maintenance status, Selenium vs Playwright performance benchmarks).
