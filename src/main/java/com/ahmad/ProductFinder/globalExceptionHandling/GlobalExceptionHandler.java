package com.ahmad.ProductFinder.globalExceptionHandling;

import com.ahmad.ProductFinder.dtos.response.ApiErrorResponse;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.AlreadyExistsException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.CloudinaryException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.IllegalArgumentException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Hidden
@RestControllerAdvice
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
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
        exception.printStackTrace();
        log.error("Unhandled exception: {}", exception.getMessage(), exception);
        if (response.isCommitted()) {
            return null;
        }
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Something went wrong on the server")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("Access denied: " + "You don't have the permission to access this resource!")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
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


//@Slf4j
//@Hidden
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler({
//            ResourceNotFoundException.class,
//            AlreadyExistsException.class,
//            IllegalArgumentException.class,
//            CloudinaryException.class,
//            AccessDeniedException.class,
//            MethodArgumentNotValidException.class,
//            Exception.class
//    })
//    public ResponseEntity<ApiErrorResponse> handleAllExceptions(Exception ex, HttpServletRequest request) {
//        HttpStatus status = determineHttpStatus(ex);
//        String message = determineMessage(ex);
//
//        log.error("Exception [{}]: {}", status, ex.getMessage(), ex);
//
//        ApiErrorResponse error = ApiErrorResponse.builder()
//                .timeStamp(LocalDateTime.now())
//                .path(request.getRequestURI())
//                .error(status.getReasonPhrase())
//                .statusCode(status.value())
//                .message(message)
//                .build();
//
//        return ResponseEntity.status(status).body(error);
//    }
//
//    private HttpStatus determineHttpStatus(Exception ex) {
//        if (ex instanceof ResourceNotFoundException) {
//            return HttpStatus.NOT_FOUND;
//        }
//        if (ex instanceof AlreadyExistsException) {
//            return HttpStatus.CONFLICT;
//        }
//        if (ex instanceof IllegalArgumentException) {
//            return HttpStatus.BAD_REQUEST;
//        }
//        if (ex instanceof CloudinaryException) {
//            int code = ((CloudinaryException) ex).getStatusCode();
//            return HttpStatus.resolve(code) != null ? HttpStatus.resolve(code) : HttpStatus.INTERNAL_SERVER_ERROR;
//        }
//        if (ex instanceof AccessDeniedException) {
//            return HttpStatus.FORBIDDEN;
//        }
//        if (ex instanceof MethodArgumentNotValidException) {
//            return HttpStatus.BAD_REQUEST;
//        }
//        // Fallback for all other exceptions
//        return HttpStatus.INTERNAL_SERVER_ERROR;
//    }
//
//    private String determineMessage(Exception ex) {
//        if (ex instanceof CloudinaryException) {
//            return "Cloudinary error: " + ((CloudinaryException) ex).getCloudinaryError();
//        }
//        if (ex instanceof MethodArgumentNotValidException) {
//            String errors = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors().stream()
//                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
//                    .collect(Collectors.joining(", "));
//            return "Validation failed: " + errors;
//        }
//        if (ex instanceof AccessDeniedException) {
//            return "Access denied: You don't have permission to access this resource.";
//        }
//        // Default to exception's message
//        return ex.getMessage();
//    }
//}
