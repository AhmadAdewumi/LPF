package com.ahmad.ProductFinder.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "UpdateImageRequest",
        description = "Request DTO for updating details of an existing image (e.g., alt text, file name) associated with a product."
)
public record UpdateImageRequestDto(
        @Schema(description = "Alternative text for the image, used for description", example = "Close-up view of a new smartphone")
        String altText,

        @Schema(description = "The new file name of the image (e.g., if the file itself is being renamed)", example = "smartphone_xyz_v2.jpg")
        String fileName,

        @Schema(description = "The ID of the product this image is associated with", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long productId
)
{}