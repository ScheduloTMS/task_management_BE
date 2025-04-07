package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Entity.AssignmentId;
import com.taskmanagement.task.Entity.TaskEntity;
import com.taskmanagement.task.Repository.AssignmentRepository;
import com.taskmanagement.task.Repository.TaskRepository;
import com.taskmanagement.task.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveAssignment(UUID taskId, String userId, byte[] fileUploads, String submissionStatus, String score) {
        AssignmentEntity assignment = new AssignmentEntity(taskId, userId, fileUploads, submissionStatus, score);
        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        if (taskEntity.getDueDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new RuntimeException("Cannot submit assignment after the due date");
        }

        assignmentRepository.save(assignment);
    }

    @Transactional
    public void updateAssignment(UUID taskId, String userId, byte[] fileData, String submissionStatus, String score) {
        AssignmentId id = new AssignmentId(taskId, userId);
        AssignmentEntity assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));


        assignment.setSubmissionStatus(submissionStatus);
        assignment.setScore(score);
        assignment.setUpdatedAt(LocalDateTime.now());

        assignmentRepository.save(assignment);
    }

    @Transactional(readOnly = true)
    public Optional<AssignmentEntity> getAssignmentByUserAndTask(String userId, UUID taskId) {

        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));


        if (taskEntity.getCreatedBy().equals(userId)) {

            return Optional.of(new AssignmentEntity(taskId, userId, null, "Not Submitted", null));
        }


        AssignmentId assignmentId = new AssignmentId(taskId, userId);
        return assignmentRepository.findById(assignmentId);
    }

    public boolean isStudentAssignedToTask(UUID taskId, String userId) {
        return !assignmentRepository.existsById(new AssignmentId(taskId, userId));
    }


    @Transactional(readOnly = true)
    public boolean hasStudentSubmittedFile(UUID taskId, String userId) {
        AssignmentId id = new AssignmentId(taskId, userId);
        return assignmentRepository.findById(id)
                .map(assignment -> assignment.getFileUploads() != null)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public List<AssignmentEntity> getAssignmentsForStudent(String studentId) {
        return assignmentRepository.findAll().stream()
                .filter(a -> a.getId().getUserId().equals(studentId))
                .toList();
    }

    @Transactional(readOnly = true)
    public String getUserIdByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email))
                .getUserId();
    }




    @Transactional
    public void assignStudents(UUID taskId, List<String> studentIds, String mentorId) throws AccessDeniedException {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));


        if (!task.getCreatedBy().equals(mentorId)) {
            throw new AccessDeniedException("You are not authorized to assign students to this task");
        }

        for (String studentId : studentIds) {
            userRepository.findById(studentId)
                    .filter(user -> user.getDeletedAt() == null)
                    .orElseThrow(() -> new RuntimeException("User not found or has been deleted with ID: " + studentId));



            AssignmentId assignmentId = new AssignmentId(taskId, studentId);


            if (assignmentRepository.existsById(assignmentId)) {
                throw new RuntimeException("Student with ID " + studentId + " is already assigned to this task");
            }

            AssignmentEntity assignment = new AssignmentEntity();
            assignment.setId(assignmentId);
            assignment.setFileUploads(null);
            assignment.setSubmissionStatus("Not Submitted");
            assignment.setScore(null);
            assignment.setSubmittedAt(null);
            assignment.setUpdatedAt(LocalDateTime.now());

            assignmentRepository.save(assignment);
        }
    }


}