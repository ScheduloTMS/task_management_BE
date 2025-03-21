package com.taskmanagement.task.Service;

import com.taskmanagement.task.DTO.NoteDTO;
import com.taskmanagement.task.Entity.Note;
import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Repository.NoteRepository;
import com.taskmanagement.task.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    public List<NoteDTO> getNotesByUser(String userId) {
        List<Note> notes = noteRepository.findByUser_UserIdAndDeletedAtIsNull(userId);
        return notes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public NoteDTO saveNoteForUser(String userId, NoteDTO noteDTO) {
        Users user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = new Note();
        note.setUser(user);
        note.setNoteText(noteDTO.getNoteText());
        note.setCreatedAt(LocalDateTime.now());

        Note savedNote = noteRepository.save(note);
        return convertToDTO(savedNote);
    }

    public NoteDTO updateUserNote(String userId, UUID noteId, NoteDTO noteDTO) {
        Optional<Note> existingNote = noteRepository.findById(noteId);
        if (existingNote.isPresent() && existingNote.get().getUser().getUserId().equals(userId)) {
            Note note = existingNote.get();
            note.setNoteText(noteDTO.getNoteText());
            note.setUpdatedAt(LocalDateTime.now());

            Note updatedNote = noteRepository.save(note);
            return convertToDTO(updatedNote);
        }
        throw new RuntimeException("Note not found or unauthorized");
    }

    public void softDeleteNote(UUID noteId) {
        Optional<Note> existingNote = noteRepository.findById(noteId);
        if (existingNote.isPresent()) {
            Note note = existingNote.get();
            note.setDeletedAt(LocalDateTime.now());
            noteRepository.save(note);
        } else {
            throw new RuntimeException("Note not found");
        }
    }

    private NoteDTO convertToDTO(Note note) {
        NoteDTO noteDTO = new NoteDTO();
        noteDTO.setNoteId(note.getNoteId());
        noteDTO.setUserId(note.getUser().getUserId());
        noteDTO.setNoteText(note.getNoteText());
        noteDTO.setCreatedAt(note.getCreatedAt());
        noteDTO.setUpdatedAt(note.getUpdatedAt());
        return noteDTO;
    }
}