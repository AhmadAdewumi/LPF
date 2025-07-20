package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.service.userService.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/register")
public class RegistrationVerificationController {
    @Value("${app.frontend.login-url}")
    private String loginUrl;

    @Value("${app.frontend.registration-url}")
    private String registrationUrl;

    private final IUserService userService;

    public RegistrationVerificationController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/verify")
    public void verifyEmail(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        if (token == null || token.isBlank()){
            log.warn("Empty token provided");
            response.sendRedirect(registrationUrl + "?error=invalid_token");
            return;
        }
        boolean isVerified = userService.verifyUser(token);

        if (isVerified) {
            log.info("Successfully verified token: {}", token);
            response.sendRedirect(loginUrl + "?verified=true");
        } else {
            log.warn("Failed verification for token: {}", token);
            response.sendRedirect(registrationUrl + "?error=verification_failed");
        }
    }

}
