package com.SmartScheduler.Service;

import java.time.LocalDateTime;
import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SmartScheduler.Entity.Task;
import com.SmartScheduler.Repository.TaskRepository;
import com.SmartScheduler.Scheduler.TaskReminderJob;

@Service
public class TaskSchedulerService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskRepository taskRepository;

    public void scheduleTaskReminder(Task task, LocalDateTime reminderTime) throws SchedulerException {
        
        Date reminderDate = Date.from(reminderTime.atZone(java.time.ZoneId.systemDefault()).toInstant());

        // Create a JobDetail for the TaskReminderJob
        JobDetail jobDetail = JobBuilder.newJob(TaskReminderJob.class)
                .withIdentity("reminderJob_" + task.getId()) 
                .usingJobData("taskId", task.getId())  
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger_" + task.getId()) 
                .startAt(reminderDate)  
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

        System.out.println("Scheduled reminder for task: " + task.getName() + " at " + reminderTime);
    }

    public void cancelTaskReminder(Task task) throws SchedulerException {
        JobKey jobKey = new JobKey("reminderJob_" + task.getId());

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            System.out.println("Cancelled reminder for task: " + task.getName());
        }
    }
}
