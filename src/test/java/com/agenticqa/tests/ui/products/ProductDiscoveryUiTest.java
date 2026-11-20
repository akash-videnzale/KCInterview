package com.agenticqa.tests.ui.products;

import com.agenticqa.api.models.KnownProducts;
import com.agenticqa.ui.assertions.ProductAssertions;
import com.agenticqa.ui.assertions.UiAssertions;
import com.agenticqa.ui.components.ProductCardComponent;
import com.agenticqa.ui.fixtures.UiBaseTest;
import com.agenticqa.ui.pages.ProductDetailsPage;
import com.agenticqa.ui.pages.ProductsPage;
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

        UiAssertions.assertSearchResultsVisible(productsPage);

        ProductCardComponent blueTop = productsPage.productCards().stream()
                .filter(c -> KnownProducts.BLUE_TOP.getName().equals(c.name()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Product missing from search: " + KnownProducts.BLUE_TOP.getName()));
        ProductAssertions.assertExactProductName(blueTop.name(), KnownProducts.BLUE_TOP.getName());
        ProductAssertions.assertExactProductPrice(blueTop.price(), KnownProducts.BLUE_TOP.getPrice());

        ProductDetailsPage details = new ProductDetailsPage();
        details.openById(KnownProducts.BLUE_TOP.getId());
        UiAssertions.assertProductDetailsLoaded(details);
        ProductAssertions.assertKnownBlueTopDetails(details.productName(), details.productPrice());
        UiAssertions.assertBrandContains(details, KnownProducts.BLUE_TOP.getBrand());
        UiAssertions.assertAvailabilityPresent(details);
    }
}
