package com.demo.modern.pages;

import com.microsoft.playwright.Page;

public class JavaScriptAlertsPage {
  private final Page page;

  public JavaScriptAlertsPage(Page page) {
    this.page = page;
  }

  public void navigate() {
    page.navigate("https://the-internet.herokuapp.com/javascript_alerts");
  }

  public void clickJsAlert() {
    page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
      new Page.GetByRoleOptions().setName("Click for JS Alert")).click();
  }

  public void acceptDialog() {
    page.onDialog(dialog -> dialog.accept());
  }

  public String getResultText() {
    return page.locator("#result").textContent();
  }
}