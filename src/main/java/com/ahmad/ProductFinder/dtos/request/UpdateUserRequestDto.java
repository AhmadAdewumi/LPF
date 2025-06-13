package com.ahmad.ProductFinder.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "UpdateUserRequest",
        description = "Request DTO for updating an existing user's profile details. All fields are optional; only provided fields will be updated."
)
public class UpdateUserRequestDto {

    @Schema(description = "The user's updated first name", example = "Arike", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String firstName;

    @Schema(description = "The user's updated last name", example = "Asake", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String lastName;

    @Schema(description = "The user's updated phone number", example = "+2349012345678", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phoneNumber;

    @Schema(description = "The user's new password (should meet complexity requirements)", example = "NewP@ssword456", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String password;
}