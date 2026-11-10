# Benchmark Metrics (In Progress)

## Status
- Project scaffolding complete (both Maven modules created)
- Analysis/dossier layers (L1-L3) already verified
- Implementation: core page objects written for both stacks
- Execution: pending compilation and 5x run cycle

## Key Design Decisions (locked from 02-analysis/demo-projects-plan.md)
1. Legacy = Selenium 4 + JUnit 4 + XPath + abstracted assertions + curl wrappers
2. Modern = Playwright 1.64 + JUnit 6 + role-label-text locator ladder + inline assertions + APIRequestContext
3. Benchmark site: the-internet.herokuapp.com (T1-T6) + jsonplaceholder.typicode.com (A1-A2)
4. Metrics: suite wall-clock, flakiness rate, LOC, abstraction layers, CI resources
