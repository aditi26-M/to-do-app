package com.example.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "taskList")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDto {
    private int taskId;
    private String taskDescription;
    private String email;
    private Time startTime;
    private Time endTime;
    @Id
    private String taskUUID;

    @Override
    public String toString() {
        return "TaskDto{" +
                "taskId=" + taskId +
                ", taskDescription='" + taskDescription  +
                ", email='" + email  +
                ", taskUUID='" + taskUUID +
                ", startTime='" + startTime +
                ", endTime='" + endTime +
                '}';
    }
}




