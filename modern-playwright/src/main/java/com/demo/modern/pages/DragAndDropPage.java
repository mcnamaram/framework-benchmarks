package com.demo.modern.pages;

import com.microsoft.playwright.Page;

public class DragAndDropPage {
  private final Page page;

  public DragAndDropPage(Page page) {
    this.page = page;
  }

  public void navigate() {
    page.navigate("https://the-internet.herokuapp.com/drag_and_drop");
  }

  public void dragColumnAtoColumnB() {
    page.locator("#column-a").dragTo(page.locator("#column-b"));
  }

  public String getColumnAText() {
    return page.locator("#column-a").textContent();
  }

  public String getColumnBText() {
    return page.locator("#column-b").textContent();
  }
}
