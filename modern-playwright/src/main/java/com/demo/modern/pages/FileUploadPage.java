package com.demo.modern.pages;

import com.microsoft.playwright.Page;

public class FileUploadPage {
  private final Page page;

  public FileUploadPage(Page page) {
    this.page = page;
  }

  public void navigate() {
    page.navigate("https://the-internet.herokuapp.com/upload");
  }

  public void uploadFile(String filePath) {
    page.setInputFiles("#file-upload", java.nio.file.Paths.get(filePath));
    page.click("#file-submit");
  }

  public String getUploadedFileName() {
    return page.locator("#uploaded-files").textContent();
  }
}
