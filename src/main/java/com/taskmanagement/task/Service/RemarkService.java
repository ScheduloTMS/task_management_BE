package com.taskmanagement.task.Service;
import com.taskmanagement.task.DTO.RemarkDTO;
import com.taskmanagement.task.Entity.RemarkEntity;
import com.taskmanagement.task.Entity.TaskEntity;
import com.taskmanagement.task.Repository.RemarkRepository;
import com.taskmanagement.task.Repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RemarkService {

    private final RemarkRepository remarkRepository;
    private final TaskRepository taskRepository;

    public RemarkService(RemarkRepository remarkRepository, TaskRepository taskRepository) {
        this.remarkRepository = remarkRepository;
        this.taskRepository = taskRepository;
    }


    public List<RemarkDTO> getAllRemarksForTask(UUID taskId) {
        List<RemarkEntity> remarks = remarkRepository.findByTask_TaskIdAndDeletedAtIsNull(taskId);
        return remarks.stream()
                .map(remark -> new RemarkDTO(
                        remark.getRemarkId(),
                        remark.getTask().getTaskId(),
                        remark.getUserId(),
                        remark.getComment(),
                        remark.getCreatedAt(),
                        remark.getDeletedAt()))
                .collect(Collectors.toList());
    }


    public RemarkDTO addRemark(UUID taskId, String userId, String comment) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        RemarkEntity remark = new RemarkEntity(task, userId, comment);
        RemarkEntity savedRemark = remarkRepository.save(remark);

        return new RemarkDTO(
                savedRemark.getRemarkId(),
                savedRemark.getTask().getTaskId(),
                savedRemark.getUserId(),
                savedRemark.getComment(),
                savedRemark.getCreatedAt(),
                savedRemark.getDeletedAt());
    }


    public void deleteRemark(UUID remarkId, String userId) {
        RemarkEntity remark = remarkRepository.findByRemarkIdAndDeletedAtIsNull(remarkId)
                .orElseThrow(() -> new RuntimeException("Remark not found or already deleted"));

        if (!remark.getUserId().equals(userId)) {
            throw new RuntimeException("Permission denied: You can only delete your own remarks");
        }

        remark.softDelete();
        remarkRepository.save(remark);
    }
}
