package com.taskmanagement.task.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity 
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID taskId;

    @NotNull(message = "Title cannot be null")
    private String title;

    @NotNull(message = "Description cannot be null")
    private String description;

    private LocalDate createdAt;

    @NotNull(message = "Due date cannot be null")
    private LocalDate dueDate;

    @Lob
    @Column(columnDefinition = "BYTEA")
    private byte[] file;

    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() 
    {
        this.createdAt = LocalDate.now();
    }

    public TaskEntity(String title, String description, LocalDate dueDate, byte[] file) 
    {
        this.title = title;
        this.description = description;
        this.createdAt = LocalDate.now();
        this.dueDate = dueDate;
        this.file = file;
        this.deletedAt = null;
    }
}