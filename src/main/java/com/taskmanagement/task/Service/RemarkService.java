package com.taskmanagement.task.Service;

import com.taskmanagement.task.DTO.RemarkDTO;
import com.taskmanagement.task.Entity.AssignmentId;
import com.taskmanagement.task.Entity.RemarkEntity;
import com.taskmanagement.task.Entity.TaskEntity;
import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Repository.RemarkRepository;
import com.taskmanagement.task.Repository.TaskRepository;
import com.taskmanagement.task.Repository.AssignmentRepository;
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
    private TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<RemarkDTO> getRemarksForAssignment(UUID taskId, String userId) {

        if (!assignmentRepository.existsById(new AssignmentId(taskId, userId))) {
            throw new RuntimeException("Assignment not found");
        }

        // Fetch only remarks where deletedAt is NULL
        List<RemarkEntity> remarks = remarkRepository.findByAssignment_Id_TaskIdAndAssignment_Id_UserIdAndDeletedAtIsNull(taskId, userId);

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
    public RemarkDTO addRemark(UUID taskId, String userId, String comment) {

        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));


        boolean isStudentAssigned = assignmentRepository.existsById(new AssignmentId(taskId, userId));
        boolean isMentor = task.getCreatedBy().equals(userId);

        if (!isStudentAssigned && !isMentor) {
            throw new RuntimeException("Only assigned students or the task mentor can add remarks");
        }


        AssignmentEntity assignment = isStudentAssigned
                ? assignmentRepository.findById(new AssignmentId(taskId, userId)).orElseThrow()
                : null;

        RemarkEntity remark = new RemarkEntity(assignment, comment);
        RemarkEntity savedRemark = remarkRepository.save(remark);

        return new RemarkDTO(
                savedRemark.getRemarkId(),
                taskId,
                userId,
                savedRemark.getComment(),
                savedRemark.getCreatedAt(),
                savedRemark.getDeletedAt()
        );
    }


    @Transactional
    public void deleteRemark(UUID remarkId, String userId) {
        RemarkEntity remark = remarkRepository.findById(remarkId)
                .orElseThrow(() -> new RuntimeException("Remark not found"));


        if (!remark.getAssignment().getId().getUserId().equals(userId)) {
            throw new RuntimeException("Permission denied: You can only delete your own remarks");
        }

        remark.softDelete();
        remarkRepository.save(remark);
    }

}