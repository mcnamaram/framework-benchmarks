package com.demo.modern.pages;

import com.microsoft.playwright.Page;

public class DynamicLoadingPage {
  private final Page page;

  public DynamicLoadingPage(Page page) {
    this.page = page;
  }

  public void navigate() {
    page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");
  }

  public void clickStart() {
    page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
      new Page.GetByRoleOptions().setName("Start")).click();
  }

  public String getFinishText() {
    return page.locator("#finish").textContent();
  }
}
