package com.agenticqa.ui.assertions;

import com.agenticqa.api.models.KnownProducts;
import org.testng.Assert;

/**
 * Exact UI product assertions.
 *
 * @author Akash Aggarwal
 */
public final class ProductAssertions {

    private ProductAssertions() {
    }

    public static void assertExactProductName(String actualName, String expectedName) {
        Assert.assertEquals(actualName, expectedName, "product name");
    }

    public static void assertExactProductPrice(String actualPrice, String expectedPrice) {
        Assert.assertEquals(actualPrice, expectedPrice, "product price");
    }

    public static void assertKnownBlueTopDetails(String name, String price) {
        assertExactProductName(name, KnownProducts.BLUE_TOP.name());
        assertExactProductPrice(price, KnownProducts.BLUE_TOP.price());
    }
}
