package com.taskmanagement.task.DTO;

import java.time.LocalDateTime;
import java.util.UUID;
import com.taskmanagement.task.Entity.Note;

public class NoteDTO {
    
    private UUID noteId;  // Change Long to UUID
    private String noteText;
    private LocalDateTime createdAt;

    // Constructor to convert Entity to DTO
    public NoteDTO(Note note) {
        this.noteId = note.getNoteId();
        this.noteText = note.getNoteText();
        this.createdAt = note.getCreatedAt();
    }

    // Getters and Setters
    public UUID getNoteId() { return noteId; }  // Change Long to UUID
    public void setNoteId(UUID noteId) { this.noteId = noteId; }  // Change Long to UUID

    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
