
package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Entity.AssignmentId;
import com.taskmanagement.task.Repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    // Get all assignments
    public List<AssignmentEntity> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    // Get assignment by composite key (userId and taskId)
    public Optional<AssignmentEntity> getAssignmentById(Long userId, Long taskId) {
        AssignmentId id = new AssignmentId(userId, taskId);
        return assignmentRepository.findById(id);
    }

    // Save (Create/Update) an assignment
    public AssignmentEntity saveAssignment(AssignmentEntity assignment) {
        return assignmentRepository.save(assignment);
    }

    // Delete an assignment by composite key
    public void deleteAssignment(Long userId, Long taskId) {
        AssignmentId id = new AssignmentId(userId, taskId);
        assignmentRepository.deleteById(id);
    }
}
