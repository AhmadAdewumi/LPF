package com.ahmad.ProductFinder.service.userService;

import com.ahmad.ProductFinder.dtos.request.ChangePasswordRequest;
import com.ahmad.ProductFinder.dtos.request.CreateUserRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateUserRequestDto;
import com.ahmad.ProductFinder.dtos.response.UserResponseDto;
import com.ahmad.ProductFinder.models.User;

import java.security.Principal;
import java.util.List;

public interface IUserService {
    UserResponseDto registerUser(CreateUserRequestDto request);
    UserResponseDto updateUser(UpdateUserRequestDto request,Long userId);
    void deleteUser(Long userId);
    void deleteUserForReal(Long userId);

    UserResponseDto restoreUser(Long userId);

    List<UserResponseDto> getAllUsers();
    UserResponseDto findUserByUsername(String username);
    UserResponseDto findUserByUserId(Long userId);
    boolean getAuthenticatedUser(String usernameParam);

    void sendRegistrationConfirmationEmail(User user);

    boolean verifyUser(String token);

//    UserResponseDto resetPassword(ChangePasswordRequest request);
}
