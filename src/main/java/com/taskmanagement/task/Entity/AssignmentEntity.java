package com.taskmanagement.task.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
public class AssignmentEntity {

    @EmbeddedId
    private AssignmentId id;

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "Varun_id",referencedColumnName = "taskId")
    private TaskEntity task;

    @ManyToOne
    @JoinColumn(name = "Anna_id", referencedColumnName = "user_id")
    private User user;
    
    private String uploads;

    @Enumerated(EnumType.STRING) 
    private Status status;

    private Double scores;

    private LocalDateTime submittedOn;
    private LocalDateTime submittedAt;

    // Constructors
    public AssignmentEntity() {}

    public AssignmentEntity(AssignmentId id, TaskEntity task, User user) {
        this.id = id;
        this.task = task;
        this.user = user;
    }

    // Getters and Setters
    public AssignmentId getId() {
        return id;
    }

    public void setId(AssignmentId id) {
        this.id = id;
    }

    public TaskEntity getTask() {
        return task;
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUploads() {
        return uploads;
    }

    public void setUploads(String uploads) {
        this.uploads = uploads;
    }

    public Status getStatus() {  // Fixed: Using Status instead of String
        return status;
    }

    public void setStatus(Status status) {  // Fixed: Using Status instead of String
        this.status = status;
    }

    public Double getScores() {
        return scores;
    }

    public void setScores(Double scores) {
        this.scores = scores;
    }

    public LocalDateTime getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(LocalDateTime submittedOn) {
        this.submittedOn = submittedOn;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    // Correct enum usage
    public enum Status {
        SUBMITTED,
        NOT_SUBMITTED
    }
}
