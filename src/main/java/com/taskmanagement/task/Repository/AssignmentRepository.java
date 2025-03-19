package com.taskmanagement.task.Repository;

import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Entity.AssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentEntity, AssignmentId> {
    Optional<AssignmentEntity> findById(AssignmentId id);


    @Query("SELECT a FROM AssignmentEntity a WHERE a.id.taskId = :taskId AND a.deletedAt IS NULL")
    List<AssignmentEntity> findAllById_TaskId(@Param("taskId") UUID taskId);
}