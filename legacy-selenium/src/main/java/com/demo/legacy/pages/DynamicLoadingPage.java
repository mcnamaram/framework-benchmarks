package com.demo.legacy.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DynamicLoadingPage {
  private WebDriver driver;
  public DynamicLoadingPage(WebDriver d) { this.driver = d; }
  public void navigate() {
    driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
    new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.titleContains("The Internet"));
  }
  public void clickStart() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='start']//button"))).click();
  }
  public String getFinishText() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='finish']")));
    return driver.findElement(By.xpath("//div[@id='finish']")).getText();
  }
}