package com.demo.legacy.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class CheckoutPage {
  private WebDriver driver;
  private WebDriverWait wait;

  public CheckoutPage(WebDriver d) {
    this.driver = d;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void proceedToCheckout() {
    WebElement checkoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class,'btn_action')]")));
    checkoutBtn.click();
  }

  public void enterCheckoutInfo(String firstName, String lastName, String postalCode) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='first-name']")));
    driver.findElement(By.xpath("//input[@id='first-name']")).sendKeys(firstName);
    driver.findElement(By.xpath("//input[@id='last-name']")).sendKeys(lastName);
    driver.findElement(By.xpath("//input[@id='postal-code']")).sendKeys(postalCode);
    WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Continue']")));
    continueBtn.click();
  }

  public boolean isSummaryTotalVisible() {
    try {
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'summary_total_label')]")));
      return driver.findElement(By.xpath("//div[contains(@class,'summary_total_label')]")).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void finishOrder() {
    WebElement finishBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='finish'] | //button[@id='finish']")));
    finishBtn.click();
  }

  public boolean isOrderComplete() {
    try {
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[@id='complete-header']")));
      return driver.findElement(By.xpath("//h2[@id='complete-header']")).getText().contains("Thank you for your order");
    } catch (Exception e) {
      return false;
    }
  }

  public void backToProducts() {
    driver.findElement(By.xpath("//*[normalize-space()='Back Home']")).click();
  }
}