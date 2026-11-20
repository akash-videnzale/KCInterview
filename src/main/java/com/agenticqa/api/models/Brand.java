package com.agenticqa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One brand entry in brandsList responses.
 *
 * @author Akash Aggarwal
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Brand {

    private int id;
    private String brand;

    public Brand() {
    }

    public Brand(int id, String brand) {
        this.id = id;
        this.brand = brand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
