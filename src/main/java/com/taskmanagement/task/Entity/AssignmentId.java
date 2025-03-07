package com.taskmanagement.task.Entity;


import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AssignmentId implements Serializable {
    private Long userId;
    private Long taskId;

    public AssignmentId() {}

    public AssignmentId(Long userId, Long taskId) {
        this.userId = userId;
        this.taskId = taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentId that = (AssignmentId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, taskId);
    }
}
