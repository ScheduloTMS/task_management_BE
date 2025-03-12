package com.taskmanagement.task.Entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data

public class AssignmentId implements Serializable 
{
    private UUID taskId;
    private String userId;

    public AssignmentId(UUID taskId, String userId) 
    {
        this.taskId = taskId;
        this.userId = userId;
    }
}
