package com.taskmanagement.task.Entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
public class AssignmentId implements Serializable {
    private UUID taskId;
    private String userId;

    public AssignmentId(UUID taskId, String userId) 
    {
        this.taskId = taskId;
        this.userId = userId;
    }
}
