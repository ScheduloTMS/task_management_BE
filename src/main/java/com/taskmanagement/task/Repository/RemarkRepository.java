package com.taskmanagement.task.Repository;

import com.taskmanagement.task.Entity.RemarkEntity;
import com.taskmanagement.task.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RemarkRepository extends JpaRepository<RemarkEntity, UUID> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.photo WHERE u.userId = :userId")
    Optional<User> findByIdWithPhoto(@Param("userId") String userId);

    List<RemarkEntity> findByTask_TaskIdAndDeletedAtIsNull(UUID taskId);

    Optional<RemarkEntity> findByRemarkIdAndDeletedAtIsNull(UUID remarkId);


    @Query("SELECT r FROM RemarkEntity r JOIN FETCH r.user WHERE r.task.taskId = :taskId AND r.deletedAt IS NULL")
    List<RemarkEntity> findRemarksWithUserDetailsByTaskId(UUID taskId);
}
