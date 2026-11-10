package com.demo.legacy.tests;

import com.demo.legacy.pages.JavaScriptAlertsPage;
import com.demo.legacy.util.TestResultCollector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class JavaScriptAlertsTest {
  private WebDriver driver;
  private JavaScriptAlertsPage page;
  private TestResultCollector collector;

  @Before
  public void setUp() {
    ChromeOptions options = new ChromeOptions();
    driver = new ChromeDriver(options);
    driver.manage().window().maximize();
    page = new JavaScriptAlertsPage(driver);
    collector = new TestResultCollector();
  }

  @After
  public void tearDown() {
    if (driver != null) driver.quit();
  }

  @Test
  public void jsAlert() {
    try {
      // Run test steps once
      runTestSteps();
    } catch (Throwable t) {
      collector.addFailure("Test failed: " + t.getMessage());
      collector.report();
      throw new AssertionError("Test failed", t);
    }
    collector.report();
  }

  private void runTestSteps() throws Exception {
    try {
      page.navigate();
      collector.addSuccess("Navigated to JavaScript alerts page");
    } catch (Exception e) {
      collector.addFailure("Navigate: " + e.getMessage());
      throw e;
    }
    try {
      page.clickFirstAlert();
      collector.addSuccess("Clicked first alert button");
    } catch (Exception e) {
      collector.addFailure("Click first alert: " + e.getMessage());
      throw e;
    }
    try {
      // Accept alert AFTER click (alert is now present)
      page.acceptAlert();
      collector.addSuccess("Accepted alert");
    } catch (Exception e) {
      collector.addFailure("Accept alert: " + e.getMessage());
      throw e;
    }
    try {
      String result = page.getResultText();
      if (result != null && result.contains("successfully")) {
        collector.addSuccess("Result text: " + result);
      } else {
        collector.addFailure("Result text check failed: " + result);
        throw new AssertionError("Result text not as expected: " + result);
      }
    } catch (Exception e) {
      collector.addFailure("Get result text: " + e.getMessage());
      throw e;
    }
  }
}