package com.ahmad.ProductFinder.globalExceptionHandling.exceptions;

public class UnknownIdentifierException extends RuntimeException{

    public UnknownIdentifierException(String message) {
        super(message);
    }
}
