package com.taskmanagement.task.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
public class Note 
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID noteId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    @JsonIgnore 
    private User user;

    @Column(nullable = false)
    private String noteText;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public Note(User user, String noteText) 
    {
        this.user = user;
        this.noteText = noteText;
    }

    @JsonProperty("userId")
    public String getUserId() 
    {
        return user != null ? user.getUserId() : null;
    }

    @PrePersist
    protected void onCreate() 
    {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() 
    {
        this.updatedAt = LocalDateTime.now();
    }
}

