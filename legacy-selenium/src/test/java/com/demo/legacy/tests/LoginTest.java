package com.demo.legacy.tests;

import com.demo.legacy.pages.LoginPage;
import com.demo.legacy.pages.SecureAreaPage;
import com.demo.legacy.util.TestResultCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoginTest {
  private WebDriver driver;
  private LoginPage loginPage;
  private SecureAreaPage secureAreaPage;
  private TestResultCollector collector;

  @Before
  public void setUp() {
    ChromeOptions options = new ChromeOptions();
    driver = new ChromeDriver(options);
    loginPage = new LoginPage(driver);
    secureAreaPage = new SecureAreaPage(driver);
    collector = new TestResultCollector();
  }

  @Test
  public void loginSuccess() {
    // Step 1: Navigate to login page
    try {
      loginPage.navigate();
      collector.addSuccess("Navigated to login page");
    } catch (Exception e) {
      collector.addFailure("Navigate to login page: " + e.getMessage());
    }

    // Step 2: Enter credentials
    try {
      loginPage.login("standard_user", "secret_sauce");
      collector.addSuccess("Login credentials submitted");
    } catch (Exception e) {
      collector.addFailure("Login credentials: " + e.getMessage());
    }

    // Step 3: Verify page title
    try {
      String title = driver.getTitle();
      if (title.contains("Swag Labs")) {
        collector.addSuccess("Login success page loaded (title: " + title + ")");
      } else {
        collector.addFailure("Login success page not loaded (title: " + title + ")");
      }
    } catch (Exception e) {
      collector.addFailure("Title check: " + e.getMessage());
    }

    // Step 4: Verify secure area is visible
    try {
      boolean visible = secureAreaPage.isVisible();
      if (visible) {
        collector.addSuccess("Secure area element is visible");
      } else {
        collector.addFailure("Secure area element not visible");
      }
    } catch (Exception e) {
      collector.addFailure("Secure area visibility check: " + e.getMessage());
    }

    collector.report();

    // Fail the test if any steps collected failures
    if (!collector.getFailures().isEmpty()) {
      throw new AssertionError(
        "LoginTest had " + collector.getFailures().size() + " failures:\n" +
        String.join("\n", collector.getFailures())
      );
    }
  }

  @After
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
}