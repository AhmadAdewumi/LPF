package com.ahmad.ProductFinder.globalExceptionHandling.exceptions;

public class AlreadyExistsException extends RuntimeException{
    public AlreadyExistsException(String message){
        super(message);
    }

}
