package com.taskmanagement.task.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;

    @NotNull
    @Column(nullable = false)
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

    public RemarkEntity(TaskEntity task, User user, String comment) {
        this.task = task;
        this.user = user;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }
}
