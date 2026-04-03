package com.hari.finTrack.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex){
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "404");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateResourceException(DuplicateResourceException ex){
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "409");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedException(UnauthorizedException ex){
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "401");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}