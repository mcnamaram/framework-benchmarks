package com.demo.modern.tests;

import com.demo.modern.pages.CartPage;
import com.demo.modern.pages.CheckoutPage;
import com.demo.modern.pages.InventoryPage;
import com.demo.modern.pages.LoginPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.APIRequestContext;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class EpicFlowTest {
  private Page page;
  private Browser browser;
  private Playwright playwright;
  private APIRequestContext apiContext;
  private LoginPage loginPage;
  private InventoryPage inventoryPage;
  private CartPage cartPage;
  private CheckoutPage checkoutPage;

  @BeforeEach
  public void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    page = browser.newPage();
    apiContext = playwright.request().newContext();
    playwright.selectors().setTestIdAttribute("data-test");
    loginPage = new LoginPage(page);
    inventoryPage = new InventoryPage(page);
    cartPage = new CartPage(page);
    checkoutPage = new CheckoutPage(page);
  }

  @AfterEach
  public void tearDown() {
    if (browser != null) browser.close();
  }

  @Test
  public void epicEndToEndFlowWithApiCalls() {
    // ==================== API VALIDATIONS (FIRST) ====================
    String userJson = apiContext.get("https://jsonplaceholder.typicode.com/users/1").text();
    assertTrue(userJson.contains("Leanne Graham") || userJson.contains("Bret"),
      () -> "Expected 'Leanne Graham' or 'Bret' in user API response, got: " + userJson);

    String sessionResponse = apiContext.get("https://httpbin.org/get").text();
    assertTrue(sessionResponse.contains("origin"),
      () -> "Expected 'origin' in session API response, got: " + sessionResponse);

    // ==================== SAUCE DEMO LOGIN ====================
    loginPage.navigate();
    assertThat(page).hasTitle("Swag Labs");
    loginPage.login("standard_user", "secret_sauce");
    assertThat(page).hasTitle(Pattern.compile(".*Swag Labs.*"));

    // ==================== INVENTORY OPERATIONS ====================
    inventoryPage.sortByPriceHighToLow();
    assertThat(page.getByTestId("inventory-item").first()).isVisible();

    inventoryPage.addTopItems(3);
    assertThat(inventoryPage.getCartBadge()).containsText("3");

    // Click into item detail page
    inventoryPage.clickFourthItem();
    assertThat(page).hasTitle(Pattern.compile(".*Swag Labs.*"));

    // ==================== ITEM DETAIL AND ADD TO CART ====================
    inventoryPage.addItemToCart();
    assertThat(inventoryPage.getCartBadge()).containsText("4");

    inventoryPage.backToProducts();
    assertThat(page).hasTitle(Pattern.compile(".*Swag Labs.*"));
    // After adding item from detail page, cart shows 4 total
    assertThat(inventoryPage.getCartBadge()).containsText("4");

    // ==================== CART OPERATIONS ====================
    inventoryPage.getCartBadge().click();
    assertThat(page).hasURL(Pattern.compile(".*cart.*"));

    cartPage.removeSecondItem();
    assertThat(cartPage.itemLocator()).hasCount(3);

    String apiResponse = apiContext.get("https://httpbin.org/headers").text();
    assertTrue(apiResponse.contains("Host"),
      () -> "Expected 'Host' in headers API response, got: " + apiResponse);

    // ==================== CHECKOUT FLOW ====================
    checkoutPage.proceedToCheckout().click();
    assertThat(page).hasTitle(Pattern.compile(".*Swag Labs.*"));

    checkoutPage.firstNameField().fill("Test");
    checkoutPage.lastNameField().fill("User");
    checkoutPage.postalCodeField().fill("12345");
    checkoutPage.continueButton().click();

    assertThat(checkoutPage.summaryTotalLabel()).isVisible();

    checkoutPage.finishButton().click();
    assertThat(checkoutPage.completeHeader()).containsText("Thank you for your order");

    // ==================== FINAL API VALIDATIONS ====================
    String uploadResponse = apiContext.get("https://httpbin.org/anything?operation=upload").text();
    assertTrue(uploadResponse.contains("headers"),
      () -> "Expected 'headers' in upload API response, got: " + uploadResponse);

    String ipResponse = apiContext.get("https://httpbin.org/ip").text();
    assertTrue(ipResponse.contains("origin"),
      () -> "Expected 'origin' in IP API response, got: " + ipResponse);

    checkoutPage.backToProductsButton().click();
    assertThat(page).hasTitle(Pattern.compile(".*Swag Labs.*"));

    String finalResponse = apiContext.get("https://jsonplaceholder.typicode.com/users/1").text();
    assertTrue(finalResponse.contains("id"),
      () -> "Expected 'id' in final API response, got: " + finalResponse);
  }
}