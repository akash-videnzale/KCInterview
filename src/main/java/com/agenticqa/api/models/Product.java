package com.agenticqa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One product in catalogue / search responses.
 *
 * @author Akash Aggarwal
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    private int id;
    private String name;
    private String price;
    private String brand;
    private Category category;

    public Product() {
    }

    public Product(int id, String name, String price, String brand, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Category {

        private Usertype usertype;
        private String category;

        public Category() {
        }

        public Category(Usertype usertype, String category) {
            this.usertype = usertype;
            this.category = category;
        }

        public Usertype getUsertype() {
            return usertype;
        }

        public void setUsertype(Usertype usertype) {
            this.usertype = usertype;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Usertype {

            private String usertype;

            public Usertype() {
            }

            public Usertype(String usertype) {
                this.usertype = usertype;
            }

            public String getUsertype() {
                return usertype;
            }

            public void setUsertype(String usertype) {
                this.usertype = usertype;
            }
        }
    }
}
