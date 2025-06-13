package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.dtos.request.CreateUserRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateUserRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.UserResponseDto;
import com.ahmad.ProductFinder.service.userService.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Management", description = "APIs for user registration, profile management, and retrieval.")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateUserRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User registered successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid user data (e.g., duplicate username, validation errors)."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during registration."
                    )
            }
    )
    @PostMapping(value = "/register",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> registerUser(@RequestBody @Valid CreateUserRequestDto request) {
        UserResponseDto result = userService.createUser(request);
        return ResponseEntity.ok(new ApiResponseBody("User registered Successfully", result));
    }

    @Operation(
            summary = "Update an existing user's profile",
            description = "Modifies the details of an existing user based on their ID.",
            parameters = {
                    @Parameter(name = "userId", description = "The unique ID of the user to update", required = true, example = "1")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user profile details",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateUserRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid update data or validation errors."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - User with the given ID does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during update."
                    )
            }
    )
    @PatchMapping(value = "/update/{userId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> updateUser(@RequestBody UpdateUserRequestDto request, @PathVariable Long userId) {
        UserResponseDto result = userService.updateUser(request, userId);
        return ResponseEntity.ok(new ApiResponseBody("User updated Successfully", result));
    }

    @Operation(
            summary = "Soft delete a user by ID",
            description = "Marks a user account as deleted without completely removing their data. This typically involves setting an 'active' or 'deleted' flag.",
            parameters = {
                    @Parameter(name = "userId", description = "The unique ID of the user to soft delete", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User soft-deleted successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - User with the given ID does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during soft deletion."
                    )
            }
    )
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponseBody> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponseBody("User deleted Successfully", null));
    }

    @Operation(
            summary = "Permanently delete a user by ID",
            description = "Completely and irreversibly removes a user account and all associated data from the system. Use with caution.",
            parameters = {
                    @Parameter(name = "userId", description = "The unique ID of the user to permanently delete", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User permanently deleted successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - User with the given ID does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during permanent deletion."
                    )
            }
    )
    @DeleteMapping("/delete/fr/{userId}")
    public ResponseEntity<ApiResponseBody> deleteUserForReal(@PathVariable Long userId) {
        userService.deleteUserForReal(userId);
        return ResponseEntity.ok(new ApiResponseBody("User deleted Successfully", null));
    }


    @Operation(
            summary = "Get user by ID",
            description = "Retrieves the profile details of a single user by their unique ID.",
            parameters = {
                    @Parameter(name = "userId", description = "The unique ID of the user to retrieve", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User retrieved successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - User with the given ID does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during retrieval."
                    )
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseBody> getUserById(@PathVariable Long userId) {
        UserResponseDto result = userService.findUserById(userId);
        return ResponseEntity.ok(new ApiResponseBody("User retrieved Successfully", result));
    }

    @Operation(
            summary = "Get user by username",
            description = "Retrieves the profile details of a single user by their unique username.",
            parameters = {
                    @Parameter(name = "username", description = "The username of the user to retrieve", required = true, example = "john.doe")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User retrieved successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - User with the given username does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during retrieval."
                    )
            }
    )
    @GetMapping("/username")
    public ResponseEntity<ApiResponseBody> getUserByUsername(@RequestParam String username) {
        UserResponseDto result = userService.findUserByUsername(username);
        return ResponseEntity.ok(new ApiResponseBody("User retrieved Successfully", result));
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all user profiles in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Users retrieved successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during retrieval."
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<ApiResponseBody> getAllUsers() {
        List<UserResponseDto> result = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponseBody("Users retrieved Successfully", result));
    }

   /* @GetMapping("/authenticated")
    public ResponseEntity<ApiResponseBody> getAut(@RequestParam String username) {
        UserResponseDto result = userService.findUserByUsername(username);
        return ResponseEntity.ok(new ApiResponseBody("User retrieved Successfully", result));
    }*/
}
