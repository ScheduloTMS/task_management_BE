package com.taskmanagement.task.Service;
import com.taskmanagement.task.Repository.NoteRepository;
import com.taskmanagement.task.DTO.NoteDTO;
import com.taskmanagement.task.Entity.Note;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
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

    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }
}
