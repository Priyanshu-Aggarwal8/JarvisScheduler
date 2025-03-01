package com.SmartScheduler.DTO;

import java.util.List;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private List<Long> taskIds; // Only include task IDs, not the entire task objects

    // Constructor
    public UserDTO(Long id, String name, String email, List<Long> taskIds) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.taskIds = taskIds;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }
}
