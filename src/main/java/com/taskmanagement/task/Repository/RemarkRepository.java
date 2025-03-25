package com.taskmanagement.task.Repository;

import com.taskmanagement.task.Entity.RemarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RemarkRepository extends JpaRepository<RemarkEntity, UUID> {


    @Query("SELECT r FROM RemarkEntity r " +
            "WHERE r.assignment.id.taskId = :taskId " +
            "AND r.assignment.id.userId = :userId " +
            "AND r.deletedAt IS NULL")
    List<RemarkEntity> findByAssignment_TaskIdAndAssignment_UserId(
            @Param("taskId") UUID taskId,
            @Param("userId") String userId
    );


    @Query("SELECT r FROM RemarkEntity r " +
            "WHERE r.taskId = :taskId " +
            "AND r.deletedAt IS NULL")

    List<RemarkEntity> findByTaskIdAndDeletedAtIsNull(@Param("taskId") UUID taskId);
}
