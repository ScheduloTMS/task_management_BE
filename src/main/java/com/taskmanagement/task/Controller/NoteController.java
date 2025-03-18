package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.NoteDTO;
import com.taskmanagement.task.Service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public List<NoteDTO> getUserNotes(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // Extract userId from JWT
        return noteService.getNotesByUser(userId);
    }

    @PostMapping
    public ResponseEntity<NoteDTO> createNote(
            @RequestBody NoteDTO noteDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        return ResponseEntity.ok(noteService.saveNoteForUser(userId, noteDTO));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteDTO> updateNote(
            @PathVariable UUID noteId,
            @RequestBody NoteDTO noteDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // Extract userId from JWT
        return ResponseEntity.ok(noteService.updateUserNote(userId, noteId, noteDTO));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable UUID noteId) {
        noteService.softDeleteNote(noteId);
        return ResponseEntity.noContent().build();
    }
}