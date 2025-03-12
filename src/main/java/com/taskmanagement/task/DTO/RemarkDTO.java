package com.taskmanagement.task.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class RemarkDTO {
    private final UUID remarkId;
    private final UUID taskId;
    private final String userId;
    private final String name;
    private final String photo;

    @NotBlank(message = "Comment cannot be empty")
    private final String comment;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;
}