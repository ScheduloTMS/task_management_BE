package com.taskmanagement.task.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID taskId;

    private String title;
    private String description;
    private LocalDate createdAt;
    private LocalDate dueDate;

    @Lob
    @Column(columnDefinition = "BYTEA")
    private byte[] file;

    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

    public TaskEntity(String title, String description, LocalDate dueDate, byte[] file) {
        this.title = title;
        this.description = description;
        this.createdAt = LocalDate.now();
        this.dueDate = dueDate;
        this.file = file;
        this.deletedAt = null;
    }
}
