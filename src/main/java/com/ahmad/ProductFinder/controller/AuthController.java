package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.controller.swaggerDocs.AuthDocs;
import com.ahmad.ProductFinder.dtos.request.LoginRequest;
import com.ahmad.ProductFinder.dtos.request.PasswordResetDto;
import com.ahmad.ProductFinder.dtos.request.PasswordResetRequestDto;
import com.ahmad.ProductFinder.dtos.request.RefreshTokenRequest;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.JwtResponse;
import com.ahmad.ProductFinder.service.authService.AuthService;
import com.ahmad.ProductFinder.service.secureTokenService.PasswordResetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.format;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication operation using jwt.")
@RequiredArgsConstructor
public class AuthController implements AuthDocs {

    private final AuthService authService;

    private final PasswordResetService resetService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> authenticate(@Valid @RequestBody LoginRequest request) {
        try {
            JwtResponse jwtResponse = authService.authenticate(request);
            return ResponseEntity.ok(new ApiResponseBody("Login Successful!", jwtResponse));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseBody("Login Failed! Please, Try Again!", null));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseBody> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        JwtResponse response = authService.refreshAccessToken(request.getRefreshToken());
        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseBody("Invalid or expired refresh token.", null));
        }

        return ResponseEntity.ok(new ApiResponseBody("Access token refreshed successfully.", response));
    }

    @PostMapping("/request/password-reset")
    public ResponseEntity<ApiResponseBody> requestPasswordReset(@RequestBody @Valid PasswordResetRequestDto request) {
        log.info("Received request in controller to reset password for: {}", request.getEmail());
        resetService.resetPasswordRequest(request);
        return ResponseEntity.ok(new ApiResponseBody(format("Password reset request submitted successfully for user with mail, %s!", request.getEmail()), null));
    }

    @PatchMapping("/password-reset")
    public ResponseEntity<ApiResponseBody> resetPassword(@RequestBody @Valid PasswordResetDto dto) {
        resetService.resetPassword(dto);
        return ResponseEntity.ok(new ApiResponseBody("Password reset successfully.", null));
    }
}

