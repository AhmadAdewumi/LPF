package com.ahmad.ProductFinder.controller.swaggerDocs;

import com.ahmad.ProductFinder.dtos.request.CreateUserRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateUserRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "Endpoints for user account management")
public interface UserDocs {

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration data",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreateUserRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error or duplicate username."),
                    @ApiResponse(responseCode = "500", description = "Internal error during registration.")
            }
    )
    ResponseEntity<ApiResponseBody> registerUser(@RequestBody @Valid CreateUserRequestDto request);


    @Operation(
            summary = "Update user profile",
            description = "Updates an existing user by ID.",
            parameters = {
                    @Parameter(name = "userId", description = "User ID", required = true, example = "1")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New user profile data",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UpdateUserRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input."),
                    @ApiResponse(responseCode = "404", description = "User not found."),
                    @ApiResponse(responseCode = "500", description = "Update failed.")
            }
    )
    ResponseEntity<ApiResponseBody> updateUser(@RequestBody UpdateUserRequestDto request, @PathVariable Long userId);


    @Operation(
            summary = "Soft delete user",
            description = "Deactivates a user by setting a flag (non-permanent).",
            parameters = {
                    @Parameter(name = "userId", description = "User ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "User soft-deleted.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "User not found."),
                    @ApiResponse(responseCode = "500", description = "Error during soft deletion.")
            }
    )
    ResponseEntity<ApiResponseBody> disableUserByUserId(@PathVariable Long userId);


    @Operation(
            summary = "Permanently delete user",
            description = "Completely removes a user account. Irreversible.",
            parameters = {
                    @Parameter(name = "userId", description = "User ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted permanently.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "User not found."),
                    @ApiResponse(responseCode = "500", description = "Deletion failed.")
            }
    )
    ResponseEntity<ApiResponseBody> deleteUserForReal(@PathVariable Long userId);


    @Hidden
    @Operation(
            summary = "Get user by username",
            description = "Returns a user profile based on the username.",
            parameters = {
                    @Parameter(name = "username", description = "Unique username", required = true, example = "jane.doe")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Username not found."),
                    @ApiResponse(responseCode = "500", description = "Retrieval failed.")
            }
    )
    ResponseEntity<ApiResponseBody> getUserByUsername(@RequestParam String username);

    @Hidden
    @Operation(
            summary = "Get all users",
            description = "Lists all user accounts in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users listed.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "500", description = "Error retrieving users.")
            }
    )
    ResponseEntity<ApiResponseBody> getAllUsers();
}
