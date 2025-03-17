package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.ApiResponse;
import com.taskmanagement.task.Entity.TaskEntity;
import com.taskmanagement.task.Service.TaskService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ApiResponse> createTask(
            @RequestParam @NotNull(message = "Title cannot be null") String title,
            @RequestParam @NotNull(message = "Description cannot be null") String description,
            @RequestParam @NotNull(message = "Due date cannot be null") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            byte[] fileData = (file != null) ? file.getBytes() : null;
            TaskEntity createdTask = taskService.createTask(title, description, dueDate, fileData, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(201, "Task created successfully", createdTask));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, "Error processing file", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getAllTasks(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<TaskEntity> tasks = taskService.getAllTasksForUser(userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse(200, "Tasks retrieved successfully", tasks));
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
    @Transactional
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ApiResponse> updateTask(
            @PathVariable UUID taskId,
            @RequestParam @NotNull(message = "Title cannot be null") String title,
            @RequestParam @NotNull(message = "Description cannot be null") String description,
            @RequestParam @NotNull(message = "Due date cannot be null") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            byte[] fileData = (file != null) ? file.getBytes() : null;
            TaskEntity updatedTask = taskService.updateTask(taskId, title, description, dueDate, fileData, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse(200, "Task updated successfully", updatedTask));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, "Error processing file", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable UUID taskId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            taskService.deleteTask(taskId, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse(200, "Task deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, e.getMessage(), null));
        }
    }
}