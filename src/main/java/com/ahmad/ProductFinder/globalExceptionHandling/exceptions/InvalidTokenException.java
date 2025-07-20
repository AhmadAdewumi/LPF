package com.ahmad.ProductFinder.globalExceptionHandling.exceptions;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message) {
        super(message);
    }
}
