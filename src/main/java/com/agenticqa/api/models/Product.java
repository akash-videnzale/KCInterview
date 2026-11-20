package com.agenticqa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Product(
        int id,
        String name,
        String price,
        String brand,
        Category category
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Category(Usertype usertype, String category) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Usertype(String usertype) {
        }
    }
}
