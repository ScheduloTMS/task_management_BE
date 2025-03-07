package com.taskmanagement.task.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "remarks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public RemarkEntity(TaskEntity task, String userId, String comment) {
        this.task = task;
        this.userId = userId;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }
}
