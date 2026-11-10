# Performance Comparison: Selenium 4 vs Playwright

## Thesis

Same workflow, old vs new: measure test suite execution time, flakiness rate, and maintenance effort (locator churn) to quantify migration value.

## Benchmark Design

**Workload:** 1 representative end‑to‑end user flow (login → navigate → submit form → verify result), executed N times against the same staging environment.

**Metrics collected:**

1. **Suite wall‑clock time** (seconds) — single browser, then parallel (max 4 workers)
2. **Per‑test mean time** — old vs new
3. **Flaky failure rate** — % of tests that pass on retry without code change
4. **Locator churn** — count of selector edits per 100 test runs over 4 weeks
5. **CI resource usage** — CPU/memory per test run

## Expected Findings (hypotheses to validate with real runs)

| Metric | Selenium 4 (current) | Playwright (target) | Expected delta |
| -------- | ---------------------- | --------------------- | ----------------- |
| Suite time (serial) | baseline X s | TBD | TBD |
| Suite time (parallel, max 4) | TBD | TBD | ≥20% faster |
| Flakiness rate | TBD | TBD | ≥30% reduction |
| Locator edits/100 runs | TBD | TBD | ≤5% (role‑first ladder) |
| CI resource (CPU) | TBD | TBD | Comparable or lower |

## Instrumentation

- **Maven Surefire reports** (`target/surefire-reports/*.txt`) for per‑test timing
- **Git diff analysis** on `**/pageobjects/**` and `**/*Page.java` for locator churn
- **CI build logs** for resource usage (CPU, memory)

## Validation Protocol

1. **Baseline run** – Execute current Selenium 4 suite 5× on CI. Record mean wall‑clock, per‑test timing, flaky failures.
2. **Port run** – Port same flows to Playwright Java. Execute 5× under identical conditions.
3. **Compare** – Compute delta per metric. Migration justified if Playwright shows ≥20% faster suite time or ≥30% lower flakiness without increasing maintenance.

## Risks to Validity

- Different staging environment between runs → use same env, same time of day
- Browser version drift → pin browser versions (chromium stable) in both suites
- Test count mismatch → port 1:1, no new tests during benchmark

**Sources:** 03-dossiers/playwright-java-api.md (APIRequestContext), 03-dossiers/locator-best-practices.md (locator ladder), 03-dossiers/junit-6-maintained.md (JUnit 6 status), 03-dossiers/performance-comparison.md (benchmark design).
