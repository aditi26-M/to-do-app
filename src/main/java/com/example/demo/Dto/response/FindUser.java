package com.example.demo.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FindUser {
    private String firstName;
    private String lastName;
    private String email;
//    private Map<Integer, String> listOfTasks;
}
