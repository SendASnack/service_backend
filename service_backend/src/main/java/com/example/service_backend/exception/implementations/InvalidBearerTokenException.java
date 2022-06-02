package com.example.service_backend.exception.implementations;

public class InvalidBearerTokenException extends RuntimeException {

    public InvalidBearerTokenException(String message) {
        super(message);
    }

}