package com.taskmanagement.task.Controller;

import com.taskmanagement.task.Service.NoteService;
import com.taskmanagement.task.Entity.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notes")
public class NoteController 
{

    @Autowired
    private NoteService noteService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Note>> getUserNotes(@PathVariable String userId) 
    {
        return ResponseEntity.ok(noteService.getNotesByUser(userId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Note> createNote(@PathVariable String userId, @RequestBody Note note) {
        return ResponseEntity.ok(noteService.saveNoteForUser(userId, note));
    }

    @PutMapping("/{userId}/{noteId}")
    public ResponseEntity<Note> updateNote(@PathVariable String userId, @PathVariable UUID noteId, @RequestBody Note note) {
        return ResponseEntity.ok(noteService.updateUserNote(userId, noteId, note));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable UUID noteId) 
    {
        noteService.softDeleteNote(noteId);
        return ResponseEntity.noContent().build();
    }
}
