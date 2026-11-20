package com.agenticqa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Response for productsList / searchProduct.
 *
 * <pre>{ "responseCode": 200, "products": [ { "id": 1, "name": "Blue Top", ... } ] }</pre>
 *
 * @author Akash Aggarwal
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductsListResponse {

    private int responseCode;
    private List<Product> products = new ArrayList<>();

    public ProductsListResponse() {
    }

    public ProductsListResponse(int responseCode, List<Product> products) {
        this.responseCode = responseCode;
        setProducts(products);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products == null ? new ArrayList<>() : products;
    }

    public boolean isSuccess() {
        return responseCode == 200;
    }

    public int productCount() {
        return products == null ? 0 : products.size();
    }

    public boolean isEmpty() {
        return products == null || products.isEmpty();
    }

    public Optional<Product> findById(int id) {
        if (products == null) {
            return Optional.empty();
        }
        return products.stream().filter(p -> p != null && p.getId() == id).findFirst();
    }

    public Product requireById(int id) {
        return findById(id).orElseThrow(() ->
                new AssertionError("Product id=" + id + " not found (count=" + productCount() + ")"));
    }
}
