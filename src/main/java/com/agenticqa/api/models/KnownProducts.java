package com.agenticqa.api.models;

/**
 * Stable Automation Exercise catalogue anchors used for exact API assertions.
 *
 * @author Akash Aggarwal
 */
public final class KnownProducts {

    /** First catalogue product — stable across AE practice data. */
    public static final Product BLUE_TOP = new Product(
            1,
            "Blue Top",
            "Rs. 500",
            "Polo",
            new Product.Category(new Product.Category.Usertype("Women"), "Tops")
    );

    public static final Product MEN_TSHIRT = new Product(
            2,
            "Men Tshirt",
            "Rs. 400",
            "H&M",
            new Product.Category(new Product.Category.Usertype("Men"), "Tshirts")
    );

    public static final Product SLEEVELESS_DRESS = new Product(
            3,
            "Sleeveless Dress",
            "Rs. 1000",
            "Madame",
            new Product.Category(new Product.Category.Usertype("Women"), "Dress")
    );

    public static final String SEARCH_TERM_TOP = "top";

    private KnownProducts() {
    }
}
