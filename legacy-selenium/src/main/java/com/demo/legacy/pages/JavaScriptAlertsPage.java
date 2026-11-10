package com.demo.legacy.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class JavaScriptAlertsPage {
  private WebDriver driver;
  public JavaScriptAlertsPage(WebDriver d) { this.driver = d; }
  public void navigate() {
    driver.get("https://the-internet.herokuapp.com/javascript_alerts");
    new WebDriverWait(driver, Duration.ofSeconds(120)).until(d -> {
      try {
        return d.findElement(By.xpath("//div[@id='content']//ul/li[1]/button")).isDisplayed();
      } catch (Exception e) { return false; }
    });
  }
  public void clickFirstAlert() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
    wait.until(d -> {
      try {
        d.findElement(By.xpath("//div[@id='content']//ul/li[1]/button")).click();
        return true;
      } catch (Exception e) { return false; }
    });
  }
  public void acceptAlert() {
    driver.switchTo().alert().accept();
  }
  public String getResultText() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
    wait.until(d -> {
      try {
        return d.findElement(By.id("result")).isDisplayed();
      } catch (Exception e) { return false; }
    });
    return driver.findElement(By.id("result")).getText();
  }
}