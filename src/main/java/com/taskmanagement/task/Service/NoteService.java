package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.User;
import com.taskmanagement.task.Repository.NoteRepository;
import com.taskmanagement.task.Repository.UserRepository;
import com.taskmanagement.task.Entity.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NoteService 
{
    
    @Autowired
    private NoteRepository noteRepository;  

    @Autowired
    private UserRepository userRepository;

    public List<Note> getNotesByUser(String userId) 
    {
        return noteRepository.findByUser_UserIdAndDeletedAtIsNull(userId);
    }

    public Note saveNoteForUser(String userId, Note note) 
    {
        User user=userRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        note.setCreatedAt(LocalDateTime.now());
        note.setUser(user);

        return noteRepository.save(note);
    }

    public Note updateUserNote(String userId, UUID noteId, Note updatedNote) 
    {
        Optional<Note> existingNote = noteRepository.findById(noteId);
        if (existingNote.isPresent() && existingNote.get().getUser().getUserId().equals(userId)) {
            Note note = existingNote.get();
            note.setNoteText(updatedNote.getNoteText());
            note.setUpdatedAt(LocalDateTime.now());
            return noteRepository.save(note);
        }
        throw new RuntimeException("Note not found or unauthorized");
    }

    public void softDeleteNote(UUID noteId) {
        Optional<Note> existingNote = noteRepository.findById(noteId);
        if (existingNote.isPresent()) 
        {
            Note note = existingNote.get();
            note.setDeletedAt(LocalDateTime.now());
            noteRepository.save(note);
        } 
        else 
        {
            throw new RuntimeException("Note not found");
        }
    }
    
}