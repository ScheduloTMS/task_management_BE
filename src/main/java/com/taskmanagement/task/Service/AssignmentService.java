
package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Entity.AssignmentId;
import com.taskmanagement.task.Repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    // Get all assignments
    public List<AssignmentEntity> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public Optional<AssignmentEntity> getAssignmentById(String userId, UUID taskId) {
        AssignmentId id = new AssignmentId(userId, taskId);
        return assignmentRepository.findById(id);
    }
    
    public void deleteAssignment(String userId, UUID taskId) {
        AssignmentId id = new AssignmentId(userId, taskId);
        assignmentRepository.deleteById(id);
    }

    // Save (Create/Update) an assignment
    public AssignmentEntity saveAssignment(AssignmentEntity assignment) {
        return assignmentRepository.save(assignment);
    }


}
