package com.taskmanagement.task.DTO;

public class UpdatePasswordRequest {
    private String currentPassword;
    private String newPassword;


    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}