package com.example.demo.Service.impl;

import com.example.demo.Dto.LoginDto;
import com.example.demo.Dto.LoginResponse;
import com.example.demo.Dto.UserDto;
import com.example.demo.Dto.TaskDto;
import com.example.demo.Service.ToDoService;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ToDoServiceImpl implements ToDoService {

    @Value("${spring.data.mongodb.uri}")
    String mongoURI;
    @Value("${spring.data.mongodb.database}")
    String dbname;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //-----------------------Register User----------------------------
    @Override
    public String addUser(UserDto addUserReq) {
        UserDto addUserObj = new UserDto();
        MongoClient mongoClient = MongoClients.create(mongoURI);
        MongoDatabase database = mongoClient.getDatabase(dbname);
        MongoCollection<Document> collection = database.getCollection("todoList");
        Document query = collection.find(new Document("email", addUserReq.getEmail())).first();
//        System.out.println(query);
        if (query == null) {
            String plainPassword = addUserReq.getPassword();

            // Validate password complexity
            if (!isValidPassword(plainPassword)) {
                return "INVALID_PASSWORD";
            }

            // Hash the password
            String hashedPassword = hashPassword(plainPassword);

            addUserObj.setEmail(addUserReq.getEmail());
            addUserObj.setFirstName(addUserReq.getFirstName());
            addUserObj.setFirstName(addUserReq.getLastName());
            addUserObj.setPassword(hashedPassword);

            //connect to mongodb
            ConnectToMongo(addUserObj);
            return addUserObj.getEmail();
        }
        System.out.println("The email: " + addUserReq.getEmail() + " is already there");
        return "ERROR";
    }

    private boolean isValidPassword(String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{5,10}$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(password);
        return matcher.matches();
    }

    private String hashPassword(String plainPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(plainPassword);
    }


    // ----------------------------------Connection to Mongo for users-----------------------------
    @Override
    public void ConnectToMongo (UserDto userRepoObj){
        MongoClient mongoClient = MongoClients.create(mongoURI);
        MongoDatabase database = mongoClient.getDatabase(dbname);
        MongoCollection<Document> collection = database.getCollection("todoList");
        // Create a new document to insert into the collection
        Document doc = new Document();
        doc.put("firstName", userRepoObj.getFirstName());
        doc.put("lastName", userRepoObj.getLastName());
        doc.put("email", userRepoObj.getEmail());
        doc.put("password", userRepoObj.getPassword());
        // Insert the document into the collection
        collection.insertOne(doc);
        log.info("Document inserted successfully");
    }


    // --------------------------Find the user by name--------------------------------------------
    @Override
    public Document findByName (String userName){
        MongoClient mongoClient = MongoClients.create(mongoURI);
        MongoDatabase database = mongoClient.getDatabase("myDatabase");
        MongoCollection<Document> findData = database.getCollection("todoList");
        Document query = new Document("firstName", userName);
//        Document query = new Document("lastName", userName);
        Document result = findData.find(query).first();
        if (result != null) {
            System.out.println("Fetched record:");
            System.out.println(result);
        } else {
            System.out.println("Record not found.");
        }
        return result;
    }

    // -------------------------------Login User-----------------------------------------------
    @Override
    public LoginResponse loginUser(LoginDto loginDto) {
        try (MongoClient mongoClient = MongoClients.create(mongoURI)) {
            MongoDatabase database = mongoClient.getDatabase("myDatabase");
            MongoCollection<Document> findData = database.getCollection("todoList");
            Document user1 = findData.find(new Document("email", loginDto.getEmail())).first();
            if (user1 != null) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String correctEncodedPassword = user1.get("password", String.class);
                String enteredPassword = loginDto.getPassword();
                boolean isPwdRight = encoder.matches(enteredPassword, correctEncodedPassword);
                if (isPwdRight) {
                    Map<String, Object> loginStatus = new HashMap<>();
                    loginStatus.put("email", loginDto.getEmail());
                    loginStatus.put("status", true);
                    loginStatus.put("loginTime", System.currentTimeMillis());
                    database.getCollection("loginStatus").insertOne(new Document(loginStatus));
                    return new LoginResponse("Login Success", true);
                }
                else {
                    return new LoginResponse("Password Not Match", false);
                }

            } else {
                return new LoginResponse("Email not exists", false);
            }

        } catch (Exception e) {
            log.error("An error occurred while logging in: {}", e.getMessage());
            return new LoginResponse("Error occurred during login", false);
        }
    }

    // ----------------------------------Connection to Mongo for login users------------------------------
    public void MongoConnection(LoginDto loginRepoObj) {
        MongoClient mongoClient = MongoClients.create(mongoURI);
        MongoDatabase database = mongoClient.getDatabase("myDatabase");
        MongoCollection<Document> collection = database.getCollection("todoList");
        Document doc = new Document();
        doc.put("email", loginRepoObj.getEmail());
        doc.put("password",loginRepoObj.getPassword());

        collection.insertOne(doc);
        log.info("User Logged In");
    }

    //-----------------------------------Add Tasks-------------------------------------
    @Override
    public String addTask (TaskDto addTaskReq){
        MongoClient mongoClient = MongoClients.create(mongoURI);
        MongoDatabase database = mongoClient.getDatabase(dbname);
        Document loginStatus = database.getCollection("loginStatus").find(new Document("email", addTaskReq.getEmail())).first();
        System.out.println(loginStatus);
        if (loginStatus == null || (Boolean) loginStatus.get("status") == false || (Long) loginStatus.get("loginTime") < System.currentTimeMillis() - 10 * 60 * 1000) {
//            System.out.println("Login Invalid");
            return "Login Invalid";
        }

        TaskDto taskRepoObj = new TaskDto();

        MongoCollection<Document> collection = database.getCollection("taskList");
        Document query = collection.find(new Document("taskUUID", addTaskReq.getTaskUUID())).first();
        System.out.println(query);
        if (query == null) {
            taskRepoObj.setTaskId(addTaskReq.getTaskId());
            taskRepoObj.setTaskDescription(addTaskReq.getTaskDescription());
            taskRepoObj.setEmail(addTaskReq.getEmail());
            taskRepoObj.setStartTime(addTaskReq.getStartTime());
            taskRepoObj.setEndTime(addTaskReq.getEndTime());
            taskRepoObj.setTaskUUID(addTaskReq.getTaskUUID());
            //connect to mongodb
            ConnectMongo(taskRepoObj);
            return taskRepoObj.getTaskUUID();
        }
        return "Error";
    }

    // ----------------------------------Connection to Mongo for tasks---------------------------
    @Override
    public void ConnectMongo (TaskDto taskRepoObj){
        MongoClient mongoClient = MongoClients.create(mongoURI);
        MongoDatabase database = mongoClient.getDatabase(dbname);
        MongoCollection<Document> collection = database.getCollection("taskList");
        // Create a new document to insert into the collection
        Document doc = new Document();
        doc.put("taskId", taskRepoObj.getTaskId());
        doc.put("taskDescription", taskRepoObj.getTaskDescription());
        doc.put("email", taskRepoObj.getEmail());
        doc.put("taskUUID", taskRepoObj.getTaskUUID());

        Document start = new Document();
        start.put("hr", taskRepoObj.getStartTime().getHr());
        start.put("min", taskRepoObj.getStartTime().getMin());
        doc.put("startTime", start);

        Document end = new Document();
        end.put("hr", taskRepoObj.getEndTime().getHr());
        end.put("min", taskRepoObj.getEndTime().getMin());
        doc.put("endTime", end);

        // Insert the document into the collection
        collection.insertOne(doc);
        log.info("Task inserted successfully");
    }


    //-----------------------------------Update Task--------------------------------------
    @Override
    public String updateTask (String taskDescription, Integer taskId){
        MongoClient mongoClient = MongoClients.create(mongoURI);
        MongoDatabase database = mongoClient.getDatabase("myDatabase");
        MongoCollection<Document> taskToUpdate = database.getCollection("taskList");
        Document key = new Document("taskId", taskId);
        System.out.println(key);
        Document update = new Document("$set", new Document("taskDescription", taskDescription));
        if (key != null) {
            System.out.println(update);
            System.out.println(taskToUpdate.find(key));
            taskToUpdate.updateOne(key, update);
        }
        System.out.println("Document updated successfully.");
        return taskDescription;
    }

    //-------------------------------------Delete Task------------------------------------------
    @Override
    public void deleteTask ( int taskId){
        log.info("inside Delete");
        MongoClient mongoClient = MongoClients.create(mongoURI);
        MongoDatabase database = mongoClient.getDatabase("myDatabase");
        MongoCollection<Document> taskToDelete = database.getCollection("taskList");
        BasicDBObject query = new BasicDBObject();
        query.put("taskId", taskId);
        taskToDelete.deleteOne(query);
    }
}













