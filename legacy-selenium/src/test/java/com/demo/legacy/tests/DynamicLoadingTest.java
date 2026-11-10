package com.demo.legacy.tests;
import com.demo.legacy.pages.DynamicLoadingPage;
import com.demo.legacy.util.TestResultCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DynamicLoadingTest {
  private WebDriver driver;
  private DynamicLoadingPage page;
  private TestResultCollector collector;

  @Before
  public void setUp() {
    ChromeOptions options = new ChromeOptions();
    driver = new ChromeDriver(options);
    driver.manage().window().maximize();
    page = new DynamicLoadingPage(driver);
    collector = new TestResultCollector();
  }

  @Test
  public void dynamicLoading() {
    try {
      page.navigate();
      collector.addSuccess("Navigated to dynamic loading page");
    } catch (Exception e) {
      collector.addFailure("Navigate: " + e.getMessage());
    }
    try {
      page.clickStart();
      collector.addSuccess("Clicked start button");
    } catch (Exception e) {
      collector.addFailure("Click start: " + e.getMessage());
    }
    try {
      String finish = page.getFinishText();
      if (finish.contains("Hello World!")) {
        collector.addSuccess("Finish text correct (" + finish + ")");
      } else {
        collector.addFailure("Finish text wrong (" + finish + ")");
      }
    } catch (Exception e) {
      collector.addFailure("Finish text: " + e.getMessage());
    }
    collector.report();

    // Fail the test if any steps collected failures
    if (!collector.getFailures().isEmpty()) {
      throw new AssertionError(
        "DynamicLoadingTest had " + collector.getFailures().size() + " failures:\n" +
        String.join("\n", collector.getFailures())
      );
    }
  }

  @After
  public void tearDown() {
    if (driver != null) driver.quit();
  }
}