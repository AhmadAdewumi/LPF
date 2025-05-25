package com.ahmad.ProductFinder.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiErrorResponse {
    private final LocalDateTime timeStamp;
    private final String message;
    private final String path;
    private final int statusCode;
    private final String error;
}
