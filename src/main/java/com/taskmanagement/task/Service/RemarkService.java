package com.taskmanagement.task.Service;

import com.taskmanagement.task.DTO.RemarkDTO;
import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Entity.AssignmentId;
import com.taskmanagement.task.Entity.RemarkEntity;
import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Repository.AssignmentRepository;
import com.taskmanagement.task.Repository.RemarkRepository;
import com.taskmanagement.task.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RemarkService {

    @Autowired
    private RemarkRepository remarkRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private UserRepository userRepository;

    // Resolve userId from email
    private String getUserIdByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .map(Users::getUserId)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public List<RemarkDTO> getRemarksForAssignment(UUID taskId, String email) {
        String userId = getUserIdByEmail(email);

        AssignmentEntity assignment = assignmentRepository.findById(new AssignmentId(taskId, userId))
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        List<RemarkEntity> remarks = remarkRepository.findByAssignment_TaskIdAndAssignment_UserId(taskId, userId);

        return remarks.stream().map(remark -> new RemarkDTO(
                remark.getRemarkId(),
                remark.getAssignment().getId().getTaskId(),
                remark.getAssignment().getId().getUserId(),
                remark.getComment(),
                remark.getCreatedAt(),
                remark.getDeletedAt()
        )).collect(Collectors.toList());
    }

    @Transactional
    public RemarkDTO addRemark(UUID taskId, String email, String comment) {
        String userId = getUserIdByEmail(email);

        AssignmentEntity assignment = assignmentRepository.findById(new AssignmentId(taskId, userId))
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        RemarkEntity remark = new RemarkEntity(assignment, comment);
        RemarkEntity savedRemark = remarkRepository.save(remark);

        return new RemarkDTO(
                savedRemark.getRemarkId(),
                savedRemark.getAssignment().getId().getTaskId(),
                savedRemark.getAssignment().getId().getUserId(),
                savedRemark.getComment(),
                savedRemark.getCreatedAt(),
                savedRemark.getDeletedAt()
        );
    }

    @Transactional
    public void deleteRemark(UUID remarkId, String email) {
        String userId = getUserIdByEmail(email);

        RemarkEntity remark = remarkRepository.findById(remarkId)
                .orElseThrow(() -> new RuntimeException("Remark not found"));

        if (!remark.getAssignment().getId().getUserId().equals(userId)) {
            throw new RuntimeException("Permission denied: You can only delete your own remarks");
        }

        remark.softDelete();
        remarkRepository.save(remark);
    }
}
