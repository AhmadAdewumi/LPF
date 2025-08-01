package com.ahmad.ProductFinder.security.config;

import com.ahmad.ProductFinder.security.jwt.JWTAuthTokenFilter;
import com.ahmad.ProductFinder.security.jwt.JwtAuthEntryPoint;
import com.ahmad.ProductFinder.security.jwt.JwtUtils;
import com.ahmad.ProductFinder.security.user.LPFUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class LPFconfig {
    private final LPFUserDetailsService lpfUserDetailsService;
    private final JwtUtils jwtUtils;
    private final JwtAuthEntryPoint authEntryPoint;

    public LPFconfig(LPFUserDetailsService lpfUserDetailsService, JwtUtils jwtUtils, JwtAuthEntryPoint authEntryPoint) {
        this.lpfUserDetailsService = lpfUserDetailsService;
        this.jwtUtils = jwtUtils;
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthTokenFilter authTokenFilter() {
        return new JWTAuthTokenFilter(jwtUtils, lpfUserDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(lpfUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            throw accessDeniedException;
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(
//                                "/**"
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/"
                        ).permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/store/**").hasAnyRole("ADMIN", "STORE_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/store/**").hasAnyRole("ADMIN", "STORE_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/user/**").hasAnyRole("ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated());
        http.authenticationProvider(daoAuthenticationProvider());
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
