package com.agenticqa.tests.ui.products;

import com.agenticqa.api.models.KnownProducts;
import com.agenticqa.ui.assertions.ProductAssertions;
import com.agenticqa.ui.components.ProductCardComponent;
import com.agenticqa.ui.fixtures.UiBaseTest;
import com.agenticqa.ui.pages.ProductDetailsPage;
import com.agenticqa.ui.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * UI product search — exact name/price on results and details.
 *
 * @author Akash Aggarwal
 */
public class ProductDiscoveryUiTest extends UiBaseTest {

    @Test(groups = {"ui", "smoke", "regression"})
    public void shouldSearchProductsAndOpenDetails() {
        ProductsPage productsPage = new ProductsPage();
        productsPage.open();
        productsPage.search(KnownProducts.SEARCH_TERM_TOP);

        Assert.assertTrue(productsPage.hasSearchResultsHeading(), "Search results should be visible");
        Assert.assertTrue(productsPage.productCount() > 0, "Search should return products");

        ProductCardComponent blueTop = productsPage.productCards().stream()
                .filter(c -> KnownProducts.BLUE_TOP.name().equals(c.name()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Blue Top missing from search results"));
        ProductAssertions.assertExactProductName(blueTop.name(), KnownProducts.BLUE_TOP.name());
        ProductAssertions.assertExactProductPrice(blueTop.price(), KnownProducts.BLUE_TOP.price());

        ProductDetailsPage details = new ProductDetailsPage();
        details.openById(KnownProducts.BLUE_TOP.id());
        Assert.assertTrue(details.isLoaded(), "Product details should load");
        ProductAssertions.assertKnownBlueTopDetails(details.productName(), details.productPrice());
        Assert.assertTrue(details.productBrand().contains(KnownProducts.BLUE_TOP.brand()), "Brand should match");
        Assert.assertFalse(details.productAvailability().isBlank(), "Availability should be present");
    }
}
