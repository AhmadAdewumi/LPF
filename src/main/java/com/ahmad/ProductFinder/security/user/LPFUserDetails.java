package com.ahmad.ProductFinder.security.user;

import com.ahmad.ProductFinder.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class LPFUserDetails implements UserDetails {
    private final User user;
    private final Long id;
    private final String username;
    @JsonIgnore
    private final String password;
    private final boolean accountVerified;

    private Collection<SimpleGrantedAuthority> authorities;

    public LPFUserDetails(User user, Long id, String username, String password, boolean accountVerified, Collection<SimpleGrantedAuthority> authorities) {
        this.user = user;
        this.id = id;
        this.username = username;
        this.password = password;
        this.accountVerified = accountVerified;
        this.authorities = authorities;
    }

//    public LPFUserDetails(User user){
//        this.user = user;
//    }

    public static LPFUserDetails buildUserDetails(User user) {
        Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role.getName()))
                .collect(Collectors.toSet());

        return new LPFUserDetails(
                user,
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.isAccountVerified(),
                authorities
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.accountVerified;
    }
}
