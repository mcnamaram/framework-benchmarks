# Side-by-Side Demo Project Design Plan

**Goal:** Build two small, runnable projects that implement the *same workflows* against the *same public test website*, one using the legacy framework patterns (Selenium 4 + JUnit 4 + XPath-heavy + abstracted assertions + curl API wrappers) and one using the proposed modern stack (Playwright + Java + JUnit 6 + Page Object Model + inline assertions + Playwright `APIRequestContext`). Capture metrics to demonstrate differences in performance, maintainability, and debuggability for a business demo.

**Verified facts (sources in section 11)**

* JUnit 6 is GA (6.0.0 released Sep 30, 2025; latest patch 6.0.2 Jan 6, 2026; Java 17 baseline; unified versioning). JUnit 4 is legacy.
* Playwright Java bindings officially support Chromium, Firefox, and WebKit; no IE 11 support. Chrome/Edge/Firefox cover our matrix.
* <https://the-internet.herokuapp.com/> is the Sauce Labs reference app for writing automated acceptance tests — stable, free, and includes login, dynamic loading, JavaScript alerts, drag-and-drop, file upload/download, status codes, frames, etc.
* Public API target: <https://jsonplaceholder.typicode.com/> (REST endpoints for GET/POST/PUT/DELETE).

---

## 1. Project layout (two sibling Maven modules under `~/dev/framework-benchmarks/`)

```sh
framework-benchmarks/
├── 02-analysis/
│   └── demo-projects-plan.md          <- this document
├── 03-dossiers/
│   └── ...                            <- existing source excerpts
├── legacy-selenium/                   <- OLD framework patterns
│   ├── pom.xml                        <- Selenium 4 + JUnit 4
│   └── src/
│       ├── main/java/com/demo/legacy/
│       │   ├── pages/                 <- Page Object Model
│       │   │   ├── LoginPage.java     <- XPath selectors
│       │   │   ├── SecureAreaPage.java
│       │   │   ├── DynamicLoadingPage.java
│       │   │   ├── JavaScriptAlertsPage.java
│       │   │   ├── DragAndDropPage.java
│       │   │   └── FileUploadPage.java
│       │   ├── helpers/               <- operations ONLY (login, nav, data setup)
│       │   │   ├── AuthFlow.java      <- login helper (no assertions)
│       │   │   ├── Navigation.java    <- navigation helper
│       │   │   └── DataSetup.java     <- test-data helper
│       │   ├── assertions/            <- ABSTRACTED assertions (the legacy anti-pattern)
│       │   │   └── Verify.java        <- helper methods tests call instead of inline expect
│       │   └── api/                   <- curl wrappers
│       │       └── CurlApiClient.java <- Runtime.exec ProcessBuilder curl calls
│       └── resources/
│           └── log4j2.xml
│       └── test/java/com/demo/legacy/tests/
│           ├── LoginTest.java         <- calls AuthFlow + Verify.assertLoginSuccess
│           ├── DynamicLoadingTest.java<- uses Thread.sleep / explicit waits
│           ├── JavaScriptAlertsTest.java
│           ├── DragAndDropTest.java
│           ├── FileUploadTest.java
│           └── ApiClientTest.java     <- curl-based GET/POST
└── modern-playwright/                 <- NEW framework patterns
    ├── pom.xml                        <- Playwright Java + JUnit 6 (6.0.2)
    └── src/
        ├── main/java/com/demo/modern/
        │   ├── pages/                 <- Page Object Model
        │   │   ├── LoginPage.java     <- Playwright locators (getByText, locator(css))
        │   │   ├── SecureAreaPage.java
        │   │   ├── DynamicLoadingPage.java
        │   │   ├── JavaScriptAlertsPage.java
        │   │   ├── DragAndDropPage.java
        │   │   └── FileUploadPage.java
        │   ├── base/                  <- BasePage (browser/context/page fixtures)
        │   │   └── BasePageTest.java
        │   └── api/                   <- Playwright APIRequestContext (no curl)
        │       └── ApiClientService.java
        └── test/java/com/demo/modern/tests/
            ├── LoginTest.java         <- inline assertions inside test
            ├── DynamicLoadingTest.java<- auto-wait, no Thread.sleep
            ├── JavaScriptAlertsTest.java
            ├── DragAndDropTest.java
            ├── FileUploadTest.java
            └── ApiClientServiceTest.java <- APIRequestContext GET/POST
```

---

## 2. Test coverage matrix (identical workflows in both projects)

Each scenario runs against `https://the-internet.herokuapp.com/` unless noted.

| ID | Workflow | Page / Endpoint | Purpose | Contrast highlight |
| ---- | ---------- | ----------------- | --------- | -------------------- |
| T1 | Form authentication (login) | `/login` | Secure login with valid creds `tomsmith / SuperSecretPassword!`; verify secure-area header + flash message | Legacy: helper `AuthFlow.login()` + `Verify.assertLoginSuccess()` → assertion buried. Modern: inline `expect(page.locator(...)).toContainText(...)` inside test. |
| T2 | Dynamic loading / waiting | `/dynamic_loading/1` | Click "Start", wait for "Hello World!" rendered after ~2s | Legacy: `Thread.sleep` / `WebDriverWait` boilerplate. Modern: Playwright auto-wait / locator `toBeVisible` — no manual sleeps. |
| T3 | JavaScript alerts | `/javascript_alerts` | Click "Click for JS Alert", accept, verify result text | Legacy: `driver.switchTo().alert().accept()`. Modern: `page.on("dialog", dialog -> dialog.accept())`. |
| T4 | Drag and drop | `/drag_and_drop` | Drag element A onto B; verify drop message | Legacy: `Actions.dragAndDrop`. Modern: `locator.dragTo(locator)`. |
| T5 | File upload | `/upload` | Choose file, click Upload, verify filename header | Legacy: `WebElement.sendKeys(path)` + manual wait. Modern: `locator.setInputFiles(path)` + auto-wait. |
| T6 | Status codes / redirects | `/status_codes/200` | Navigate to 200 page; verify "200" content | Shows browser-navigation parity. |
| A1 | API GET | `https://jsonplaceholder.typicode.com/posts/1` | Fetch post 1; assert `userId == 1` | Legacy: `curl` via `ProcessBuilder`, parse `org.json`. Modern: `APIRequestContext.get()` + `expect(response).toBeOK()` + JSON body assertion. |
| A2 | API POST | `https://jsonplaceholder.typicode.com/posts` | Create post; assert status 201 + returned id | Same contrast as A1 with write path. |

**Total:** 6 UI tests + 2 API tests per project = 8 tests each, covering login, waits, dialogs, interactions, file I/O, navigation, and network API — enough surface area to show performance and debuggability differences.

---

## 3. Side-by-side code contrasts (what the demo will show)

### 3.1 Login — helper vs inline assertion

**Legacy (test contains NO assertion):**

```java
@Test
public void loginSuccess() {
    AuthFlow.login(driver, "tomsmith", "SuperSecretPassword!");
    // assertion is hidden inside AuthFlow or Verify helper —
    // to see why this failed you must open another class
}
```

**Modern (assertion in test):**

```java
@Test
void loginSuccess(Page page) {
    loginPage.navigate();
    loginPage.login("tomsmith", "SuperSecretPassword!");
    expect(page.locator("div.flash.success")).toContainText("You logged into a secure area!");
    expect(page.locator("h2")).toHaveText("Secure Area");
}
```

### 3.2 Dynamic loading — explicit waits vs auto-wait

**Legacy:**

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("." + selector)));
```

**Modern:**

```java
await page.locator("#finish").toBeVisible(); // Playwright auto-waits, polls, retries
```

### 3.3 API — curl wrapper vs APIRequestContext

**Legacy:**

```java
ProcessBuilder process = new ProcessBuilder("curl", "-s", "-X", request.getRestRequestMethod(), "-d", body, "-i", "https://jsonplaceholder.typicode.com/posts/1");
// build a process for get method without body if request.getRestRequestMethod() returns "GET"
// read via BufferedReader using while((line = reader.readLine()) != null), parse into ApiResponse model using org.json, assert manually
```

**Modern:**

```java
APIResponse response = requestContext.get("https://jsonplaceholder.typicode.com/posts/1");
expect(response).toBeOK();
expect(response.json().getInt("userId")).toBe(1);
```

### 3.4 Boilerplate

**Legacy:**

```java
// have to import driver setup and wrapper annotations to initialize web interaction
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumJunit4Test {

    private WebDriver driver;

    @Before
    public void setUp() {
        // Initialize the WebDriver (Chrome in this example)
        // Note: WebDriverManager or Selenium 4 Manager handles driver binaries automatically
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testGoogleSearch() {
        // Navigate to the web page
        driver.get("https://www.google.com");

        // Interact with elements
        WebElement searchBox = driver.findElement(By.xpath("//div[@class='q']"));
        searchBox.sendKeys("JUnit 4 Selenium");
        searchBox.submit();

        // Verify outcomes using JUnit assertions
        String title = driver.getTitle();
        // Simple assertion to verify title contains the search query
        assertEquals(true, title.contains("JUnit 4 Selenium"));
    }

    @After
    public void tearDown() {
        // Close the browser window and destroy the WebDriver instance instance safely
        if (driver != null) {
            driver.quit();
        }
    }
}
```

**Modern:**

```java
// @UsePlaywright should be all we need generally
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UsePlaywright
public class TestExample {
  @Test
  void shouldClickButton(Page page) {
    page.navigate("data:text/html,<script>var result;</script><button onclick='result=\"Clicked\"'>Go</button>");
    page.locator("button").click();
    assertEquals("Clicked", page.evaluate("result"));
  }

  @Test
  void shouldCheckTheBox(Page page) {
    page.setContent("<input id='checkbox' type='checkbox'></input>");
    page.locator("input").check();
    assertEquals(true, page.evaluate("window['checkbox'].checked"));
  }

  @Test
  void shouldSearchWiki(Page page) {
    page.navigate("https://www.wikipedia.org/");
    page.locator("input[name=\"search\"]").click();
    page.locator("input[name=\"search\"]").fill("playwright");
    page.locator("input[name=\"search\"]").press("Enter");
    assertThat(page).hasURL("https://en.wikipedia.org/wiki/Playwright");
  }
}
```

---

## 4. Maven dependencies

### legacy-selenium/pom.xml

* Java 8+ source/target
* `org.seleniumhq.selenium:selenium-java:4.35.0`
* `junit:junit:4.13.2`
* `org.json:json:20250517` (curl response parsing)
* Surefire plugin configured for JUnit 4

### modern-playwright/pom.xml

* Java 25 source/target (required by JUnit 6)
* `com.microsoft.playwright:playwright:1.64.0` (Java bindings)
* `org.junit.jupiter:junit-jupiter:6.0.2` (JUnit 6)
* Surefire plugin configured for JUnit Platform / JUnit 6
* Lombok

---

## 5. Metrics collection plan (captured during demo runs)

| Metric | How measured | Legacy expected | Modern expected |
| -------- | -------------- | ----------------- | ----------------- |
| Suite wall-clock time | `mvn test` total elapsed | Baseline (slower due to `Thread.sleep`, explicit waits, process-per-curl) | Faster (auto-wait, parallel browser contexts, native API calls) |
| Per-test duration | Surefire surefire-reports/*.txt | Higher variance | Lower variance, tighter clustering |
| LOC (excluding blanks/comments) | `cloc` or `find ... -name '*.java' \| xargs wc -l` | Higher (boilerplate waits, curl parsing, helper indirection) | Lower (concise locators, inline assertions, built-in API) |
| Abstraction layers per test | Count of helper classes a test depends on | 2–3 (AuthFlow, Verify, CurlApiClient) | 0–1 (page object only; assertions inline) |
| Flakiness over 5 repeated runs | Pass/fail count per scenario | Higher (sleep-based timing, XPath brittleness) | Lower (auto-wait, stable locators) |
| Debug evidence on failure | Screenshots / trace artifacts count | Manual screenshots only | Playwright trace viewer + screenshots auto-attached |
| CI resource usage | Peak CPU/RAM during `mvn test` (via `/usr/bin/time -v` or CI metrics) | Higher (WebDriver process per test) | Lower (browser contexts share one browser instance) |

**Data collection method:**

1. Run each suite 5 times on the same machine.
2. Capture `target/surefire-reports/*.txt` for timing.
3. Capture `target/playwright-trace/trace.zip` (modern) vs manual `target/screenshots/*.png` (legacy).
4. Run `cloc` on both `src/` trees.
5. Record CI resource usage via `time` or GitHub Actions job logs.

---

## 6. Step-by-step build instructions (to reproduce the demo)

### Prerequisites

* macOS (per user environment)
* Maven 3.10+
* Java 25 JDK
* Internet access (public sites are external; no local server needed)

### Phase 1 — Scaffold modules

```
cd ~/dev/framework-benchmarks
mkdir -p legacy-selenium/src/main/java/com/demo/legacy/{pages,helpers,assertions,api,resources}
mkdir -p legacy-selenium/src/test/java/com/demo/legacy/tests
mkdir -p modern-playwright/src/main/java/com/demo/modern/{pages,base,api}
mkdir -p modern-playwright/src/test/java/com/demo/modern/tests
```

### Phase 2 — Write legacy-selenium/pom.xml

```xml
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.demo</groupId>
  <artifactId>legacy-selenium</artifactId>
  <version>1.0.0</version>
  <properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
    <selenium.version>4.35.0</selenium.version>
  </properties>
  <dependencies>
    <dependency>
      <groupId>org.seleniumhq.selenium</groupId>
      <artifactId>selenium-java</artifactId>
      <version>${selenium.version}</version>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.13.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.json</groupId>
      <artifactId>json</artifactId>
      <version>20250517</version>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.5.1</version>
      </plugin>
    </plugins>
  </build>
</project>
```

### Phase 3 — Write modern-playwright/pom.xml

```xml
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.demo</groupId>
  <artifactId>modern-playwright</artifactId>
  <version>1.0.0</version>
  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <playwright.version>1.64.0</playwright.version>
    <junit.version>6.0.2</junit.version>
  </properties>
  <dependencies>
    <dependency>
      <groupId>com.microsoft.playwright</groupId>
      <artifactId>playwright</artifactId>
      <version>${playwright.version}</version>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${junit.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.5.1</version>
        <configuration>
          <includes>
            <include>**/*Test.java</include>
          </includes>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

### Phase 4 — Implement shared page objects

* Write `LoginPage`, `SecureAreaPage`, `DynamicLoadingPage`, `JavaScriptAlertsPage`, `DragAndDropPage`, `FileUploadPage` in both modules using each framework's idioms (XPath vs Playwright locators).
* Legacy `AuthFlow` / `Verify` / `CurlApiClient` helpers.
* Modern `BasePageTest` fixture launching `BrowserType.chromium().launch()` + `browser.newContext()` + `page`.
* Modern `ApiClientService` using `APIRequestContext`.

### Phase 5 — Implement identical test classes (8 per module)

* Map T1–T6 and A1–A2 to same-named test classes in both modules.
* Keep legacy assertions inside `Verify` helpers; keep modern assertions inline inside `@Test` methods.

### Phase 6 — Run both suites and collect metrics

```
# Legacy
cd legacy-selenium && mvn clean test && cp -r target/surefire-reports ../metrics/legacy-run-1
# Modern
cd modern-playwright && mvn clean test && cp -r target/surefire-reports ../metrics/modern-run-1
```

Repeat 5x; capture timing, LOC (`cloc src/`), and failure artifacts.

### Phase 7 — Produce demo output

* Combined markdown report: `02-analysis/demo-metrics-report.md`
* Side-by-side code snippets (from section 3) pasted into a slide deck or README.
* Playwright trace zip + legacy screenshots attached as evidence.

---

## 7. Demo script for the business (5–10 minutes)

1. **Context (1 min):** Show current legacy stack diagram (Selenium 4, JUnit 4, XPath, abstracted assertions, curl wrappers).
2. **Live run — legacy (2 min):** Run one UI test + one API test; show execution time, then intentionally break a locator (change XPath) and show how long it takes to find the failure cause across helper classes.
3. **Live run — modern (2 min):** Run the same two tests in Playwright; show faster execution, inline assertion failure message in console, and open the Playwright trace viewer.
4. **Metrics slide (2 min):** Compare suite duration, LOC, abstraction layers, flakiness over 5 runs, CI resources.
5. **Recommendation (2 min):** Propose pilot migration of one feature team using Playwright + Java + JUnit 6, keeping assertions in tests and abstracting only login/navigation/data setup.

---

## 8. Risks & mitigations

| Risk | Mitigation |
| ------ | ------------ |
| External site changes break tests | Pin to stable paths on the-internet; add retry logic; document that tests are smoke/demo only |
| JUnit 6 requires Java 17+ | Confirm CI agents have JDK 17+ |
| Playwright browser binaries not downloaded | `mvn test` triggers Playwright download automatically; pre-bake browsers in CI image |
| curl removal breaks API tests | Modern module has zero curl; legacy keeps curl only to demonstrate the anti-pattern being removed |

---

## 9. Source URLs & citations

* JUnit 6 release notes & status: <https://docs.junit.org/6.0.1/release-notes.html> — GA Sep 30, 2025; Java 17 baseline; 6.0.2 patch Jan 6, 2026.
* Migration guide (JUnit 5 → 6): <https://github.com/junit-team/junit-framework/wiki/Upgrading-to-JUnit-6.0>
* Playwright Java API testing (`APIRequestContext`): <https://playwright.dev/java/docs/api-testing>
* Playwright best practices (locators, auto-waiting): <https://playwright.dev/docs/best-practices>
* Public test application: <https://the-internet.herokuapp.com/> (Sauce Labs; GitHub: <https://github.com/saucelabs/the-internet>)
* Public JSON API: <https://jsonplaceholder.typicode.com/>
* Comparative performance data (Playwright vs Selenium speed/resource): Markaicode 2025 benchmarks; A comparative analysis of web application test automation tools (doi.org/10.35784/jcsi.7119)

---

## 10. Next steps (after business sign-off)

1. Approve browser matrix (Chrome / Edge / Firefox; drop IE 11 — confirmed unsupported by Playwright).
2. Create the two module repos under `~/dev/framework-benchmarks/`.
3. Implement Phase 1–6 above.
4. Schedule a 30-minute business demo using the script in Section 7.
5. If approved, scope a pilot migration (one feature team, 2–4 weeks).

---

## 11. Artifact pyramid mapping

* L1 verdict: `01-summary/findings.md` (already written — migrate to Playwright + Java + JUnit 6)
* L2 analysis: this file + `02-analysis/framework-migration.md` + `02-analysis/performance-comparison.md`
* L3 dossiers: `03-dossiers/playwright-java-api.md`, `03-dossiers/locator-best-practices.md`, `03-dossiers/junit-6-maintained.md`
