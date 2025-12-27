package com.example.productcatalogservice.controllers;

import com.example.productcatalogservice.exceptions.NoProductAvailable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerAdvisor {
    @ExceptionHandler(NoProductAvailable.class)
    public ResponseEntity<String> exception(NoProductAvailable e) {
        return new ResponseEntity<>("No Product found", HttpStatus.BAD_REQUEST);
    }
}
