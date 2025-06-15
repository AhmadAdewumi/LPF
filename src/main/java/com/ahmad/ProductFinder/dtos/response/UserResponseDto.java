package com.ahmad.ProductFinder.dtos.response;

import com.ahmad.ProductFinder.models.Role;
import com.ahmad.ProductFinder.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private Collection<Role> role;

    public static UserResponseDto from(User user){
        return new UserResponseDto(
                user.getFirstName(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }
}
