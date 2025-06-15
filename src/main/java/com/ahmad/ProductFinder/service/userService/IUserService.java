package com.ahmad.ProductFinder.service.userService;

import com.ahmad.ProductFinder.dtos.request.CreateUserRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateUserRequestDto;
import com.ahmad.ProductFinder.dtos.response.UserResponseDto;

import java.util.List;

public interface IUserService {
    UserResponseDto createUser(CreateUserRequestDto request);
    UserResponseDto updateUser(UpdateUserRequestDto request,Long userId);
    void deleteUser(Long userId);
    void deleteUserForReal(Long userId);

    UserResponseDto restoreUser(Long userId);

    List<UserResponseDto> getAllUsers();
    UserResponseDto findUserByUsername(String username);
    UserResponseDto findUserByUserId(Long userId);
    boolean getAuthenticatedUser(String usernameParam);
}
