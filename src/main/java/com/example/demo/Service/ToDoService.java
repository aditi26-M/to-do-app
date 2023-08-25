package com.example.demo.Service;

import com.example.demo.Dto.LoginDto;
import com.example.demo.Dto.LoginResponse;
import com.example.demo.Dto.UserDto;
import com.example.demo.Dto.TaskDto;
import org.bson.Document;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public interface ToDoService {
    LoginResponse loginUser(LoginDto loginDto);
    String addUser(UserDto userDto);
    void ConnectMongo(TaskDto taskRepo);
    String addTask(TaskDto taskRepo);
    void deleteTask(int taskId);
    String updateTask(String taskDescription, Integer taskId);
    void ConnectToMongo(UserDto employeeRepoObj);
    default String generateID(){
        return UUID.randomUUID().toString();
    }
    Document findByName(String userName);
}


