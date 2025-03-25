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
    @JoinColumns({
            @JoinColumn(name = "taskId", referencedColumnName = "taskId", insertable = false, updatable = false),
            @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    })
    private AssignmentEntity assignment;
    @Column(nullable = false)
    private UUID taskId;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private String authorId;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public RemarkEntity() {}


    public RemarkEntity(AssignmentEntity assignment, UUID taskId, String comment, String authorId) {
        this.assignment = assignment;
        this.taskId = taskId;
        this.comment = comment;
        this.authorId = authorId;
        this.createdAt = LocalDateTime.now();
    }


    public RemarkEntity(UUID taskId, String comment, String authorId) {
        this.assignment = null;
        this.taskId = taskId;
        this.comment = comment;
        this.authorId = authorId;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getRemarkId() { return remarkId; }
    public void setRemarkId(UUID remarkId) { this.remarkId = remarkId; }

    public AssignmentEntity getAssignment() { return assignment; }
    public void setAssignment(AssignmentEntity assignment) { this.assignment = assignment; }

    public UUID getTaskId() { return taskId; }
    public void setTaskId(UUID taskId) { this.taskId = taskId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
