package com.ahmad.ProductFinder.dtos.response;

public record CloudinaryResponseDto(
        String url,
        String publicId,
        String format,
        Long size
)
{}
