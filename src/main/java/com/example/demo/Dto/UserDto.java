package com.example.demo.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean loginStatus;
    private String sessionExpiry;

    @Override
    public String toString() {
        return "UserDto{" +
                ", firstName='" + firstName +
                ", lastName='" + lastName +
                ", email='" + email +
                ", password='" + password +
                ", loginStatus='"+ loginStatus +
                ", sessionExpiry='"+ sessionExpiry +
                '}';
    }
}
