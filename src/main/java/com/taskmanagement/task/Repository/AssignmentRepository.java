package com.taskmanagement.task.Repository;

import com.taskmanagement.task.Entity.AssignmentEntity;
import com.taskmanagement.task.Entity.AssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentEntity, AssignmentId> {
}
