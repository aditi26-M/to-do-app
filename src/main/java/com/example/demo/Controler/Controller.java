package com.example.demo.Controler;

import com.example.demo.Dto.LoginDto;
import com.example.demo.Dto.LoginResponse;
import com.example.demo.Dto.TaskDto;
import com.example.demo.Dto.UserDto;
import com.example.demo.Dto.response.FindUser;
import com.example.demo.Service.ToDoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/api/v1")
@RestController
@Slf4j
//@CrossOrigin
public class Controller {
    @Autowired
    private ToDoService toDoService;

    @PostMapping(path = "/save")
    public ResponseEntity saveUserName(@RequestBody UserDto request) {
        String id = toDoService.addUser(request);
        request.setEmail(getID());
        if (id == "ERROR")
            return new ResponseEntity("Duplicate Email", HttpStatus.BAD_REQUEST);
        return new ResponseEntity("Saved Email :" +id, HttpStatus.ACCEPTED);
    }

    @GetMapping("/generate-uuid")
    private String getID(){
        return toDoService.generateID();
    }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> LoginUser(@RequestBody LoginDto loginDto) {
        LoginResponse loginResponse = toDoService.loginUser(loginDto);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping(path = "/findalluser")
    public ResponseEntity findAllUser(@RequestParam String userName) {
        System.out.println(userName);
        Document res = toDoService.findByName(userName);
        FindUser response = new FindUser();
        response.setFirstName(res.get("firstName").toString());
        response.setFirstName(res.get("lasttName").toString());
        response.setEmail(res.get("email").toString());
        return new ResponseEntity(response, HttpStatus.ACCEPTED);
    }
    @PostMapping(path = "/addTask")
    public ResponseEntity addTask(@RequestBody TaskDto task) {
        String taskId = task.getEmail() +'_' + task.getTaskId();
        task.setTaskUUID(taskId);
        String taskname = toDoService.addTask(task);
        if (taskname == "Error")
            return new ResponseEntity("Task Id Repeated", HttpStatus.BAD_REQUEST);
        else if (taskname == "Login Invalid")
            return new ResponseEntity("Login Invalid", HttpStatus.BAD_REQUEST);
        return new ResponseEntity("Save Task:" + task.getTaskId(), HttpStatus.ACCEPTED);
    }

    @PutMapping(path = "/updateTask")
    public ResponseEntity updateTask(@RequestParam String taskDescription,
                                     @RequestParam Integer taskId){
        System.out.println(taskId);
        toDoService.updateTask(taskDescription, taskId);
        return new ResponseEntity("Task Updated:" +taskDescription, HttpStatus.ACCEPTED);
    }
    @DeleteMapping(path = "/deleteTask")
    public ResponseEntity deleteTask(@RequestParam int taskId) {
        toDoService.deleteTask(taskId);
        return new ResponseEntity("Task Deleted: " + taskId, HttpStatus.ACCEPTED);
    }
}






