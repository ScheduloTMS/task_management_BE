package com.taskmanagement.task.Entity;

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
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
    
    @Column(nullable = false)
    private String noteText;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
    
    public Note(User user, String noteText, LocalDateTime createdAt) 
    {
        this.noteId = UUID.randomUUID();
        this.user = user;
        this.noteText = noteText;
        this.createdAt = createdAt;
    }
}