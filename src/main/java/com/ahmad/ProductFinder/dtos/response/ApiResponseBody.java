package com.ahmad.ProductFinder.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@Schema(
        name = "ApiResponseBody",
        description = "A standard wrapper for all API responses, providing a message and the actual data payload."
)
public class ApiResponseBody {

//    @Schema(description = "A descriptive message indicating the outcome of the API operation", example = "Operation successful!")
    private String message;

    @Schema(description = "The actual data payload of the response. This can be a single object, a list of objects, or null")
    private Object data;
}