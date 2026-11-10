package com.demo.legacy.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;

public class DragAndDropPage {
  private WebDriver driver;
  public DragAndDropPage(WebDriver d) { this.driver = d; }
  public void navigate() {
    driver.get("https://the-internet.herokuapp.com/drag_and_drop");
    new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.titleContains("The Internet"));
  }
  public void dragAndDrop() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    wait.until(d -> d.findElement(By.id("column-a")));
    wait.until(d -> d.findElement(By.id("column-b")));
    WebElement source = driver.findElement(By.id("column-a"));
    WebElement target = driver.findElement(By.id("column-b"));
    new Actions(driver).dragAndDrop(source, target).perform();
  }
  public String getColumnAHeader() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    wait.until(d -> d.findElement(By.id("column-a")));
    return driver.findElement(By.id("column-a"))
      .findElement(By.xpath(".//*"))
      .getText();
  }
  public String getColumnBHeader() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    wait.until(d -> d.findElement(By.id("column-b")));
    return driver.findElement(By.id("column-b"))
      .findElement(By.xpath(".//*"))
      .getText();
  }
}