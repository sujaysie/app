package com.example.productcatalogservice.exceptions;

public class NoProductAvailable extends Exception {
    public NoProductAvailable() {
        super("No product available");
    }
}
