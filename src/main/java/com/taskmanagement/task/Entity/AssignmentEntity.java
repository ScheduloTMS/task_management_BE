package com.taskmanagement.task.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "assignments")
public class AssignmentEntity {

    @EmbeddedId
    private AssignmentId id;

    @Lob
    private byte[] fileUploads;
    private String submissionStatus;
    private LocalDateTime submittedAt;
    private String score;
    private String feedback;

    public AssignmentEntity(UUID taskId, String userId, byte[] fileUploads, String submissionStatus, String score, String feedback) {
        this.id = new AssignmentId(taskId, userId);
        this.fileUploads = fileUploads;
        this.submissionStatus = submissionStatus;
        this.submittedAt = LocalDateTime.now();
        this.score = score;
        this.feedback = feedback;
    }
}
