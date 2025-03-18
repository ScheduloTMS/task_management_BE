package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.StudentAssignmentRequest;
import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @PostMapping("/{taskId}/assign")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<?> assignStudents(
            @PathVariable UUID taskId,
            @RequestBody StudentAssignmentRequest request) {

        assignmentService.assignStudents(taskId, request.getStudentIds());
        return ResponseEntity.ok("Students assigned to task successfully");
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> createAssignment(
            @RequestParam UUID taskId,
            @RequestParam(required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = userDetails.getUsername();


            if (!assignmentService.isStudentAssignedToTask(taskId, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not assigned to this task");
            }

            byte[] fileData = (file != null) ? file.getBytes() : null;
            String submissionStatus = "Submitted, Marked for review";
            String score = null;

            assignmentService.saveAssignment(taskId, userId, fileData, submissionStatus, score);
            return ResponseEntity.status(HttpStatus.CREATED).body("Assignment created successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<String> updateAssignment(
            @RequestParam UUID taskId,
            @RequestParam String userId,
            @RequestParam String score) {
        try {

            if (!assignmentService.hasStudentSubmittedFile(taskId, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Student has not submitted a file for this task");
            }

            String submissionStatus = "Reviewed";

            assignmentService.updateAssignment(taskId, userId, null, submissionStatus, score);
            return ResponseEntity.ok("Assignment updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<AssignmentEntity> getAssignment(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();


        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"))) {
            Optional<AssignmentEntity> assignment = assignmentService.getAssignmentByUserAndTask(userId, taskId);
            return assignment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }


        Optional<AssignmentEntity> assignment = assignmentService.getAssignmentByUserAndTask(userId, taskId);
        return assignment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}