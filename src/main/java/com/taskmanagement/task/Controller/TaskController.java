package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.ApiResponse;
import com.taskmanagement.task.DTO.AssignmentResponse;
import com.taskmanagement.task.DTO.TaskWithStatusDTO;
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
import org.springframework.security.core.GrantedAuthority;
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
            String email = userDetails.getUsername();
            String role = userDetails.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .filter(auth -> auth.startsWith("ROLE_"))
                    .findFirst().orElse("ROLE_STUDENT");

            List<TaskWithStatusDTO> responseList;

            if (role.equals("ROLE_MENTOR")) {
                List<TaskEntity> tasks = taskService.getAllTasksForUser(email);

                responseList = tasks.stream().map(task -> {
                    List<AssignmentEntity> assignments = assignmentService.getAssignmentsByTaskId(task.getTaskId());

                    long total = assignments.size();
                    long reviewed = assignments.stream().filter(a -> a.getScore() != null).count();
                    long overdue = assignments.stream().filter(a ->
                            a.getFileUploads() == null &&
                                    task.getDueDate().isBefore(LocalDate.now())
                    ).count();

                    String status;
                    if (total == 0) {
                        status = "To Do";
                    } else if (overdue > 0) {
                        status = "Overdue";
                    } else if (reviewed == total) {
                        status = "Completed";
                    } else if (reviewed > 0) {
                        status = "In Progress";
                    } else {
                        status = "To Do";
                    }

                    return new TaskWithStatusDTO(task, status);
                }).collect(Collectors.toList());

            } else {
                List<AssignmentEntity> assignments = assignmentService.getAssignmentsForStudent(email);

                responseList = assignments.stream().map(assignment -> {
                    TaskEntity task = taskService.getTaskById(assignment.getId().getTaskId());

                    String status;
                    if (assignment.getFileUploads() == null && task.getDueDate().isBefore(LocalDate.now())) {
                        status = "Overdue";
                    } else if (assignment.getFileUploads() == null) {
                        status = "To Do";
                    } else if (assignment.getScore() != null) {
                        status = "Completed";
                    } else {
                        status = "In Progress";
                    }

                    return new TaskWithStatusDTO(task, status);
                }).collect(Collectors.toList());
            }

            return ResponseEntity.ok(new ApiResponse("success", 200, "Tasks retrieved successfully", responseList));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", 404, e.getMessage(), null));
        }
    }


    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse> getTaskById(@PathVariable UUID taskId,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(auth -> auth.startsWith("ROLE_"))
                    .findFirst()
                    .orElse("ROLE_STUDENT");

            // First check if task exists (regardless of permissions)
            TaskEntity task = taskService.getTaskById(taskId);

            if (role.equals("ROLE_MENTOR")) {
                // Verify mentor is the creator of the task
                if (!task.getCreatedBy().equals(email)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ApiResponse("error", 403,
                                    "You are not the creator of this task", null));
                }

                // Mentor-specific logic
                List<AssignmentEntity> assignments = assignmentService.getAssignmentsByTaskId(taskId);
                String status = calculateMentorTaskStatus(task, assignments);

                List<AssignmentResponse> studentResponses = assignments.stream()
                        .map(a -> new AssignmentResponse(
                                a.getId().getTaskId(),
                                a.getStudent() != null ? a.getStudent().getName() : a.getId().getUserId(),
                                a.getSubmissionStatus(),
                                a.getScore(),
                                a.getFileUploads() != null ? "Submitted" : "Not Submitted"
                        ))
                        .toList();

                TaskWithStatusDTO responseDTO = new TaskWithStatusDTO(task, status, studentResponses);
                return ResponseEntity.ok(new ApiResponse("success", 200,
                        "Task retrieved successfully", responseDTO));
            } else {
                // Student access logic
                if (!assignmentService.isStudentAssignedToTask(taskId, email)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ApiResponse("error", 403,
                                    "You are not assigned to this task", null));
                }

                AssignmentEntity assignment = assignmentService.getAssignmentByTaskAndStudent(taskId, email);
                if (assignment == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse("error", 404,
                                    "Assignment not found", null));
                }

                String status = calculateStudentTaskStatus(task, assignment);
                TaskWithStatusDTO responseDTO = new TaskWithStatusDTO(task, status);
                return ResponseEntity.ok(new ApiResponse("success", 200,
                        "Task retrieved successfully", responseDTO));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", 404, e.getMessage(), null));
        }
    }

    // Helper method for mentor status calculation
    private String calculateMentorTaskStatus(TaskEntity task, List<AssignmentEntity> assignments) {
        if (assignments.isEmpty()) {
            return "To Do";
        }

        long total = assignments.size();
        long reviewed = assignments.stream().filter(a -> a.getScore() != null).count();
        long overdue = assignments.stream().filter(a ->
                a.getFileUploads() == null && task.getDueDate().isBefore(LocalDate.now())
        ).count();

        if (overdue > 0) return "Overdue";
        if (reviewed == total) return "Completed";
        if (reviewed > 0) return "In Progress";
        return "To Do";
    }

    // Helper method for student status calculation
    private String calculateStudentTaskStatus(TaskEntity task, AssignmentEntity assignment) {
        if (assignment.getFileUploads() == null) {
            return task.getDueDate().isBefore(LocalDate.now()) ? "Overdue" : "To Do";
        }
        return assignment.getScore() != null ? "Completed" : "In Progress";
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