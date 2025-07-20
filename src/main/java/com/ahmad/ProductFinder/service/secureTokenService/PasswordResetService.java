package com.ahmad.ProductFinder.service.secureTokenService;

import com.ahmad.ProductFinder.dtos.request.PasswordResetDto;
import com.ahmad.ProductFinder.dtos.request.PasswordResetRequestDto;
import com.ahmad.ProductFinder.enums.TokenType;
import com.ahmad.ProductFinder.mailing.EmailService;
import com.ahmad.ProductFinder.mailing.PasswordResetEmailContext;
import com.ahmad.ProductFinder.models.SecureToken;
import com.ahmad.ProductFinder.models.User;
import com.ahmad.ProductFinder.repositories.UserRepository;
import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.thymeleaf.context.Context;

@Slf4j
@Service
public class PasswordResetService {
    private final SecureTokenService secureTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${site.base.url.https}")
    private String baseURL;

    public PasswordResetService(SecureTokenService secureTokenService, UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.secureTokenService = secureTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void resetPasswordRequest(@ModelAttribute PasswordResetRequestDto requestDto) {
        log.info("Password reset request received for: {}", requestDto.getEmail());

        userRepository.findByEmail(requestDto.getEmail()).ifPresent(
                user -> {
                    SecureToken token = secureTokenService.createToken();
                    token.setUser(user);
                    token.setTokenType(TokenType.PASSWORD_RESET);
                    secureTokenService.saveSecureToken(token);

                    Context context = new Context();
                    context.setVariable("username", user.getUsername());
                    context.setVariable("resetLink",
                            "https://lpf.com/reset-password.html?token=" + token.getToken()
                    );

                    PasswordResetEmailContext passwordResetEmailContext = new PasswordResetEmailContext();
                    passwordResetEmailContext.init(user);
                    passwordResetEmailContext.setToken(token.getToken());
                    passwordResetEmailContext.buildVerificationUrl(baseURL, token.getToken());

                    try {
                        emailService.sendEmail(passwordResetEmailContext);
                        log.info("Password reset email queued for {}", user.getEmail());
                    } catch (MessagingException | jakarta.mail.MessagingException e) {
                        log.info("Reset Password mail not sent for, email with : {}", requestDto.getEmail());
                        e.printStackTrace();
                    }
                }
        );
    }

    public void resetPassword(PasswordResetDto dto) {
        SecureToken token = secureTokenService.findByToken(dto.getToken());
        if (token == null || token.isExpired() || token.getTokenType() != TokenType.PASSWORD_RESET) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match , new password and confirm password must be equal!");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        secureTokenService.removeToken(token);
        log.info("Password reset successful for userId = {}", user.getId());
    }
}
