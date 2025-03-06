package com.taskmanagement.task.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "remarks")
public class RemarkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID remarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    @Column(nullable = false)
    private String userId;

    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    // Constructors
    public RemarkEntity() {}

    public RemarkEntity(TaskEntity task, String userId, String comment) { // Updated constructor
        this.task = task;
        this.userId = userId;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public UUID getRemarkId() {
        return remarkId;
    }

    public TaskEntity getTask() {
        return task;
    }

    public String getUserId() {  // Updated getter
        return userId;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
}
