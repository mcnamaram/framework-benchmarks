package com.demo.modern.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class InventoryPage {
  private final Page page;

  public InventoryPage(Page page) {
    this.page = page;
  }

  public void sortByPriceHighToLow() {
    page.selectOption(".product_sort_container", "Price (high to low)");
  }

  public void addTopItems(int count) {
    for (int i = 0; i < count; i++) {
      page.locator(".inventory_item").nth(i).locator(".btn_inventory").click();
    }
  }

  public void clickFourthItem() {
    page.locator(".inventory_item_name").nth(3).click();
  }

  public Locator getCartBadge() {
    return page.locator(".shopping_cart_badge");
  }

  public void addItemToCart() {
    page.getByTestId("add-to-cart").click();
  }

  public void backToProducts() {
    page.getByTestId("back-to-products").click();
  }
}