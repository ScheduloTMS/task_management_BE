package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.ApiResponse;
import com.taskmanagement.task.Entity.TaskEntity;
import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Service.AssignmentService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private AssignmentService assignmentService;

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ApiResponse> createTask(
            @RequestParam @NotNull(message = "Title cannot be null") String title,
            @RequestParam @NotNull(message = "Description cannot be null") String description,
            @RequestParam @NotNull(message = "Due date cannot be null") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            LocalDate createdAt = LocalDate.now();


            if (!dueDate.isAfter(createdAt)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("error", 400, "Due date must be after the created date", null));
            }


            if (taskService.isDuplicateTask(title, description)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse("error", 409, "Task with the same title and description already exists.", null));
            }

            byte[] fileData = (file != null) ? file.getBytes() : null;
            TaskEntity createdTask = taskService.createTask(title, description, dueDate, fileData, userDetails.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("success", 201, "Task created successfully", createdTask));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("error", 400, "Failed to create task: " + e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", 500, e.getMessage(), null));
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getAllTasks(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<TaskEntity> tasks;

            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"))) {
                tasks = taskService.getAllTasksForUser(userDetails.getUsername());
            } else {
                List<AssignmentEntity> assignments = assignmentService.getAssignmentsForStudent(userDetails.getUsername());
                tasks = assignments.stream()
                        .map(a -> taskService.getTaskById(a.getId().getTaskId()))
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(new ApiResponse("success", 200, "Tasks retrieved successfully", tasks));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", 404, e.getMessage(), null));
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse> getTaskById(@PathVariable UUID taskId,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        try {
            TaskEntity task;

            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"))) {
                task = taskService.getTaskByIdForUser(taskId, userDetails.getUsername());
            } else {
                if (!assignmentService.isStudentAssignedToTask(taskId, userDetails.getUsername())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ApiResponse("error", 403, "You are not assigned to this task", null));
                }
                task = taskService.getTaskById(taskId);
            }

            return ResponseEntity.ok(new ApiResponse("success", 200, "Task retrieved successfully", task));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", 404, e.getMessage(), null));
        }
    }


    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('MENTOR')")
    @Transactional
    public ResponseEntity<ApiResponse> updateTask(
            @PathVariable UUID taskId,
            @RequestParam @NotNull String title,
            @RequestParam @NotNull String description,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {

            TaskEntity existingTask = taskService.getTaskById(taskId);


            if (existingTask.getDeletedAt() != null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("error", 404, "Task already deleted", null));
            }


            byte[] fileData = (file != null) ? file.getBytes() : null;


            TaskEntity updatedTask = taskService.updateTask(taskId, title, description, dueDate, fileData, userDetails.getUsername());

            return ResponseEntity.ok(new ApiResponse("success", 200, "Task updated successfully", updatedTask));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("error", 400, "Failed to update task: " + e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", 404, e.getMessage(), null));
        }
    }


    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ApiResponse> deleteTask(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            taskService.deleteTask(taskId, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse("success", 200, "Task deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", 404, e.getMessage(), null));
        }
    }
}