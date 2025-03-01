package com.SmartScheduler.Scheduler;

import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SmartScheduler.Entity.Task;
import com.SmartScheduler.Repository.TaskRepository;
import com.SmartScheduler.Service.TaskMessagePublisher;

@Component
public class TaskReminderJob implements Job {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskEmailConsumer taskEmailConsumer;
    
    @Autowired
    private TaskMessagePublisher taskMessagePublisher;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // Get taskId from JobDataMap using the correct key
            Long taskId = context.getJobDetail().getJobDataMap().getLong("taskId");
            
            System.out.println("Executing reminder job for task ID: " + taskId);

            // Fetch the task
            Task task = taskRepository.findById(taskId).orElseThrow(() -> 
                new JobExecutionException("Task not found with ID: " + taskId));

            String email = task.getUser().getEmail();
            String subject = "Reminder: Task " + task.getName();
            String body = "This is a reminder for your task: \n\n" +
                          "Task Name: " + task.getName() + "\n" +
                          "Task Description: " + task.getDescription() + "\n\n" +
                          "Please complete it as soon as possible.";

            // Prepare email payload
            Map<String, String> payload = new HashMap<>();
            payload.put("email", email);
            payload.put("taskName", task.getName());
            payload.put("taskDescription", body);

            // Send message to RabbitMQ queue instead of direct processing
            taskMessagePublisher.sendTaskReminder(payload);
            
            System.out.println("Queued reminder for task: " + task.getName() + " to email: " + email);
        } catch (Exception e) {
            System.err.println("Error in TaskReminderJob: " + e.getMessage());
            e.printStackTrace();
            throw new JobExecutionException("Error executing task reminder job: " + e.getMessage(), e);
        }
    }
}