package com.ahmad.ProductFinder.globalExceptionHandling;

import com.ahmad.ProductFinder.dtos.response.ApiErrorResponse;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.AlreadyExistsException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.CloudinaryException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.IllegalArgumentException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request) {
        log.error("Resource not found: {}", exception.getMessage(), exception);
        var response = ApiErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleAlreadyExistsException(AlreadyExistsException exception, HttpServletRequest request) {
        log.error("Conflict: {}", exception.getMessage(), exception);
        var response = ApiErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .statusCode(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception, HttpServletRequest request) {
        log.error("Bad request: {}", exception.getMessage(), exception);
        var response = ApiErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CloudinaryException.class)
    public ResponseEntity<ApiErrorResponse> handleCloudinaryException(CloudinaryException exception, HttpServletRequest request) {
        log.error("Cloudinary exception occurred: {}", exception.getMessage(), exception);
        HttpStatus status = HttpStatus.resolve(exception.getStatusCode());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .error(status.getReasonPhrase())
                .statusCode(status.value())
                .message("Cloudinary error: " + exception.getCloudinaryError())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception, HttpServletRequest request) {
        log.error("Unhandled exception: {}", exception.getMessage(), exception);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Something went wrong on the server")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        log.error("Validation failed: {}", exception.getMessage(), exception);
        String errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(","));
        ApiErrorResponse response = buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), "validation failed : " + errors, exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // HELPER METHOD TO USE IN VALIDATION EXCEPTION, CAN ALSO BE USED IN OTHER EXCEPTIONS , WILL REFACTOR LATER SHA
    private ApiErrorResponse buildErrorResponse(HttpStatus status, String message, String path, Exception exception) {
        log.error("Error Response Built - Status: {}, Message: {}, Path: {}", status, message, path, exception);
        return ApiErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}
