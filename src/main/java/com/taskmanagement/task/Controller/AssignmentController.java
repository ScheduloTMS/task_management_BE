package com.taskmanagement.task.Controller;

import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Service.AssignmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/assignments")
public class AssignmentController 
{
    @Autowired
    AssignmentService assignmentService;


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> createAssignment(@RequestParam UUID taskId, 
                                            @RequestParam String userId, 
                                            @RequestParam(required = false) MultipartFile file, 
                                            @RequestParam String submissionStatus, 
                                            @RequestParam String score
                                            ) 
    {
        try 
        {
            byte[] fileData = (file != null) ? file.getBytes() : null;
            assignmentService.saveAssignment(taskId, userId, fileData, submissionStatus, score);
            return ResponseEntity.status(HttpStatus.CREATED).body("Assignment created successfully");
        } 
        catch (IOException e) 
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/{taskId}")
    public ResponseEntity<AssignmentEntity> getAssignment(@PathVariable String userId,@PathVariable UUID taskId) 
    {
        Optional<AssignmentEntity> assignment = assignmentService.getAssignmentByUserAndTask(userId, taskId);
        return assignment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(consumes = "multipart/form-data") 
    public ResponseEntity<String> updateAssignment(@RequestParam UUID taskId, 
                        @RequestParam String userId, 
                        @RequestParam(required = false) MultipartFile file, 
                        @RequestParam String submissionStatus,
                        @RequestParam String score) 
    {
        try 
        {
            byte[] fileData = (file != null) ? file.getBytes() : null;
            assignmentService.updateAssignment(taskId, userId, fileData, submissionStatus, score);
            return ResponseEntity.ok("Assignment updated successfully");
        } 
            catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
            catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Error processing file: " + e.getMessage());
        }
    }

}
