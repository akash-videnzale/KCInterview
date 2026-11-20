package com.agenticqa.ui.pages;

import com.agenticqa.core.reporting.ExtentLogger;
import com.agenticqa.ui.components.ProductCardComponent;
import com.agenticqa.ui.messages.UiMessages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Page object for the products catalogue and search results.
 *
 * @author Akash Aggarwal
 */
public class ProductsPage extends BasePage {

    public ProductsPage() {
        super();
    }

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public ProductsPage open() {
        ExtentLogger.step("Open products catalogue");
        navigate("/products");
        dismissOverlaysIfPresent();
        ExtentLogger.screenshot("Products catalogue");
        return this;
    }

    public ProductsPage search(String query) {
        ExtentLogger.step("Search products for: " + query);
        type(By.id("search_product"), query);
        click(By.id("submit_search"));
        waitForDomReady();
        ExtentLogger.screenshot("Search results for " + query);
        return this;
    }

    public boolean hasSearchResultsHeading() {
        return isVisible(byTagTextContains("h2", UiMessages.SEARCHED_PRODUCTS))
                || isVisible(By.cssSelector(".features_items"));
    }

    public List<ProductCardComponent> productCards() {
        List<WebElement> cards = findAll(By.cssSelector(".features_items .product-image-wrapper"));
        List<ProductCardComponent> result = new ArrayList<>();
        for (WebElement card : cards) {
            result.add(new ProductCardComponent(card));
        }
        return result;
    }

    public int productCount() {
        return count(By.cssSelector(".features_items .product-image-wrapper"));
    }

    public ProductDetailsPage openFirstProductDetails() {
        ExtentLogger.step("Open first product details");
        dismissOverlaysIfPresent();
        WebElement link = findAll(By.cssSelector(".features_items .product-image-wrapper a[href*='product_details']"))
                .stream()
                .findFirst()
                .orElseGet(() -> find(By.xpath(
                        "(//div[contains(@class,'features_items')]//a[contains(normalize-space(),\""
                                + UiMessages.VIEW_PRODUCT + "\")])[1]")));
        String href = link.getAttribute("href");
        if (href != null && !href.isBlank()) {
            navigate(href);
        } else {
            click(By.xpath("(//div[contains(@class,'features_items')]//a[contains(normalize-space(),\""
                    + UiMessages.VIEW_PRODUCT + "\")])[1]"));
        }
        waitForDomReady();
        dismissOverlaysIfPresent();
        waitVisible(By.cssSelector(".product-information"));
        ExtentLogger.screenshot("Product details");
        return new ProductDetailsPage();
    }

    public ProductsPage addFirstProductToCart() {
        ExtentLogger.step("Add first product to cart");
        dismissOverlaysIfPresent();
        waitVisible(By.cssSelector(".features_items .product-image-wrapper"));
        List<WebElement> addButtons = findAll(By.cssSelector(
                ".features_items .product-image-wrapper .productinfo a.add-to-cart, "
                        + ".features_items .product-image-wrapper .product-overlay a.add-to-cart"));
        if (!addButtons.isEmpty()) {
            WebElement button = addButtons.get(0);
            try {
                ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", button);
            } catch (RuntimeException e) {
                button.click();
            }
        } else {
            // Fallback: known stable product detail page when catalogue cards are obscured
            navigate("/product_details/1");
            dismissOverlaysIfPresent();
            new ProductDetailsPage().addToCart();
            ExtentLogger.screenshot("Added to cart modal");
            return this;
        }
        waitDefault().until(ExpectedConditions.visibilityOfElementLocated(By.id("cartModal")));
        ExtentLogger.screenshot("Added to cart modal");
        return this;
    }

    public ProductsPage continueShopping() {
        click(By.xpath("//div[@id='cartModal']//button[contains(normalize-space(),\""
                + UiMessages.CONTINUE_SHOPPING + "\")]"));
        return this;
    }

    public CartPage viewCartFromModal() {
        ExtentLogger.step("View cart from modal");
        click(By.cssSelector("#cartModal a[href='/view_cart']"));
        waitForDomReady();
        ExtentLogger.screenshot("Cart page");
        return new CartPage();
    }
}
