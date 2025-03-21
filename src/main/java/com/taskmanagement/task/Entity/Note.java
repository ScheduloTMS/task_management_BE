package com.taskmanagement.task.Entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID noteId;

    @ManyToOne
    @JoinColumn (nullable = false)
    private Users user;

    @Column(nullable = false)
    private String noteText;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;


    public Note() {}

    public Note(Users user, String noteText) {
        this.user = user;
        this.noteText = noteText;
    }


    public UUID getNoteId() { return noteId; }
    public void setNoteId(UUID noteId) { this.noteId = noteId; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    @JsonProperty("userId")
    public String getUserId() {
        return user != null ? user.getUserId() : null;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}