package com.taskmanagement.task.Controller;

import com.taskmanagement.task.Entity.AssignmentEntity;
//import com.taskmanagement.task.Entity.AssignmentId;
import com.taskmanagement.task.Service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    // Get all assignments
    @GetMapping
    public ResponseEntity<List<AssignmentEntity>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    // Get assignment by composite key (userId and taskId)
    @GetMapping("/{userId}/{taskId}")
    public ResponseEntity<AssignmentEntity> getAssignmentById(@PathVariable Long userId, @PathVariable Long taskId) {
        Optional<AssignmentEntity> assignment = assignmentService.getAssignmentById(userId, taskId);
        return assignment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create or update an assignment
    @PostMapping
    public ResponseEntity<AssignmentEntity> createOrUpdateAssignment(@RequestBody AssignmentEntity assignment) {
        return ResponseEntity.ok(assignmentService.saveAssignment(assignment));
    }

    // Delete an assignment by composite key
    @DeleteMapping("/{userId}/{taskId}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long userId, @PathVariable Long taskId) {
        assignmentService.deleteAssignment(userId, taskId);
        return ResponseEntity.noContent().build();
    }
}
