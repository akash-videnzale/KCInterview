package com.agenticqa.ui.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Reusable product card on catalogue/search results.
 *
 * @author Akash Aggarwal
 */
public class ProductCardComponent {

    private final WebElement root;

    public ProductCardComponent(WebElement root) {
        this.root = root;
    }

    public String name() {
        return root.findElement(By.cssSelector(".productinfo p")).getText().trim();
    }

    public String price() {
        return root.findElement(By.cssSelector(".productinfo h2")).getText().trim();
    }

    public void addToCart() {
        root.findElement(By.cssSelector(".productinfo a.add-to-cart")).click();
    }

    public void viewProduct() {
        root.findElement(By.xpath(".//a[contains(normalize-space(),'View Product')]")).click();
    }
}
