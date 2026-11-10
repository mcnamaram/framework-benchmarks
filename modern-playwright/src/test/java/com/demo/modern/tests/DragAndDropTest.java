package com.demo.modern.tests;

import com.demo.modern.pages.DragAndDropPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DragAndDropTest {
  private Page page;
  private Browser browser;
  private Playwright playwright;
  private DragAndDropPage dragAndDropPage;

  @BeforeEach
  public void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    page = browser.newPage();
    dragAndDropPage = new DragAndDropPage(page);
  }

  @AfterEach
  public void tearDown() {
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @Test
  public void dragAndDrop() {
    dragAndDropPage.navigate();
    assertTrue(page.title().contains("The Internet"),
      "Expected title 'The Internet', got: " + page.title());

    // Drag column-a to column-b using CSS id selectors
    dragAndDropPage.dragColumnAtoColumnB();

    // Verify columns exist after drag
    String colAText = dragAndDropPage.getColumnAText();
    String colBText = dragAndDropPage.getColumnBText();

    assertTrue(colAText != null && !colAText.isEmpty(),
      "Column A content should be visible");
    assertTrue(colBText != null && !colBText.isEmpty(),
      "Column B content should be visible");
  }
}