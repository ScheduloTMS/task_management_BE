package com.taskmanagement.task.DTO;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public class RemarkDTO {
    private final UUID remarkId;
    private final UUID taskId;
    private final String userId;
    private final String name;
    private final String photo;  // ✅ Base64 Encoded Image

    @NotBlank(message = "Comment cannot be empty")
    private final String comment;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    public RemarkDTO(UUID remarkId, UUID taskId, String userId, String name, String photo, String comment, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.remarkId = remarkId;
        this.taskId = taskId;
        this.userId = userId;
        this.name = name;
        this.photo = photo;
        this.comment = comment;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public UUID getRemarkId() {
        return remarkId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
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
