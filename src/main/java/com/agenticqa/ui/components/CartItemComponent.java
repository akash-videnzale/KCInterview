package com.agenticqa.ui.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Reusable cart-row component.
 *
 * @author Akash Aggarwal
 */
public class CartItemComponent {

    private final WebElement row;

    public CartItemComponent(WebElement row) {
        this.row = row;
    }

    public String name() {
        return row.findElement(By.cssSelector(".cart_description h4 a")).getText().trim();
    }

    public String price() {
        return row.findElement(By.cssSelector(".cart_price p")).getText().trim();
    }

    public String quantity() {
        var buttons = row.findElements(By.cssSelector(".cart_quantity button"));
        if (!buttons.isEmpty()) {
            return buttons.get(0).getText().trim();
        }
        return row.findElement(By.cssSelector(".cart_quantity")).getText().trim();
    }

    public void remove() {
        row.findElement(By.cssSelector("a.cart_quantity_delete")).click();
    }
}
