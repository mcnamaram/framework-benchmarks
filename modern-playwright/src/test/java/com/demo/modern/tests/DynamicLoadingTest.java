package com.demo.modern.tests;

import com.demo.modern.pages.DynamicLoadingPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicLoadingTest {
  private Page page;
  private Browser browser;
  private Playwright playwright;
  private DynamicLoadingPage dynamicLoadingPage;

  @BeforeEach
  public void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    page = browser.newPage();
    dynamicLoadingPage = new DynamicLoadingPage(page);
  }

  @AfterEach
  public void tearDown() {
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @Test
  public void dynamicLoading() {
    dynamicLoadingPage.navigate();
    assertTrue(page.title().contains("The Internet"),
      "Expected title 'The Internet', got: " + page.title());

    // Click start button
    dynamicLoadingPage.clickStart();

    // Wait for finish text and verify
    String finishText = dynamicLoadingPage.getFinishText();
    assertTrue(finishText.contains("Hello World!"),
      "Expected finish text to contain 'Hello World!', but got: " + finishText);
  }
}