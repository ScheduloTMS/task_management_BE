package com.taskmanagement.task.Service;

import com.taskmanagement.task.DTO.TaskDTO;
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
    public TaskEntity createTask(TaskDTO taskDTO) {
        TaskEntity task = new TaskEntity();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setFile(taskDTO.getFile());
        return taskRepository.save(task);
    }

    public List<TaskEntity> getAllTasks() {
        return taskRepository.findByDeletedAtIsNull();
    }

    public TaskEntity getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Transactional
    public TaskEntity updateTask(UUID taskId, TaskDTO taskDTO) {
        TaskEntity task = getTaskById(taskId);
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        if (taskDTO.getFile() != null) {
            task.setFile(taskDTO.getFile());
        }
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(UUID taskId) {
        TaskEntity task = getTaskById(taskId);
        task.setDeletedAt(java.time.LocalDateTime.now());
        taskRepository.save(task);
    }
}