package com.SmartScheduler.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SmartScheduler.Entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    
}
