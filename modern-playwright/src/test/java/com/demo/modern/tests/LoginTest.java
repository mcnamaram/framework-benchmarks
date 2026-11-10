package com.demo.modern.tests;

import com.demo.modern.pages.LoginPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {
  private Page page;
  private Browser browser;
  private Playwright playwright;
  private LoginPage loginPage;

  @BeforeEach
  public void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    page = browser.newPage();
    loginPage = new LoginPage(page);
  }

  @AfterEach
  public void tearDown() {
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @Test
    public void loginSuccess() {
      // Step 1: Navigate to login page (Sauce Demo)
      loginPage.navigate();
    
      // Step 2: Enter Sauce Demo credentials
      loginPage.login("standard_user", "secret_sauce");
    
      // Step 3: Verify page title (Swag Labs / Products)
      String title = page.title();
      assertTrue(title.contains("Swag Labs") || title.contains("Products"),
        "Expected title to contain 'Swag Labs' or 'Products', but was: " + title);
    }
}