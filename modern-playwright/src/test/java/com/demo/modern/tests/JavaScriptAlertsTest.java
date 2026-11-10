package com.demo.modern.tests;

import com.demo.modern.pages.JavaScriptAlertsPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JavaScriptAlertsTest {
  private Page page;
  private Browser browser;
  private Playwright playwright;
  private JavaScriptAlertsPage alertsPage;

  @BeforeEach
  public void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    page = browser.newPage();
    alertsPage = new JavaScriptAlertsPage(page);
  }

  @AfterEach
  public void tearDown() {
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @Test
  public void jsAlert() {
    alertsPage.navigate();
    assertTrue(page.title().contains("The Internet"),
      "Expected title 'The Internet', got: " + page.title());

    // Register dialog listener BEFORE clicking (avoids race condition)
    alertsPage.acceptDialog();

    // Click first alert button (getByRole best practice, not XPath)
    alertsPage.clickJsAlert();

    // Verify result text
    String result = alertsPage.getResultText();
    assertTrue(result.contains("successfully"),
      "Result text should contain 'successfully', but got: " + result);
  }
}