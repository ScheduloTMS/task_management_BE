package com.taskmanagement.task.Repository;

import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Entity.AssignmentId;
import com.taskmanagement.task.Entity.Users;
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

    List<AssignmentEntity> findByIdTaskId(UUID taskId);

    Optional<AssignmentEntity> findByIdTaskIdAndIdUserId(UUID taskId, String userId);


    List<AssignmentEntity> findById_UserIdAndDeletedAtIsNull(String userId);

    @Query("SELECT u FROM Users u JOIN AssignmentEntity a ON u.id = a.id.userId WHERE a.id.taskId = :taskId")
    List<Users> findStudentsByTaskId(@Param("taskId") UUID taskId);

    @Query("SELECT COUNT(a) > 0 FROM AssignmentEntity a WHERE a.id.taskId = :taskId AND a.id.userId = :userId AND a.fileUploads IS NOT NULL")
    boolean hasSubmitted(@Param("taskId") UUID taskId, @Param("userId") String userId);
}