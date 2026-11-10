package com.demo.legacy.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.List;
import org.openqa.selenium.WebElement;

public class CartPage {
  private WebDriver driver;

  public CartPage(WebDriver d) { this.driver = d; }

  public void removeSecondItem() {
    List<WebElement> cartItems = driver.findElements(By.xpath("//div[@class='cart_item']"));
    if (cartItems.size() > 1) {
      cartItems.get(1).findElement(By.xpath(".//button[contains(@class, 'btn_secondary')]")).click();
    }
  }

  public int getCartItemCount() {
    return driver.findElements(By.xpath("//div[@class='cart_item']")).size();
  }
}