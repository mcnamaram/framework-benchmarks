# Automation Framework Benchmark Project

**Purpose:** Evaluate migrating from Selenium 4 + JUnit 4 to Playwright + Java + JUnit 6 for a Java‑based automation framework using Maven.

**Structure:**  

- **01-summary/findings.md** – high‑level verdict and key claims  
- **02-analysis/** – detailed migration plan and performance benchmark design  
- **03-dossiers/** – source excerpts, best‑practice guides, maintenance status  

**Next steps:**  

1. Set up a fresh Maven project with the Junit 4 and selenium framework.
2. Implement a PoM model based test suite using xPath selectors and wrapping curl for API requests.
3. Set up a fresh Maven project with Playwright Java bindings.  
4. Port the same test flows covered by Selenium to Playwright Java.  
5. Run the baseline Selenium 4 suite (record times, flakiness).  
6. Execute the Playwright suite for performance benchmark and compare metrics.
