package com.taskmanagement.task.Controller;
import com.taskmanagement.task.Service.NoteService;
import com.taskmanagement.task.DTO.NoteDTO;
import com.taskmanagement.task.Entity.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/notes")
public class NoteController 
{

    @Autowired
    private NoteService noteService;

    @GetMapping
    public ResponseEntity<List<NoteDTO>> getNotes() {
        return ResponseEntity.ok(noteService.getAllNotes());
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        return ResponseEntity.ok(noteService.saveNote(note));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}
