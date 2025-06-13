package com.ahmad.ProductFinder.security.jwt;

import com.ahmad.ProductFinder.security.user.LPFUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    @Value("${jwt.token.jwtSecret}")
    private String jwtSecret;

    /**
     * Access tokens for short time access , to reduce the risk of compromising(short-lived) and
     * refresh tokens for the user to re authenticate (long-lived)
     */
//    @Value("${jwt.access_token.expirationTime}")
//    private Duration accessTokenExpirationTime;

    @Value("${jwt.refresh_token.expirationTime}")
    private int refreshTokenExpirationTime;

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshTokenExpirationTime);

    //    private String buildToken(LPFUserDetails userDetails,Duration expiration){
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime()+expiration.toMillis());
//
//        Collection<String> roles = userDetails.getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .toList();
//
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .claim("id",userDetails.getId())
//                .claim("authorities",roles)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
//                .compact();
//    }
    public String generateTokenForUser(Authentication authentication) {
        LPFUserDetails userPrincipal = (LPFUserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + refreshTokenExpirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();

    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    //FOR ACCESS TOKEN IN THE FUTURE
//    public String buildAccessToken(LPFUserDetails userDetails){
//        return buildToken(userDetails,accessTokenExpirationTime);
//    }

    // THIS CAN BE USED FOR REFRESH TOKEN IN FUTURE , FOR FORCE RE-LOGIN
//    public String generateTokenForUser(LPFUserDetails userDetails){
//        return buildToken(userDetails,refreshTokenExpirationTime);
//    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            throw new JwtException("JWT validation failed : " + e.getMessage());
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(LPFUserDetails userDetails, String token) {
        final String username = extractUsername(token);
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
