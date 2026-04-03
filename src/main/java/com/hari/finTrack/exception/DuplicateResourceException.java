package com.hari.finTrack.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message){
        super(message);
    }
}