package com.ahmad.ProductFinder.service.userService;

import com.ahmad.ProductFinder.dtos.request.CreateUserRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateUserRequestDto;
import com.ahmad.ProductFinder.dtos.response.UserResponseDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.AlreadyExistsException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.User;
import com.ahmad.ProductFinder.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@Service
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto createUser(CreateUserRequestDto request) {
        log.info("Attempting to create user with email: {} and username: {}", request.getEmail(), request.getUsername());
        return Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail())
                        && !userRepository.existsByUsername(request.getUsername()))
                .map(req -> {
                    User user = new User();
                    user.setFirstName(req.getFirstName());
                    user.setLastname(req.getLastName());
                    user.setPassword(passwordEncoder.encode(req.getPassword()));
                    user.setUsername(req.getUsername());
                    user.setEmail(req.getEmail());
                    user.setRole(req.getRole());
                    user.setPhoneNumber(req.getPhoneNumber());
                    user.setCreatedAt(LocalDateTime.now());
                    userRepository.save(user);

                    log.info("User created successfully with username: {}", user.getUsername());
                    return UserResponseDto.from(user);
                }).orElseThrow(() -> {
                    log.warn("User creation failed - email or username already exists");
                    return new AlreadyExistsException(format("User with email,%s ,or username,%s , already exist!",request.getEmail(),request.getUsername()));
                });
    }

    @Override
    public UserResponseDto updateUser(UpdateUserRequestDto request, Long userId) {
        log.info("In update user service method");
        log.info("Attempting to update user with ID: {}", userId);
        return userRepository.findById(userId)
                .map(existingUser->{
                    existingUser.setFirstName(request.getFirstName());
                    existingUser.setLastname(request.getLastName());
                    existingUser.setPhoneNumber(request.getPhoneNumber());
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    existingUser.setPassword(passwordEncoder.encode(request.getPassword()));

                    userRepository.save(existingUser);
                    log.info("User with ID {} updated successfully", userId);
                    return UserResponseDto.from(existingUser);
                })
                .orElseThrow(()-> {
                    log.warn("Update failed - user with ID {} not found", userId);
                    return new ResourceNotFoundException(format("User with ID:%d , Not Found",userId));
                });
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("In delete user service method");
        log.info("Attempting to soft delete user with ID: {}", userId);
        userRepository.findById(userId).map(user -> {
            user.setActive(false);
            log.info("User with ID {} marked as inactive", userId);
            return userRepository.save(user);
        }).orElseThrow(()-> {
            log.warn("Soft delete failed - user with ID {} not found", userId);
            return new ResourceNotFoundException(format("User with ID:%d , Not Found",userId));
        });
    }

    @Override
    public void deleteUserForReal(Long userId) {
        log.info("In delete user for real service method");
        log.info("Attempting to permanently delete user with ID: {}", userId);
        userRepository.findById(userId).orElseThrow(()-> {
            log.warn("Permanent delete failed - user with ID {} not found", userId);
            return new ResourceNotFoundException(format("User with ID:%d , Not Found",userId));
        });
        userRepository.deleteById(userId);
        log.info("User with ID {} permanently deleted", userId);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("In get all users service method");
        log.info("Fetching all active users");
        List<UserResponseDto> users = userRepository.findAllByActiveTrue()
                .stream()
                .map(UserResponseDto::from).toList();
        log.info("Fetched {} active users", users.size());
        return users;
    }

    @Override
    public UserResponseDto findUserByUsername(String username) {
        log.info("In find by username service method");
        log.info("Searching for user by username: {}", username);
        User user = userRepository.findByUsernameAndActiveTrue(username);
        log.info("User found with username: {}", username);
        return UserResponseDto.from(user);
    }

    @Override
    public UserResponseDto findUserById(Long userId) {
        log.info("Im find User by id service method");
        log.info("Searching for user by ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(()-> {
            log.warn("User with ID {} not found", userId);
            return new ResourceNotFoundException(format("User with ID:%d , Not Found",userId));
        });
        log.info("User found with ID: {}", userId);
        return UserResponseDto.from(user);
    }

    @Override
    public boolean getAuthenticatedUser(String usernameParam) {
        log.info("In get authenticated user service method");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAuthenticated = username.equals(usernameParam) && userRepository.existsByUsernameAndActiveTrue(username);
        log.debug("Authenticated user check - Provided: {}, Authenticated: {}, Result: {}", usernameParam, username, isAuthenticated);
        return isAuthenticated;
    }
}
