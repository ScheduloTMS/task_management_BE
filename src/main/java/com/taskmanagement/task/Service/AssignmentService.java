package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Entity.AssignmentId;

import com.taskmanagement.task.Repository.AssignmentRepository;
import com.taskmanagement.task.Repository.TaskRepository;
import com.taskmanagement.task.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository, TaskRepository taskRepository, UserRepository userRepository) 
    {
        this.assignmentRepository = assignmentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveAssignment(UUID taskId, String userId, byte[] file, String submissionStatus, String score) {
        AssignmentEntity assignment = new AssignmentEntity(taskId, userId, file, submissionStatus, score);
        assignmentRepository.save(assignment);
    }

    @Transactional
    public void assignStudents(UUID taskId, List<String> studentIds) 
    {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        for (String studentId : studentIds) 
        {
            userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + studentId));

            AssignmentId assignmentId = new AssignmentId(taskId, studentId);
            if (!assignmentRepository.existsById(assignmentId)) 
            {
                AssignmentEntity assignment = new AssignmentEntity();
                assignment.setId(assignmentId);
                assignment.setFileUploads(null); // No file initially
                assignment.setSubmissionStatus("Not Submitted");
                assignment.setScore(null);
                assignment.setSubmittedAt(null);
                assignment.setUpdatedAt(LocalDateTime.now());

                assignmentRepository.save(assignment);
            }
        }
    }

    public Optional<AssignmentEntity> getAssignmentByUserAndTask(String userId, UUID taskId) {
        AssignmentId id = new AssignmentId(taskId, userId);
        return assignmentRepository.findById(id);
    }

    @Transactional
    public AssignmentEntity updateAssignment(UUID taskId, String userId, byte[] file, String submissionStatus, String score) {
        AssignmentId id = new AssignmentId(taskId, userId);
        AssignmentEntity assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setFileUploads(file);
        assignment.setSubmissionStatus(submissionStatus);
        assignment.setSubmittedAt(LocalDateTime.now());
        assignment.setScore(score);
        assignment.setUpdatedAt(LocalDateTime.now());

        return assignmentRepository.save(assignment);
    }
}
