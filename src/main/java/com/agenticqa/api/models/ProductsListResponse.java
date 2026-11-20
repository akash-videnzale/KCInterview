package com.agenticqa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public record ProductsListResponse(int responseCode, List<Product> products) {

    public ProductsListResponse {
        products = products == null ? List.of() : List.copyOf(products);
    }

    public boolean isSuccess() {
        return responseCode == 200;
    }

    public int productCount() {
        return products.size();
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public Optional<Product> findById(int id) {
        return products.stream().filter(p -> p != null && p.id() == id).findFirst();
    }

    public Product requireById(int id) {
        return findById(id).orElseThrow(() ->
                new AssertionError("Product id=" + id + " not found (count=" + productCount() + ")"));
    }
}
