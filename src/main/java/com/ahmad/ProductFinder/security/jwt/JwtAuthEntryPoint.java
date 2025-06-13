package com.ahmad.ProductFinder.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("message" , "You may login and try again!");
        body.put("path" , request.getServletPath());
        body.put("error" , "Unauthorized");
        body.put("exception" , authException.getMessage());


        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(),body);
        } catch (IOException e) {
            response.reset();
            throw e;
        }
    }
}
