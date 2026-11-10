# Dossier: Playwright Locator Best Practices & Anti-XPath

**Sources:** Playwright docs (`https://playwright.dev/docs/best-practices`), `https://dev.to/zhilin_yang/stop-treating-locators-as-strings-a-more-resilient-locator-strategy-in-playwright-3781`, `https://deviqa.com/blog/stable-playwright-selectors-a-decision-framework-for-eliminating-flaky-tests` — retrieved 2026-10-29.

## Locator Ladder (highest to lowest resilience)

1. `getByRole()` — accessibility tree, survives CSS refactors
2. `getByLabel()` — form inputs with labels
3. `getByText()` — visible copy
4. `getByTestId()` — explicit test contracts
5. CSS selectors — structural fallback
6. XPath — last resort, brittle to DOM changes

## Key Fact

Playwright locators are lazy and auto‑wait for attached/visible/stable/enabled states. No manual `Thread.sleep()` needed. XPath only answers "where is it now?"; role‑based locators answer "what does this represent?"
