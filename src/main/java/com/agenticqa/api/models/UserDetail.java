package com.agenticqa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDetail(
        int id,
        String name,
        String email,
        String title,
        @JsonProperty("birth_day") String birthDay,
        @JsonProperty("birth_month") String birthMonth,
        @JsonProperty("birth_year") String birthYear,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String company,
        String address1,
        String address2,
        String country,
        String state,
        String city,
        String zipcode
) {
}
