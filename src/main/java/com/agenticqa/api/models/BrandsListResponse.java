package com.agenticqa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
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
public class BrandsListResponse {

    private int responseCode;
    private List<Brand> brands = new ArrayList<>();

    public BrandsListResponse() {
    }

    public BrandsListResponse(int responseCode, List<Brand> brands) {
        this.responseCode = responseCode;
        setBrands(brands);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands == null ? new ArrayList<>() : brands;
    }

    public boolean isSuccess() {
        return responseCode == 200;
    }

    public int brandCount() {
        return brands == null ? 0 : brands.size();
    }

    public boolean isEmpty() {
        return brands == null || brands.isEmpty();
    }

    public Optional<Brand> findById(int id) {
        if (brands == null) {
            return Optional.empty();
        }
        return brands.stream().filter(b -> b != null && b.getId() == id).findFirst();
    }

    public Brand requireById(int id) {
        return findById(id).orElseThrow(() ->
                new AssertionError("Brand id=" + id + " not found (count=" + brandCount() + ")"));
    }
}
