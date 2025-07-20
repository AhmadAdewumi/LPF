package com.ahmad.ProductFinder.mailing;

import com.ahmad.ProductFinder.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

public class PasswordResetEmailContext extends AbstractEmailContext{
    private String token;

    @Value("${sender.email}")
    private String sendersMail;

    @Override
    public <T> void init(T context) {
        User user = (User) context;

        put("firstName", user.getFirstName());
        put("resetLink", "http://localhost:8080/api/v1/auth/password-reset");
        setTemplateLocation("mailing/password-reset-request");
        setSubject("Complete your password reset request");
        setFrom(sendersMail);
        setTo(user.getEmail());
    }

    public void setToken(String token){
        this.token=token;
        put("token",token);
    }


    public void buildVerificationUrl(final String baseURL,final String token){
        final String url = UriComponentsBuilder
                .fromHttpUrl(baseURL)
                .path("/password/reset")
                .queryParam("token",token)
                .toUriString();
        put("verificationURL",url);
    }
}
