package com.taskmanagement.task.DTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class StudentAssignmentRequest 
{
    private List<String> studentIds;
}
