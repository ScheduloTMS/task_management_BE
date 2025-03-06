package com.taskmanagement.task.Entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String userId;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Lob
    private byte[] photo;

    private String role;

    @Column(name = "deleted_at")
    private Date deletedAt;


    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Date getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Date deletedAt) { this.deletedAt = deletedAt; }
}
