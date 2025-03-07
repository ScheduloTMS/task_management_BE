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
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private TaskEntity task;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String uploads;

    @Enumerated(EnumType.STRING)
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
}
