package com.taskmanagement.task.Entity;


import jakarta.persistence.*;


import java.time.LocalDateTime;


@Entity
public class Users {
    @Id
    private String userId;

    private String name;
    private String password;
    private String role;
    private String email;

    @Lob
    private byte[] photo;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

}