package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.TaskEntity;
import com.taskmanagement.task.Repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    public TaskEntity createTask(String title, String description, LocalDate dueDate, byte[] file) {
        TaskEntity task = new TaskEntity(title, description, dueDate, file);
        return taskRepository.save(task);
    }


    public List<TaskEntity> getAllTasks() {
        return taskRepository.findByDeletedAtIsNull();
    }


    public Optional<TaskEntity> getTaskById(UUID taskId) {
        return taskRepository.findById(taskId).filter(task -> task.getDeletedAt() == null);
    }


    @Transactional
    public TaskEntity updateTask(UUID taskId, String title, String description, LocalDate dueDate, byte[] file) {
        return taskRepository.findById(taskId).map(task -> {
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(dueDate);
            if (file != null) {
                task.setFile(file);
            }
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found"));
    }


    @Transactional
    public boolean deleteTask(UUID taskId) {
        return taskRepository.findById(taskId).map(task -> {
            task.setDeletedAt(LocalDateTime.now());
            taskRepository.save(task);
            return true;
        }).orElse(false);
    }
}

