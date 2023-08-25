package com.example.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "todoList")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDto {
    private String email;
    private String password;

    @Override
    public String toString() {
        return "LoginDto{" +
                "email='" + email +
                ", password='" + password +
                '}';
    }
}
