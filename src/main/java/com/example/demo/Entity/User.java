package com.example.demo.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Document(collection = "todoList")
@NoArgsConstructor
@AllArgsConstructor
@Data

@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "User_id")
    private int userID;
    @Column(name = "User_name")
    private String userName;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;

    @Override
    public String toString() {
        return "Employee{" +
                "UserId =" + userID +
                ", UserName='" + userName +
                ", email='" + email +
                ", password='" + password +
                '}';
    }
}
