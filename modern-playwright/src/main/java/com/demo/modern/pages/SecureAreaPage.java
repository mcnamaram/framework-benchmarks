package com.demo.modern.pages;

import com.microsoft.playwright.Page;

public class SecureAreaPage {
  private final Page page;

  public SecureAreaPage(Page page) {
    this.page = page;
  }

  public boolean isSecureAreaVisible() {
    return page.getByText("Secure Area").isVisible()
      || page.title().contains("Products");
  }
}