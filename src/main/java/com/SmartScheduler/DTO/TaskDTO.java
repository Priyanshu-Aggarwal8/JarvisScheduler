package com.SmartScheduler.DTO;

import java.time.LocalDateTime;

public class TaskDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime dueDate; // Use String to make JSON serialization simple
    private Boolean completed;

    // Constructor with arguments
    public TaskDTO(Long id, String name, String description, LocalDateTime dueDate, Boolean completed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    // Empty constructor
    public TaskDTO() {
        // No-argument constructor for creating empty TaskDTO objects
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
