package com.ahmad.ProductFinder.service.authService;

import com.ahmad.ProductFinder.dtos.request.LoginRequest;
import com.ahmad.ProductFinder.dtos.request.RefreshTokenRequest;
import com.ahmad.ProductFinder.dtos.response.JwtResponse;
import com.ahmad.ProductFinder.security.jwt.JwtUtils;
import com.ahmad.ProductFinder.security.user.LPFUserDetails;
import com.ahmad.ProductFinder.security.user.LPFUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final LPFUserDetailsService userDetailsService;

    public JwtResponse authenticate(LoginRequest request) {
        log.info("Authentication attempt for username: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Authentication successful for username: {}", request.getUsername());
        String accessToken = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        LPFUserDetails userDetails = (LPFUserDetails) authentication.getPrincipal();
        log.info("Tokens generated for user ID: {}", userDetails.getId());

        return new JwtResponse(userDetails.getId(), accessToken, refreshToken);
    }

    public JwtResponse refreshAccessToken(String refreshToken) {
//        String refreshToken = request.getRefreshToken();
        log.info("Refresh token request received.");

        if (!jwtUtils.validateToken(refreshToken)) {
            log.warn("Invalid or expired refresh token.");
            return null;
        }

        String username = jwtUtils.extractUsernameFromToken(refreshToken);
        log.debug("Extracted username '{}' from refresh token.", username);

        LPFUserDetails userDetails = (LPFUserDetails) userDetailsService.loadUserByUsername(username);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String newAccessToken = jwtUtils.generateAccessToken(auth);

        log.info("Access token refreshed for user: {}", username);
        return new JwtResponse(userDetails.getId(), newAccessToken, null);
    }
}
