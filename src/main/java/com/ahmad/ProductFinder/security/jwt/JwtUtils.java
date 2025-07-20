package com.ahmad.ProductFinder.security.jwt;

import com.ahmad.ProductFinder.security.user.LPFUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtUtils {
    @Value("${jwt.token.jwtSecret}")
    private String jwtSecret;

    /**
     * Access tokens for short time access , to reduce the risk of compromising(short-lived) and
     * refresh tokens for the user to re authenticate (long-lived)
     */

    @Value("${jwt.refresh_token.expirationTime}")
    private long refreshTokenExpirationTime;

    @Value("${jwt.access_token.expirationTime}")
    private long accessTokenExpirationTime;

    Date now = new Date();


//    public String generateTokenForUser(Authentication authentication) {
//        LPFUserDetails userPrincipal = (LPFUserDetails) authentication.getPrincipal();
//        List<String> roles = userPrincipal.getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority).toList();
//
//        return Jwts.builder()
//                .setSubject(userPrincipal.getUsername())
//                .claim("id", userPrincipal.getId())
//                .claim("roles", roles)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(new Date().getTime() + refreshTokenExpirationTime))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
//
//    }

    public String generateAccessToken(Authentication auth) {
        LPFUserDetails userPrincipal = (LPFUserDetails) auth.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();

        log.debug("Generating access token for user: {}", userPrincipal.getUsername());
        Date expiration = new Date(now.getTime() + accessTokenExpirationTime);
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(Authentication auth) {
        LPFUserDetails user = (LPFUserDetails) auth.getPrincipal();
        Date expiration = new Date(now.getTime() + refreshTokenExpirationTime);

        log.debug("Generating refresh token for user: {}", user.getUsername());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            return false;
//            throw new JwtException("JWT validation failed : " + e.getMessage());
        }
    }

    public String extractUsernameFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(LPFUserDetails userDetails, String token) {
        final String username = extractUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
