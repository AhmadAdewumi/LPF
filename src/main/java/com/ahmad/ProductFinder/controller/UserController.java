package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.controller.swaggerDocs.UserDocs;
import com.ahmad.ProductFinder.dtos.request.ChangePasswordRequest;
import com.ahmad.ProductFinder.dtos.request.CreateUserRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateUserRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.UserResponseDto;
import com.ahmad.ProductFinder.service.userService.IUserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for user registration, profile management, and retrieval.")
public class UserController implements UserDocs {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }


    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> registerUser(@RequestBody @Valid CreateUserRequestDto request) {
        UserResponseDto result = userService.registerUser(request);
        return ResponseEntity.ok(
                new ApiResponseBody("User registered successfully. Please check your email to verify your account.", result)
        );
    }

    @PatchMapping(value = "/update/{userId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> updateUser(@RequestBody UpdateUserRequestDto request, @PathVariable Long userId) {
        UserResponseDto result = userService.updateUser(request, userId);
        return ResponseEntity.ok(new ApiResponseBody("User updated Successfully", result));
    }

//    @PatchMapping(value = "/passwordReset" , consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponseBody> resetPassword(@RequestBody ChangePasswordRequest request){
//        UserResponseDto result = userService.resetPassword(request);
//        return ResponseEntity.ok(new ApiResponseBody("Password reset successfully!",result));
//    }


    @PatchMapping("/disable/{userId}")
    public ResponseEntity<ApiResponseBody> disableUserByUserId(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponseBody("User deactivated Successfully", null));
    }

    @Hidden
    @PatchMapping(value = "/restore/{userId}")
    public ResponseEntity<ApiResponseBody> restoreUser(@PathVariable Long userId) {
        log.info("Restore User endpoint called");
        UserResponseDto result = userService.restoreUser(userId);
        log.info("User restored successfully");
        return ResponseEntity.ok(new ApiResponseBody("User restored Successfully", result));
    }

    @DeleteMapping("/delete/fr/{userId}")
    public ResponseEntity<ApiResponseBody> deleteUserForReal(@PathVariable Long userId) {
        userService.deleteUserForReal(userId);
        return ResponseEntity.ok(new ApiResponseBody("User deleted Successfully", null));
    }


    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseBody> getUserById(@PathVariable Long userId) {
        UserResponseDto result = userService.findUserByUserId(userId);
        return ResponseEntity.ok(new ApiResponseBody("User retrieved Successfully", result));
    }


    @GetMapping("/username")
    public ResponseEntity<ApiResponseBody> getUserByUsername(@RequestParam String username) {
        UserResponseDto result = userService.findUserByUsername(username);
        return ResponseEntity.ok(new ApiResponseBody("User retrieved Successfully", result));
    }

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
