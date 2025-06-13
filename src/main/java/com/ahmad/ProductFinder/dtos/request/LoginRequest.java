package com.ahmad.ProductFinder.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema; // Import the Schema annotation

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "LoginRequest",
        description = "Request DTO for user authentication (login)."
)
public class LoginRequest {

    @NotBlank(message = "username cannot be blank!")
    @Schema(description = "The user's username", example = "john.doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "password cannot be blank!")
    @Schema(description = "The user's password", example = "mySecretPassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}