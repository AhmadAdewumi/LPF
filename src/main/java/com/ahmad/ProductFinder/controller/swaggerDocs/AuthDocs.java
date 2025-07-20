package com.ahmad.ProductFinder.controller.swaggerDocs;

import com.ahmad.ProductFinder.dtos.request.LoginRequest;
import com.ahmad.ProductFinder.dtos.request.RefreshTokenRequest;
import com.ahmad.ProductFinder.dtos.request.PasswordResetDto;
import com.ahmad.ProductFinder.dtos.request.PasswordResetRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Authentication", description = "User authentication and token management")
public interface AuthDocs {

    @Operation(
            summary = "User login (JWT)",
            description = "Authenticates a user with username and password. Returns access and refresh tokens.",
            requestBody = @RequestBody(
                    description = "User login credentials",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful. Tokens returned.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "500", description = "Server error.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
            }
    )
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponseBody> authenticate(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request
    );


    @Operation(
            summary = "Refresh access token",
            description = "Refreshes a new access token using a valid refresh token.",
            requestBody = @RequestBody(
                    description = "Refresh token payload",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RefreshTokenRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token refreshed successfully.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "500", description = "Server error.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
            }
    )
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponseBody> refreshAccessToken(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody RefreshTokenRequest request
    );

    @Operation(
            summary = "Request password reset",
            description = "Sends a password-reset email to the user if the email exists. Contains a one-time token link.",
            requestBody = @RequestBody(
                    description = "Email address for password reset",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PasswordResetRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset email sent (if account exists).",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid email format.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "500", description = "Server error.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
            }
    )
    @PostMapping(value = "/password-reset/request", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponseBody> requestPasswordReset(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody PasswordResetRequestDto request
    );

    @Operation(
            summary = "Perform password reset",
            description = "Resets the user's password given a valid reset token, new password, and confirmation.",
            requestBody = @RequestBody(
                    description = "Password reset payload",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PasswordResetDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset successfully.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid token or passwords do not match.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "500", description = "Server error.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
            }
    )
    @PostMapping(value = "/password-reset", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponseBody> resetPassword(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody PasswordResetDto dto
    );
}
