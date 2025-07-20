package com.ahmad.ProductFinder.dtos.response;

import com.ahmad.ProductFinder.models.Role;
import com.ahmad.ProductFinder.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private List<String> role;


    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getFirstName(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                user.getPhoneNumber(),
                user.getRoles().stream().map(userRole -> userRole.getName()).collect(Collectors.toList())
        );
    }
}
