package com.ahmad.ProductFinder.dtos.response;

public record ImageResponseDto(
        Long id,
        String url,
        String fileName,
        String altText
) {}
