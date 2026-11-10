package com.demo.modern.tests;

import com.demo.modern.pages.FileUploadPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileUploadTest {
  private Page page;
  private Browser browser;
  private Playwright playwright;
  private FileUploadPage fileUploadPage;

  @BeforeEach
  public void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    page = browser.newPage();
    fileUploadPage = new FileUploadPage(page);
  }

  @AfterEach
  public void tearDown() {
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @Test
  public void fileUpload() throws IOException {
    fileUploadPage.navigate();
    assertTrue(page.title().contains("The Internet"),
      "Expected title 'The Internet', got: " + page.title());

    // Create a test file
    Path uploadFile = Files.createTempFile("test-upload", ".txt");
    Files.write(uploadFile, "Test content".getBytes());

    // Upload file
    FileChooser fileChooser = page.waitForFileChooser(() -> {
      page.locator("#file-upload").click();
    });
    fileChooser.setFiles(uploadFile);

    // Click upload button
    page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
      new Page.GetByRoleOptions().setName("Upload")).click();

    // Verify uploaded file name
    String uploadedFileName = page.locator("#uploaded-files").textContent();
    assertTrue(uploadedFileName.contains("test-upload"),
      "Uploaded file name should contain 'test-upload', but got: " + uploadedFileName);
  }
}