package com.example.service_backend.exception.implementations;

public class CartNotFoundException extends RuntimeException{
    
    public CartNotFoundException(String message) {
        super(message);
    }

}
