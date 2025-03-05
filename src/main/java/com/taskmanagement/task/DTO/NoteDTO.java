package com.taskmanagement.task.DTO;

import java.time.LocalDateTime;
import com.taskmanagement.task.Entity.Note;

public class NoteDTO {
    
    private Long noteId;
    private String noteText;
    private LocalDateTime createdAt;

    // Constructor to convert Entity to DTO
    public NoteDTO(Note note) {
        this.noteId = note.getNoteId();
        this.noteText = note.getNoteText();
        this.createdAt = note.getCreatedAt();
    }

    // Getters and Setters
    public Long getNoteId() { return noteId; }
    public void setNoteId(Long noteId) { this.noteId = noteId; }

    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
