package com.example.service_backend.exception.implementations;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

}