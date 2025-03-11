package com.taskmanagement.task.Service;

import com.taskmanagement.task.DTO.RemarkDTO;
import com.taskmanagement.task.Entity.RemarkEntity;
import com.taskmanagement.task.Entity.TaskEntity;
import com.taskmanagement.task.Entity.User;
import com.taskmanagement.task.Repository.RemarkRepository;
import com.taskmanagement.task.Repository.TaskRepository;
import com.taskmanagement.task.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RemarkService {

    private final RemarkRepository remarkRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public RemarkService(RemarkRepository remarkRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.remarkRepository = remarkRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }


    @Transactional(readOnly = true)
    public List<RemarkDTO> getAllRemarksForTask(UUID taskId) {
        List<RemarkEntity> remarks = remarkRepository.findRemarksWithUserDetailsByTaskId(taskId);

        return remarks.stream().map(remark -> new RemarkDTO(
                remark.getRemarkId(),
                remark.getTask().getTaskId(),
                remark.getUser().getUserId(),
                remark.getUser().getName(),
                convertPhotoToBase64(remark.getUser().getPhoto()),
                remark.getComment(),
                remark.getCreatedAt(),
                remark.getDeletedAt()
        )).collect(Collectors.toList());
    }


    @Transactional
    public RemarkDTO addRemark(UUID taskId, String userId, String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be null or empty");
        }
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findByIdWithPhoto(userId)  // ✅ Use findByIdWithPhoto() to fetch photo
                .orElseThrow(() -> new RuntimeException("User not found"));

        RemarkEntity remark = new RemarkEntity(task, user, comment);
        RemarkEntity savedRemark = remarkRepository.save(remark);

        return new RemarkDTO(
                savedRemark.getRemarkId(),
                savedRemark.getTask().getTaskId(),
                savedRemark.getUser().getUserId(),
                savedRemark.getUser().getName(),
                convertPhotoToBase64(savedRemark.getUser().getPhoto()),
                savedRemark.getComment(),
                savedRemark.getCreatedAt(),
                savedRemark.getDeletedAt()
        );
    }


    @Transactional
    public void deleteRemark(UUID remarkId, String userId) {
        RemarkEntity remark = remarkRepository.findByRemarkIdAndDeletedAtIsNull(remarkId)
                .orElseThrow(() -> new RuntimeException("Remark not found or already deleted"));

        if (!remark.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Permission denied: You can only delete your own remarks");
        }

        remark.softDelete();
        remarkRepository.save(remark);
    }


    private String convertPhotoToBase64(byte[] photo) {
        return (photo != null) ? Base64.getEncoder().encodeToString(photo) : null;
    }
}