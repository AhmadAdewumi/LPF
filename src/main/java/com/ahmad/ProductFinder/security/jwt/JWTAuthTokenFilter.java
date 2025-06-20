package com.ahmad.ProductFinder.security.jwt;

import com.ahmad.ProductFinder.security.user.LPFUserDetails;
import com.ahmad.ProductFinder.security.user.LPFUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTAuthTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final LPFUserDetailsService userDetailsService;

    public JWTAuthTokenFilter(JwtUtils jwtUtils, LPFUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /**
         * 1. Extract bearer token
         * 2. validate token
         * 3. extract username from token
         * 4. load user from database
         * 5. confirm token belong to the loaded user and the token isn't expired
         * 6. build authentication objects using roles , password(null) and authorities(UsernamePasswordAuthentication object
         * 7. forward to security context and put it there
         */

        try {
            String jwtToken = parseJwt(request);
            if (StringUtils.hasText(jwtToken)) {
                jwtUtils.validateToken(jwtToken);

                String username = jwtUtils.extractUsername(jwtToken);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtils.isTokenValid((LPFUserDetails) userDetails, jwtToken)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or Expired JWT: " + e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Error occurred while processing JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);

    }

    private String parseJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
