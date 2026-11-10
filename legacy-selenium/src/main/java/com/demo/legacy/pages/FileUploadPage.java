package com.demo.legacy.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.nio.file.Path;
import java.time.Duration;

public class FileUploadPage {
  private WebDriver driver;
  public FileUploadPage(WebDriver d) { this.driver = d; }
  public void navigate() {
    driver.get("https://the-internet.herokuapp.com/upload");
    new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.titleContains("The Internet"));
  }
  public void uploadFile(Path filePath) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='file-upload']"))).sendKeys(filePath.toString());
  }
  public void clickUpload() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='file-submit']"))).click();
  }
  public String getUploadedFileName() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='uploaded-files']")));
    return driver.findElement(By.xpath("//div[@id='uploaded-files']")).getText();
  }
}