package com.demo.modern.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CartPage {
  private final Page page;

  public CartPage(Page page) {
    this.page = page;
  }

  public void removeSecondItem() {
    page.locator(".cart_item").nth(1).locator(".btn_secondary").click();
  }

  public Locator itemLocator() {
    return page.locator(".cart_item");
  }
}