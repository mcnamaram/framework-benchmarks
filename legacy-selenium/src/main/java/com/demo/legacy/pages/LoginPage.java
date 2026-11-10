package com.demo.legacy.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
  private WebDriver driver;

  public LoginPage(WebDriver d) { this.driver = d; }

  public void navigate() { driver.get("https://www.saucedemo.com/"); }

  public void login(String u, String p) {
    driver.findElement(By.xpath("//input[@id='user-name']")).sendKeys(u);
    driver.findElement(By.xpath("//input[@id='password']")).sendKeys(p);
    driver.findElement(By.xpath("//input[@id='login-button']")).click();
  }
}