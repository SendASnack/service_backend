package com.example.service_backend.exception.implementations;

public class ProductNotFoundException extends RuntimeException{
    
    public ProductNotFoundException(String message) {
        super(message);
    }

}
