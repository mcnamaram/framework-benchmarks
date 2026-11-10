package com.demo.modern.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage {
  private final Page page;

  public LoginPage(Page page) {
    this.page = page;
  }

  public void navigate() {
    page.navigate("https://www.saucedemo.com/");
  }

  public Locator usernameField() {
    return page.getByLabel("Username");
  }

  public Locator passwordField() {
    return page.getByLabel("Password");
  }

  public Locator loginButton() {
    return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
      new Page.GetByRoleOptions().setName("Login"));
  }

  public void login(String username, String password) {
    usernameField().fill(username);
    passwordField().fill(password);
    loginButton().click();
  }

  public boolean isLoggedIn() {
    return page.title().contains("Swag Labs");
  }
}