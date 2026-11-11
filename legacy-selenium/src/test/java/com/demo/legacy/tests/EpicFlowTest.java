package com.demo.legacy.tests;

import com.demo.legacy.api.CurlApiClient;
import com.demo.legacy.pages.LoginPage;
import com.demo.legacy.pages.InventoryPage;
import com.demo.legacy.pages.CartPage;
import com.demo.legacy.pages.CheckoutPage;
import com.demo.legacy.util.TestResultCollector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class EpicFlowTest {
  private WebDriver driver;
  private LoginPage loginPage;
  private InventoryPage inventoryPage;
  private CartPage cartPage;
  private CheckoutPage checkoutPage;
  private CurlApiClient apiClient;
  private TestResultCollector collector;

  @Before
  public void setUp() {
    ChromeOptions options = new ChromeOptions();
    // Run in guest mode to avoid password manager and saved credentials
    options.addArguments("--guest");
    // Disable Chrome Password Manager so the "Change your password" breach popup never appears
    options.setExperimentalOption("prefs", new java.util.HashMap<String, Object>() {{
      put("credentials.enable_service", false);
      put("credentials.enable_biometric_auth", false);
      put("profile.password_manager_enabled", false);
      put("profile.password_manager_leak_detection_enabled", false);
      put("profile.password_manager_bubble_shown", false);
      put("profile.password_manager_shown", false);
      put("password_manager_leak_detection_enabled", false);
      put("profile.default_content_setting_values.autofill", 2);
      put("profiles.password_bubble_shown", false);
      put("password_manager.leak_detection_enabled", false);
      put("net.http.privacy_mode", true);
    }});
    options.addArguments("--disable-password-manager");
    options.addArguments("--password-store=basic");
    options.addArguments("--disable-features=PasswordManager,PasswordLeakDetection,PasswordManagerBubble");
    options.addArguments("--disable-blink-features=AutomationControlled");
    options.addArguments("--no-service-autorun");
    options.addArguments("start-maximized");
    options.addArguments("--disable-sync");
    options.addArguments("--no-first-run");
    options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
    driver = new ChromeDriver(options);
    driver.manage().window().maximize();
    loginPage = new LoginPage(driver);
    inventoryPage = new InventoryPage(driver);
    cartPage = new CartPage(driver);
    checkoutPage = new CheckoutPage(driver);
    apiClient = new CurlApiClient();
    collector = new TestResultCollector();
  }

  @After
  public void tearDown() {
    if (driver != null) driver.quit();
  }

  @Test
  public void epicEndToEndFlowWithApiCalls() throws Exception {
    // ==================== API CALL 1: User data ===
    try {
      String resp = apiClient.get("https://jsonplaceholder.typicode.com/users/1").toString();
      if (resp.contains("Leanne Graham") || resp.contains("Bret"))
        collector.addSuccess("API Call 1: User data verified");
      else
        collector.addFailure("API Call 1: User data missing expected data");
    } catch (Exception e) { collector.addFailure("API Call 1: " + e.getMessage()); }

    // ==================== UI STEP 1: Navigate to Sauce Demo ===
    try {
      driver.get("https://www.saucedemo.com/");
      collector.addSuccess("UI Step 1: Navigated to Sauce Demo");
    } catch (Exception e) { collector.addFailure("UI Step 1: " + e.getMessage()); }

    // ==================== UI STEP 2: Login ===
    try {
      loginPage.login("standard_user", "secret_sauce");
      collector.addSuccess("UI Step 2: Logged in as standard_user");
    } catch (Exception e) { collector.addFailure("UI Step 2: " + e.getMessage()); }

    // ==================== API CALL 2: Session verified ===
    try {
      String resp = apiClient.get("https://httpbin.org/get").toString();
      if (resp.contains("origin"))
        collector.addSuccess("API Call 2: Session verified");
      else
        collector.addFailure("API Call 2: Session info missing");
    } catch (Exception e) { collector.addFailure("API Call 2: " + e.getMessage()); }

    // ==================== UI STEP 3: Sort by price high to low ===
    try {
      inventoryPage.sortByPriceHighToLow();
      collector.addSuccess("UI Step 3: Sorted by price high to low");
    } catch (Exception e) { collector.addFailure("UI Step 3: " + e.getMessage()); }

    // ==================== UI STEP 4: Add top 3 items ===
    try {
      inventoryPage.addTopItems(3);
      collector.addSuccess("UI Step 4: Added top 3 items");
    } catch (Exception e) { collector.addFailure("UI Step 4: " + e.getMessage()); }

    // ==================== UI STEP 5: Click 4th item link ===
    try {
      inventoryPage.clickFourthItem();
      collector.addSuccess("UI Step 5: Clicked 4th item to view detail page");
    } catch (Exception e) { collector.addFailure("UI Step 5: " + e.getMessage()); }

    // ==================== API CALL 3: Headers verified ===
    try {
      String resp = apiClient.get("https://httpbin.org/headers").toString();
      if (resp.contains("Host"))
        collector.addSuccess("API Call 3: Headers verified");
      else
        collector.addFailure("API Call 3: Headers missing");
    } catch (Exception e) { collector.addFailure("API Call 3: " + e.getMessage()); }

    // ==================== UI STEP 6: Add 4th item to cart from detail page ===
    try {
      inventoryPage.addItemToCart();
      collector.addSuccess("UI Step 6: Added item to cart from detail page");
    } catch (Exception e) { collector.addFailure("UI Step 6: " + e.getMessage()); }

    // ==================== UI STEP 7: Back to inventory ===
    try {
      inventoryPage.backToProducts();
      collector.addSuccess("UI Step 7: Returned to inventory page");
    } catch (Exception e) { collector.addFailure("UI Step 7: " + e.getMessage()); }

    // ==================== UI STEP 8: Go to cart ===
    try {
      inventoryPage.goToCart();
      collector.addSuccess("UI Step 8: Opened cart page");
    } catch (Exception e) { collector.addFailure("UI Step 8: " + e.getMessage()); }

    // ==================== UI STEP 9: Remove 2nd item ===
    try {
      cartPage.removeSecondItem();
      collector.addSuccess("UI Step 9: Removed 2nd cart item");
    } catch (Exception e) { collector.addFailure("UI Step 9: " + e.getMessage()); }

    // ==================== API CALL 4: Headers verified ===
    try {
      String resp = apiClient.get("https://httpbin.org/headers").toString();
      if (resp.contains("Host"))
        collector.addSuccess("API Call 4: Headers verified");
      else
        collector.addFailure("API Call 4: Headers missing");
    } catch (Exception e) { collector.addFailure("API Call 4: " + e.getMessage()); }

    // ==================== UI STEP 10: Proceed to checkout ===
    try {
      checkoutPage.proceedToCheckout();
      collector.addSuccess("UI Step 10: Proceeded to checkout");
    } catch (Exception e) { collector.addFailure("UI Step 10: " + e.getMessage()); }

    // ==================== UI STEP 11: Enter checkout info ===
    try {
      checkoutPage.enterCheckoutInfo("Test", "User", "12345");
      collector.addSuccess("UI Step 11: Entered checkout info");
    } catch (Exception e) { collector.addFailure("UI Step 11: " + e.getMessage()); }

    // ==================== API CALL 5: Session verified ===
    try {
      String resp = apiClient.get("https://httpbin.org/get").toString();
      if (resp.contains("origin"))
        collector.addSuccess("API Call 5: Session verified");
      else
        collector.addFailure("API Call 5: Session missing");
    } catch (Exception e) { collector.addFailure("API Call 5: " + e.getMessage()); }

    // ==================== UI STEP 12: Verify summary total ===
    try {
      if (checkoutPage.isSummaryTotalVisible()) {
        collector.addSuccess("UI Step 12: Summary total visible");
      } else {
        collector.addFailure("UI Step 12: Summary total not visible");
      }
    } catch (Exception e) { collector.addFailure("UI Step 12: " + e.getMessage()); }

    // ==================== UI STEP 13: Finish order ===
    try {
      checkoutPage.finishOrder();
      collector.addSuccess("UI Step 13: Finished order");
    } catch (Exception e) { collector.addFailure("UI Step 13: " + e.getMessage()); }

    // ==================== API CALL 6: Upload API ===
    try {
      String resp = apiClient.get("https://httpbin.org/anything?operation=upload").toString();
      if (resp.contains("headers"))
        collector.addSuccess("API Call 6: Upload API verified");
      else
        collector.addFailure("API Call 6: Upload API missing");
    } catch (Exception e) { collector.addFailure("API Call 6: " + e.getMessage()); }

    // ==================== API CALL 7: Final IP ===
    try {
      String resp = apiClient.get("https://httpbin.org/ip").toString();
      if (resp.contains("origin"))
        collector.addSuccess("API Call 7: Final IP verified");
      else
        collector.addFailure("API Call 7: IP missing");
    } catch (Exception e) { collector.addFailure("API Call 7: " + e.getMessage()); }

    // ==================== UI STEP 14: Back to products ===
    try {
      checkoutPage.backToProducts();
      collector.addSuccess("UI Step 14: Back to products");
    } catch (Exception e) { collector.addFailure("UI Step 14: " + e.getMessage()); }

    // ==================== API CALL 8: Final user data ===
    try {
      String resp = apiClient.get("https://jsonplaceholder.typicode.com/users/1").toString();
      if (resp.contains("id"))
        collector.addSuccess("API Call 8: Final user data verified");
      else
        collector.addFailure("API Call 8: Final user data missing");
    } catch (Exception e) { collector.addFailure("API Call 8: " + e.getMessage()); }

    collector.report();

    // Fail the test if any steps collected failures
    if (!collector.getFailures().isEmpty()) {
      throw new AssertionError(
        "EpicFlowTest had " + collector.getFailures().size() + " failures:\n" +
        String.join("\n", collector.getFailures())
      );
    }
  }
}