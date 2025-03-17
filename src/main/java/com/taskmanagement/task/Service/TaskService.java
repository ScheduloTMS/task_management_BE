package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.TaskEntity;
import com.taskmanagement.task.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    public TaskEntity createTask(String title, String description, LocalDate dueDate, byte[] fileData, String createdBy) {
        TaskEntity task = new TaskEntity();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setFile(fileData);
        task.setCreatedBy(createdBy);
        return taskRepository.save(task);
    }

    public List<TaskEntity> getAllTasksForUser(String username) {
        return taskRepository.findByCreatedByAndDeletedAtIsNull(username);
    }

    public TaskEntity getTaskByIdForUser(UUID taskId, String username) {
        return taskRepository.findByTaskIdAndCreatedBy(taskId, username)
                .orElseThrow(() -> new RuntimeException("Task not found or unauthorized"));
    }

    public TaskEntity getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Transactional
    public TaskEntity updateTask(UUID taskId, String title, String description, LocalDate dueDate, byte[] fileData, String username) {
        TaskEntity task = getTaskByIdForUser(taskId, username);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        if (fileData != null) {
            task.setFile(fileData);
        }
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(UUID taskId, String username) {
        TaskEntity task = getTaskByIdForUser(taskId, username);
        task.setDeletedAt(java.time.LocalDateTime.now());
        taskRepository.save(task);
    }
}