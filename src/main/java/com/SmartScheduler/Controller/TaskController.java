package com.SmartScheduler.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SmartScheduler.DTO.TaskDTO;
import com.SmartScheduler.Service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Create a new task for a user with an optional reminder
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestParam String email, @RequestBody TaskDTO taskDTO) {
        try {
            if (taskDTO.getDueDate() != null) {
                // Create task with a reminder
                TaskDTO createdTask = taskService.createTaskWithReminder(email, taskDTO, taskDTO.getDueDate());
                return ResponseEntity.ok(createdTask);
            } else {
                // Create task without a reminder
                TaskDTO createdTask = taskService.createTask(email, taskDTO);
                return ResponseEntity.ok(createdTask);
            }
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get all tasks for a specific user
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getTasksByUser(@RequestParam String email) {
        try {
            List<TaskDTO> tasks = taskService.getTasksByUser(email)
                .stream()
                .map(task -> new TaskDTO(
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getDueDate(),
                    task.getCompleted()
                )) // Convert to TaskDTO
                .collect(Collectors.toList());
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Get a specific task by its ID for a specific user
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskByIdAndUser(@PathVariable Long id, @RequestParam String email) {
        try {
            TaskDTO task = taskService.getTaskByIdAndUser(id, email)
                .map(taskObj -> new TaskDTO(
                    taskObj.getId(),
                    taskObj.getName(),
                    taskObj.getDescription(),
                    taskObj.getDueDate(),
                    taskObj.getCompleted()
                )) // Convert to TaskDTO
                .orElseThrow(() -> new RuntimeException("Task not found or does not belong to the user"));
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Update an existing task for a specific user
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestParam String email, @RequestBody TaskDTO updatedTaskDTO) {
        try {
            TaskDTO task = taskService.updateTask(id, email, updatedTaskDTO);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Delete a task by ID for a specific user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @RequestParam String email) {
        try {
            taskService.deleteTask(id, email);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Create a task using natural language date parsing for a user
    @PostMapping("/natural-language")
    public ResponseEntity<TaskDTO> createTaskWithNaturalLanguage(
        @RequestParam String email,
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam String naturalLanguageDate) {
        try {
            TaskDTO task = taskService.createTaskWithNaturalLanguage(email, title, description, naturalLanguageDate);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
