package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.ApiResponse;
import com.taskmanagement.task.DTO.TaskDTO;
import com.taskmanagement.task.Entity.TaskEntity;
import com.taskmanagement.task.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse> createTask(@RequestBody TaskDTO taskDTO) {
        try {
            TaskEntity createdTask = taskService.createTask(taskDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(201, "Task created successfully", createdTask));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllTasks() {
        try {
            return ResponseEntity.ok(new ApiResponse(200, "Tasks retrieved successfully", taskService.getAllTasks()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, e.getMessage(), null));
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse> getTaskById(@PathVariable UUID taskId) {
        try {
            TaskEntity task = taskService.getTaskById(taskId);
            return ResponseEntity.ok(new ApiResponse(200, "Task retrieved successfully", task));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, e.getMessage(), null));
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse> updateTask(@PathVariable UUID taskId, @RequestBody TaskDTO taskDTO) {
        try {
            TaskEntity updatedTask = taskService.updateTask(taskId, taskDTO);
            return ResponseEntity.ok(new ApiResponse(200, "Task updated successfully", updatedTask));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable UUID taskId) {
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.ok(new ApiResponse(200, "Task deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, e.getMessage(), null));
        }
    }
}