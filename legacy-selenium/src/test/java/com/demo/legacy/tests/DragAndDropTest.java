package com.demo.legacy.tests;
import com.demo.legacy.pages.DragAndDropPage;
import com.demo.legacy.util.TestResultCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DragAndDropTest {
  private WebDriver driver;
  private DragAndDropPage page;
  private TestResultCollector collector;

  @Before
  public void setUp() {
    ChromeOptions options = new ChromeOptions();
    driver = new ChromeDriver(options);
    driver.manage().window().maximize();
    page = new DragAndDropPage(driver);
    collector = new TestResultCollector();
  }

  @Test
  public void dragAndDrop() {
    try {
      page.navigate();
      collector.addSuccess("Navigated to drag and drop page");
    } catch (Exception e) {
      collector.addFailure("Navigate: " + e.getMessage());
    }
    try {
      page.dragAndDrop();
      collector.addSuccess("Drag and drop executed");
    } catch (Exception e) {
      collector.addFailure("Drag and drop: " + e.getMessage());
    }
    try {
      String colA = page.getColumnAHeader();
      if (colA != null && !colA.isEmpty()) collector.addSuccess("Column A header: " + colA);
      else collector.addFailure("Column A header missing");
    } catch (Exception e) {
      collector.addFailure("Column A header: " + e.getMessage());
    }
    try {
      String colB = page.getColumnBHeader();
      if (colB != null && !colB.isEmpty()) collector.addSuccess("Column B header: " + colB);
      else collector.addFailure("Column B header missing");
    } catch (Exception e) {
      collector.addFailure("Column B header: " + e.getMessage());
    }
    collector.report();

    // Fail the test if any steps collected failures
    if (!collector.getFailures().isEmpty()) {
      throw new AssertionError(
        "DragAndDropTest had " + collector.getFailures().size() + " failures:\n" +
        String.join("\n", collector.getFailures())
      );
    }
  }

  @After
  public void tearDown() {
    if (driver != null) driver.quit();
  }
}