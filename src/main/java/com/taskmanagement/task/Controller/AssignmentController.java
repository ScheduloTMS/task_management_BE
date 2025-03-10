package com.taskmanagement.task.Controller;

import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Service.AssignmentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
// import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/assignments")
public class AssignmentController 
{
    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createAssignment(@RequestParam UUID taskId, 
                                            @RequestParam String userId, 
                                            @RequestParam(required = false) MultipartFile file, 
                                            @RequestParam String submissionStatus, 
                                            @RequestParam String score
                                            ) {
        try {
            byte[] fileData = (file != null) ? file.getBytes() : null;
            AssignmentEntity savedAssignment = assignmentService.saveAssignment(taskId, userId, fileData, submissionStatus, score);
            return ResponseEntity.ok(savedAssignment);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    // ✅ GET: Retrieve an assignment by user ID and task ID
    @GetMapping("/{userId}/{taskId}")
    public ResponseEntity<AssignmentEntity> getAssignment(@PathVariable String userId, 
                                                      @PathVariable UUID taskId) 
    {
    Optional<AssignmentEntity> assignment = assignmentService.getAssignmentByUserAndTask(userId, taskId);
    return assignment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ PUT: Update an existing assignment
    @PutMapping(consumes = "multipart/form-data") 
    public ResponseEntity<?> updateAssignment(@RequestParam UUID taskId, 
                                                             @RequestParam String userId, 
                                                             @RequestParam(required = false) MultipartFile file, 
                                                             @RequestParam String submissionStatus,
                                                             @RequestParam String score
                                                             ) 
    {
        try {
            byte[] fileData = (file != null) ? file.getBytes() : null;
            Optional<AssignmentEntity> updatedAssignment = assignmentService.updateAssignment(taskId, userId, fileData, submissionStatus,score);
            return updatedAssignment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } 
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing file: " + e.getMessage());
        }
        
    }
}
