package com.taskmanagement.task.DTO;

import java.util.UUID;

public class AssignmentResponse {
    private UUID taskId;
    private String userId;
    private String submissionStatus;
    private String score;
    private String fileStatus;


    public AssignmentResponse(UUID taskId, String userId, String submissionStatus, String score, String fileStatus) {
        this.taskId = taskId;
        this.userId = userId;
        this.submissionStatus = submissionStatus;
        this.score = score;
        this.fileStatus = fileStatus;

    }


    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(String submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

}