package com.ahmad.ProductFinder.globalExceptionHandling.exceptions;

public class CloudinaryException extends RuntimeException {
    private final int statusCode;
    private final String cloudinaryError;

    public CloudinaryException(String message) {
        this(message, 400, "Unknown Cloudinary error");
    }

    public CloudinaryException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
        this.cloudinaryError = cause.getMessage();
    }

    public CloudinaryException(String message, int statusCode, String cloudinaryError) {
        super(message);
        this.statusCode = statusCode;
        this.cloudinaryError = cloudinaryError;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getCloudinaryError() {
        return cloudinaryError;
    }
}

