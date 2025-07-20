package com.ahmad.ProductFinder.service.userService;

import com.ahmad.ProductFinder.dtos.request.ChangePasswordRequest;
import com.ahmad.ProductFinder.dtos.request.CreateUserRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateUserRequestDto;
import com.ahmad.ProductFinder.dtos.response.UserResponseDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.AlreadyExistsException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.IllegalArgumentException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.InvalidTokenException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.mailing.AccountVerificationEmailContext;
import com.ahmad.ProductFinder.mailing.EmailService;
import com.ahmad.ProductFinder.models.Role;
import com.ahmad.ProductFinder.models.SecureToken;
import com.ahmad.ProductFinder.models.User;
import com.ahmad.ProductFinder.repositories.RoleRepository;
import com.ahmad.ProductFinder.repositories.UserRepository;
import com.ahmad.ProductFinder.security.user.LPFUserDetails;
import com.ahmad.ProductFinder.service.secureTokenService.SecureTokenService;
import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

/***
 * A VERY BIG THING TO NOTE
 * I AM SETTING ACCOUNT VERIFIED TO TRUE FOR NOW IN THE DB, BUT I MUST AMKE SURE I HANDLE THAT AUTOMATICALLY AFTER THE ...
 * ... VERIFIES HIS/HER EMAIL WHEN REGISTERING.
 */
@Slf4j
@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SecureTokenService secureTokenService;
    private final EmailService emailService;

    @Value("${site.base.url.https}")
    private String baseURL;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, SecureTokenService secureTokenService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.secureTokenService = secureTokenService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public UserResponseDto registerUser(CreateUserRequestDto request) {
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

                    //THIS FOR ASSIGNING DEFAULT ROLE "USER" ON SIGN UP, CAN UPGRADE TO STORE_OWNER ON STORE CREATION
                  /*  Role customerRole = roleRepository.findByName("USER")
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
                    user.setRoles(Set.of(customerRole));*/

                    Set<Role> rolesFromDb = req.getRole().stream().map(roleName ->
                            roleRepository.findByName(roleName)
                                    .orElseThrow(() -> new ResourceNotFoundException(format("Role with name(s),%s , not found!", roleName)))
                    ).collect(Collectors.toSet());
                    user.setRoles(rolesFromDb);
                    user.setPhoneNumber(req.getPhoneNumber());
                    user.setCreatedAt(LocalDateTime.now());
                    userRepository.save(user);
                    sendRegistrationConfirmationEmail(user);

                    log.info("User created successfully with username: {}", user.getUsername());
                    return UserResponseDto.from(user);
                }).orElseThrow(() -> {
                    log.warn("User creation failed - email or username already exists");
                    return new AlreadyExistsException(format("User with email,%s ,or username,%s , already exist!", request.getEmail(), request.getUsername()));
                });
    }


    @Override
    public UserResponseDto updateUser(UpdateUserRequestDto request, Long userId) {
        log.info("In update user service method");
        log.info("Attempting to update user with ID: {}", userId);
        return userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setFirstName(request.getFirstName());
                    existingUser.setLastname(request.getLastName());
                    existingUser.setPhoneNumber(request.getPhoneNumber());
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    existingUser.setPassword(passwordEncoder.encode(request.getPassword()));

                    userRepository.save(existingUser);
                    log.info("User with ID {} updated successfully", userId);
                    return UserResponseDto.from(existingUser);
                })
                .orElseThrow(() -> {
                    log.warn("Update failed - user with ID {} not found", userId);
                    return new ResourceNotFoundException(format("User with ID:%d , Not Found", userId));
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
        }).orElseThrow(() -> {
            log.warn("Soft delete failed - user with ID {} not found", userId);
            return new ResourceNotFoundException(format("User with ID:%d , Not Found", userId));
        });
    }

    @Override
    public void deleteUserForReal(Long userId) {
        log.info("In delete user for real service method");
        log.info("Attempting to permanently delete user with ID: {}", userId);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Permanent delete failed - user with ID {} not found", userId);
            return new ResourceNotFoundException(format("User with ID:%d , Not Found", userId));
        });
        userRepository.deleteById(userId);
        log.info("User with ID {} permanently deleted", userId);
    }

    @Override
    public UserResponseDto restoreUser(Long userId) {
        log.info("Attempting to restore user with ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Restore user failed - user with ID {} not found", userId);
            return new ResourceNotFoundException(format("User with ID:%d , Not Found", userId));
        });

        user.setActive(true);
        userRepository.save(user);
        log.info("User with ID: {} restored successfully!", userId);
        return UserResponseDto.from(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
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
    public UserResponseDto findUserByUserId(Long userId) {
        log.info("Im find User by id service method");
        log.info("Searching for user by ID: {}", userId);
        User user = userRepository.findByIdAndActiveTrue(userId).orElseThrow(() -> {
            log.warn("User with ID {} not found", userId);
            return new ResourceNotFoundException(format("User with ID:%d , Not Found", userId));
        });
        log.info("User found with ID: {}", userId);
        return UserResponseDto.from(user);
    }

    @Override
    public boolean getAuthenticatedUser(String usernameParam) { //username parameter is different from that got from authentication
        log.info("In get authenticated user service method");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAuthenticated = username.equals(usernameParam) && userRepository.existsByUsernameAndActiveTrue(username);
        log.debug("Authenticated user check - Provided: {}, Authenticated: {}, Result: {}", usernameParam, username, isAuthenticated);
        return isAuthenticated;
    }

    @Override
    public void sendRegistrationConfirmationEmail(User user) {
        SecureToken secureToken = secureTokenService.createToken();
        secureToken.setUser(user);
        secureTokenService.saveSecureToken(secureToken);

        AccountVerificationEmailContext verificationEmailContext = new AccountVerificationEmailContext();
        verificationEmailContext.init(user);
        verificationEmailContext.setToken(secureToken.getToken());
        verificationEmailContext.buildVerificationUrl(baseURL, secureToken.getToken());

        try {
            emailService.sendEmail(verificationEmailContext);
        } catch (MessagingException | jakarta.mail.MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyUser(String token) {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if (Objects.isNull(token) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()) {
            throw new InvalidTokenException("Token is not valid!");

        }
        User user = userRepository.findById(secureToken.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(format("User with ID:%d , Not Found", secureToken.getUser().getId())));
        if (Objects.isNull(user)) {
            return false;
        }

        user.setAccountVerified(true);
        userRepository.save(user);
        secureTokenService.removeToken(secureToken);
        return true;
    }

//    @Override
//    public UserResponseDto resetPassword(ChangePasswordRequest request) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        LPFUserDetails userDetails = (LPFUserDetails) authentication.getPrincipal();
//        User user = userDetails.getUser();
//        //        var user = (LPFUserDetails) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
//        //check if the current password matches existing password
//        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
//            throw new IllegalArgumentException("Current password does not match your existing password!");
//        }
//        // check if the new password and confirmation password field matches
//        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
//            throw new IllegalArgumentException("New password and Confirm password doesn't match");
//        }
//
//        //update the password
//        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        //save the password in the repository
//        userRepository.save(user);
//
//        return UserResponseDto.from(user);
//    }
}
