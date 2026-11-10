package com.demo.modern.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CheckoutPage {
  private final Page page;

  public CheckoutPage(Page page) {
    this.page = page;
  }

  public Locator proceedToCheckout() {
    return page.locator(".btn_action");
  }

  public Locator firstNameField() {
    return page.getByLabel("first name");
  }

  public Locator lastNameField() {
    return page.getByLabel("last name");
  }

  public Locator postalCodeField() {
    return page.getByLabel("postal code");
  }

  public Locator continueButton() {
    return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
      new Page.GetByRoleOptions().setName("Continue"));
  }

  public Locator finishButton() {
    return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
      new Page.GetByRoleOptions().setName("Finish"));
  }

  public Locator backToProductsButton() {
    return page.getByText("Back Home");
  }

  public Locator summaryTotalLabel() {
    return page.locator(".summary_total_label");
  }

  public Locator completeHeader() {
    return page.getByText("Thank you for your order");
  }
}