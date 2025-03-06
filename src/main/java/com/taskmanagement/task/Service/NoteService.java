package com.taskmanagement.task.Service;

import com.taskmanagement.task.Repository.NoteRepository;
import com.taskmanagement.task.DTO.NoteDTO;
import com.taskmanagement.task.Entity.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoteService {
    
    @Autowired
    private NoteRepository noteRepository;

    public List<NoteDTO> getAllNotes() {
        return noteRepository.findAll().stream()
                .map(NoteDTO::new)
                .collect(Collectors.toList());
    }

    public Note saveNote(Note note) {
        note.setCreatedAt(LocalDateTime.now());
        return noteRepository.save(note);
    }

    public void deleteNote(UUID id) {  // Change Long to UUID
        noteRepository.deleteById(id);
    }
}
