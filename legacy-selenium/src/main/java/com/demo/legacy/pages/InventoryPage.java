package com.demo.legacy.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

public class InventoryPage {
  private WebDriver driver;
  private WebDriverWait wait;

  public InventoryPage(WebDriver d) {
    this.driver = d;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void sortByPriceHighToLow() {
    WebElement sortDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//select[@class='product_sort_container']")));
    new Select(sortDropdown).selectByVisibleText("Price (high to low)");
  }

  public void addTopItems(int count) throws Exception {
    for (int i = 0; i < count; i++) {
      List<WebElement> items = driver.findElements(By.xpath("//div[contains(@class,'inventory_item')]"));
      if (i < items.size()) {
        WebElement item = wait.until(ExpectedConditions.elementToBeClickable(items.get(i)));
        item.click();
      }
    }
  }

  public void clickFourthItem() {
    List<WebElement> items = driver.findElements(By.xpath("//div[contains(@class,'inventory_item_name')]"));
    if (items.size() > 3) {
      WebElement item = wait.until(ExpectedConditions.elementToBeClickable(items.get(3)));
      item.click();
    }
  }

  public int getCartBadgeCount() {
    try {
      String text = driver.findElement(By.xpath("//span[contains(@class,'shopping_cart_badge')]")).getText();
      return Integer.parseInt(text);
    } catch (Exception e) {
      return 0;
    }
  }

  public void addItemToCart() {
    WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class,'btn_inventory')]")));
    addToCartBtn.click();
  }

  public void backToProducts() {
    WebElement backBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class,'inventory_details_back_button')]")));
    backBtn.click();
  }

  public void goToCart() {
    WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@class,'shopping_cart_link')]")));
    cartLink.click();
  }
}