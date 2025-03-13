package com.taskmanagement.task.Controller;

import com.taskmanagement.task.Entity.TaskEntity;
import com.taskmanagement.task.Service.TaskService;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("api/tasks")

public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createTask(
            @RequestParam @NotNull(message = "Title cannot be null") String title,
            @RequestParam @NotNull(message = "Description cannot be null") String description,
            @RequestParam @NotNull(message = "Due date cannot be null") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
            @RequestParam(required = false) MultipartFile file) {

        try {
            byte[] fileData = (file != null) ? file.getBytes() : null;
            TaskEntity createdTask = taskService.createTask(title, description, due_date, fileData);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 201);
            response.put("message", "Task created successfully");
            response.put("body", createdTask);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", "Error processing file");
            errorResponse.put("body", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTasks() {
        List<TaskEntity> tasks = taskService.getAllTasks();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Successfully retrieved the list of tasks");
        response.put("body", tasks);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{task_id}")
    public ResponseEntity<Map<String, Object>> getTaskById(@PathVariable UUID task_id) {
        Optional<TaskEntity> task = taskService.getTaskById(task_id);
        if (task.isPresent()) {

            if (task.get().getDeletedAt() != null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 404);
                errorResponse.put("message", "Task has been deleted");
                errorResponse.put("body", null);

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Successfully retrieved task details");
            response.put("body", task.get());

            return ResponseEntity.ok(response);
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 404);
        errorResponse.put("message", "Task not found");
        errorResponse.put("body", null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }


    @PutMapping(value = "/{task_id}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> updateTask(
            @PathVariable UUID task_id,
            @RequestParam @NotNull(message = "Title cannot be null") String title,
            @RequestParam @NotNull(message = "Description cannot be null") String description,
            @RequestParam @NotNull(message = "Due date cannot be null") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due_date,
            @RequestParam(required = false) MultipartFile file) {

        Optional<TaskEntity> existingTask = taskService.getTaskById(task_id);

        if (existingTask.isPresent()) {

            if (existingTask.get().getDeletedAt() != null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 404);
                errorResponse.put("message", "Task has been deleted");
                errorResponse.put("body", null);

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            try {
                byte[] fileData = (file != null) ? file.getBytes() : null;
                TaskEntity updatedTask = taskService.updateTask(task_id, title, description, due_date, fileData);

                Map<String, Object> response = new HashMap<>();
                response.put("status", 200);
                response.put("message", "Task updated successfully");
                response.put("body", updatedTask);

                return ResponseEntity.ok(response);
            } catch (IOException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 400);
                errorResponse.put("message", "Error processing file");
                errorResponse.put("body", null);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 404);
        errorResponse.put("message", "Task not found");
        errorResponse.put("body", null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @DeleteMapping("/{task_id}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable UUID task_id) {
        boolean deleted = taskService.deleteTask(task_id);
        if (deleted) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Task deleted successfully");
            response.put("body", null);

            return ResponseEntity.ok(response);
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 404);
        errorResponse.put("message", "Task not found");
        errorResponse.put("body", null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}