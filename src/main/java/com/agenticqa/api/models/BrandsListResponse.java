package com.agenticqa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Optional;

/**
 * Response for brandsList.
 *
 * <pre>{ "responseCode": 200, "brands": [ { "id": 1, "brand": "Polo" } ] }</pre>
 *
 * @author Akash Aggarwal
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BrandsListResponse(int responseCode, List<Brand> brands) {

    public BrandsListResponse {
        brands = brands == null ? List.of() : List.copyOf(brands);
    }

    public boolean isSuccess() {
        return responseCode == 200;
    }

    public int brandCount() {
        return brands.size();
    }

    public boolean isEmpty() {
        return brands.isEmpty();
    }

    public Optional<Brand> findById(int id) {
        return brands.stream().filter(b -> b != null && b.id() == id).findFirst();
    }

    public Brand requireById(int id) {
        return findById(id).orElseThrow(() ->
                new AssertionError("Brand id=" + id + " not found (count=" + brandCount() + ")"));
    }
}
