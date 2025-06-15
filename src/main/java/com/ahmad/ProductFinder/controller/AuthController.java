package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.dtos.request.LoginRequest;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.JwtResponse;
import com.ahmad.ProductFinder.security.jwt.JwtUtils;
import com.ahmad.ProductFinder.security.user.LPFUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication operation using jwt.")
public class AuthController {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;


    public AuthController(JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @Operation(
            summary = "User Login",
            description = "Authenticates a user with provided credentials and returns a JWT token upon successful login. " +
                    "This token should be used for subsequent authenticated requests.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials for login",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful, JWT token returned.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Invalid username or password.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid input data (e.g., validation errors for username/password).",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - Unexpected error during authentication or token generation.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    )
            }
    )
    @PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> authenticate(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Authentication attempt for username: {}", request.getUsername());
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("Authentication successful for username: {}", request.getUsername());
            log.debug("Generating JWT token...");

            System.out.println("generate token might crash , Bismillah");
            String jwt = jwtUtils.generateTokenForUser(authentication);
            System.out.println("Alhamdulillah, it didn't crash");

            log.info("JWT token generated successfully for user: {}", request.getUsername());

            LPFUserDetails userDetails = (LPFUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), jwt);
            log.info("Login response prepared for user ID: {}", userDetails.getId());
            return ResponseEntity.ok(new ApiResponseBody("Login Successful!", jwtResponse));
        } catch (AuthenticationException e) {
            log.warn("Login failed for username: {} - Reason: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseBody("Login Failed! Please, Try Again! ", null));
        }
    }



}
