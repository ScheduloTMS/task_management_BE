package com.taskmanagement.task.Repository;

import com.taskmanagement.task.Entity.RemarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RemarkRepository extends JpaRepository<RemarkEntity, UUID> {

    List<RemarkEntity> findByTask_TaskIdAndDeletedAtIsNull(UUID taskId);

    Optional<RemarkEntity> findByRemarkIdAndDeletedAtIsNull(UUID remarkId);
}
