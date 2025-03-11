package com.taskmanagement.task.Service;

import com.taskmanagement.task.Repository.AssignmentRepository;
import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Service.AssignmentService;

import com.taskmanagement.task.Entity.AssignmentId;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssignmentService 
{
    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository) 
    {
        this.assignmentRepository = assignmentRepository;
    }

    // Post
    @Transactional
    public AssignmentEntity saveAssignment(UUID taskId, String userId, byte[] file, String submissionStatus, String score) 
    {
        AssignmentEntity assignment = new AssignmentEntity(taskId, userId, file, submissionStatus, score);
        return assignmentRepository.save(assignment);
    }

    // Get
    public Optional<AssignmentEntity> getAssignmentByUserAndTask(String userId, UUID taskId) 
    {
        AssignmentId id = new AssignmentId(taskId, userId);
        return assignmentRepository.findById(id);
    }

    // Edit
    @Transactional
    public Optional<AssignmentEntity> updateAssignment(UUID taskId, String userId, byte[] file,String submissionStatus,String score) 
    {
        AssignmentId id = new AssignmentId(taskId, userId);
        Optional<AssignmentEntity> existingAssignment = assignmentRepository.findById(id);
        if (existingAssignment.isPresent()) 
        {
            AssignmentEntity assignment = existingAssignment.get();
            assignment.setFileUploads(file);
            assignment.setSubmissionStatus(submissionStatus);
            assignment.setSubmittedAt(LocalDateTime.now());
            assignment.setScore(score);

            return Optional.of(assignmentRepository.save(assignment));
        }
        return Optional.empty();
    }
}
