package com.demo.legacy.helpers;
import com.demo.legacy.pages.LoginPage;
public class AuthFlow {
  private LoginPage loginPage;
  public AuthFlow(LoginPage lp) { this.loginPage = lp; }
  public void login(String u, String p) {
    loginPage.login(u, p);
  }
}
