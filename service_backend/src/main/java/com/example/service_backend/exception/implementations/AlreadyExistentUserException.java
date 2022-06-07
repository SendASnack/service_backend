package com.example.service_backend.exception.implementations;

public class AlreadyExistentUserException extends RuntimeException {

    public AlreadyExistentUserException(String message) {
        super(message);
    }

}