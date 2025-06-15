package com.ahmad.ProductFinder.security.user;

import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.User;
import com.ahmad.ProductFinder.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;

@Service
public class LPFUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public LPFUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = Optional.ofNullable(userRepository.findByUsernameAndActiveTrue(username))
                .orElseThrow(()-> new ResourceNotFoundException(format("USER WITH USERNAME :, %s , NOT FOUND!",username)));
        return LPFUserDetails.buildUserDetails(user);
    }
}
