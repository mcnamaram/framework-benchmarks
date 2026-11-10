package com.demo.legacy.tests;
import com.demo.legacy.pages.FileUploadPage;
import com.demo.legacy.util.TestResultCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUploadTest {
  private WebDriver driver;
  private FileUploadPage page;
  private TestResultCollector collector;

  @Before
  public void setUp() {
    ChromeOptions options = new ChromeOptions();
    driver = new ChromeDriver(options);
    driver.manage().window().maximize();
    page = new FileUploadPage(driver);
    collector = new TestResultCollector();
  }

  @Test
  public void fileUpload() throws Exception {
    try {
      page.navigate();
      collector.addSuccess("Navigated to file upload page");
    } catch (Exception e) {
      collector.addFailure("Navigate: " + e.getMessage());
    }
    try {
      Path uploadFile = Files.createTempFile("test-upload", ".txt");
      Files.write(uploadFile, "Test content".getBytes());
      page.uploadFile(uploadFile);
      collector.addSuccess("Uploaded test file");
    } catch (Exception e) {
      collector.addFailure("Upload file: " + e.getMessage());
    }
    try {
      page.clickUpload();
      collector.addSuccess("Clicked upload button");
    } catch (Exception e) {
      collector.addFailure("Click upload: " + e.getMessage());
    }
    try {
      String uploaded = page.getUploadedFileName();
      if (uploaded != null && uploaded.contains("test-upload")) collector.addSuccess("Uploaded file name: " + uploaded);
      else collector.addFailure("Uploaded file name check failed");
    } catch (Exception e) {
      collector.addFailure("Get uploaded file name: " + e.getMessage());
    }
    collector.report();

    // Fail the test if any steps collected failures
    if (!collector.getFailures().isEmpty()) {
      throw new AssertionError(
        "FileUploadTest had " + collector.getFailures().size() + " failures:\n" +
        String.join("\n", collector.getFailures())
      );
    }
  }

  @After
  public void tearDown() {
    if (driver != null) driver.quit();
  }
}