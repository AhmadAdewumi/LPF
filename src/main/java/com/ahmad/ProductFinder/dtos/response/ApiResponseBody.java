package com.ahmad.ProductFinder.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ApiResponseBody {
    private String message;
    private Object data;
}
