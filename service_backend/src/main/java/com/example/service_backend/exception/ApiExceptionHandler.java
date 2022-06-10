package com.example.service_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.service_backend.exception.implementations.AlreadyExistentUserException;
import com.example.service_backend.exception.implementations.BadRequestException;
import com.example.service_backend.exception.implementations.ForbiddenOperationException;
import com.example.service_backend.exception.implementations.ProductNotFoundException;
import com.example.service_backend.exception.implementations.UserNotFoundException;

import java.util.Date;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestException(BadRequestException badRequestException, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), badRequestException.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistentUserException.class)
    public ResponseEntity<?> alreadyExistentUserException(AlreadyExistentUserException alreadyExistentUserException, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), alreadyExistentUserException.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<?> forbiddenOperationException(ForbiddenOperationException forbiddenOperationException, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), forbiddenOperationException.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> productNotFoundException(ProductNotFoundException productNotFoundException, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), productNotFoundException.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> userNotFoundException(UserNotFoundException userNotFoundException, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), userNotFoundException.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

}
