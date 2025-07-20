package com.ahmad.ProductFinder.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetDto {
    private String token;
    @NotBlank
//    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;

    @NotBlank
    String confirmPassword;
}
