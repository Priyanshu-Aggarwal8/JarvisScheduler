package com.SmartScheduler.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SmartScheduler.DTO.TaskDTO;
import com.SmartScheduler.Entity.Task;
import com.SmartScheduler.Entity.User;
import com.SmartScheduler.Repository.TaskRepository;
import com.SmartScheduler.Repository.UserRepository;
import com.SmartScheduler.Scheduler.TaskEmailConsumer;
import com.joestelmach.natty.Parser;

@Service
public class TaskService {

    @Autowired
    private final TaskRepository taskRepository;

    @Autowired
    private final UserRepository userRepository;
    
    private final Scheduler scheduler;
    private final RabbitTemplate rabbitTemplate;
    private final HuggingFaceService huggingFaceService;

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    @Autowired
    private TaskEmailConsumer taskEmailConsumer;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, Scheduler scheduler, RabbitTemplate rabbitTemplate, HuggingFaceService huggingFaceService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.scheduler = scheduler;
        this.rabbitTemplate = rabbitTemplate;
        this.huggingFaceService = huggingFaceService;
    }

    public TaskDTO createTaskWithReminder(String email, TaskDTO taskDTO, LocalDateTime reminderTime) throws SchedulerException {
        
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        Task task = new Task();
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(reminderTime);
        task.setCompleted(false);
        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        String taskTips = "";
        try {
            taskTips = huggingFaceService.generateTaskTips(task.getDescription());
        } catch (Exception e) {
            System.out.println("Task tips could not be generated.");
            return null;
        }

        Map<String, String> emailPayload = new HashMap<>();
        emailPayload.put("taskName", savedTask.getName());
        emailPayload.put("taskDescription", savedTask.getDescription());
        emailPayload.put("taskTips", taskTips);
        emailPayload.put("email", savedTask.getUser().getEmail());

        taskEmailConsumer.processTaskReminder(emailPayload);

        taskSchedulerService.scheduleTaskReminder(savedTask, reminderTime);

        return new TaskDTO(
                savedTask.getId(),
                savedTask.getName(),
                savedTask.getDescription(),
                savedTask.getDueDate(),
                savedTask.getCompleted()
        );
    }

    public Optional<TaskDTO> getTaskById(Long id) {
        return taskRepository.findById(id)
            .map(task -> new TaskDTO(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getDueDate(),
                task.getCompleted()
            ));
    }

    public TaskDTO createTask(String email, TaskDTO taskDTO) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        Task task = new Task();
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setCompleted(false);
        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        return new TaskDTO(
            savedTask.getId(),
            savedTask.getName(),
            savedTask.getDescription(),
            savedTask.getDueDate(),
            savedTask.getCompleted()
        );
    }

    public TaskDTO createTaskWithNaturalLanguage(String email, String title, String description, String naturalLanguageDate) throws SchedulerException {
        if (naturalLanguageDate == null || naturalLanguageDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Date input cannot be null or empty.");
        }

        Parser parser = new Parser();
        List<Date> dates = parser.parse(naturalLanguageDate).get(0).getDates();

        if (dates.isEmpty()) {
            throw new IllegalArgumentException("Could not parse date from input.");
        }

        System.out.println("Parsed Dates: " + dates);

        Date reminderTime = dates.get(0);

        if (reminderTime.before(new Date())) {
            throw new IllegalArgumentException("Reminder time must be in the future.");
        }

        LocalDateTime timestamp = LocalDateTime.ofInstant(reminderTime.toInstant(), ZoneId.systemDefault());

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName(title);
        taskDTO.setDescription(description);
        taskDTO.setCompleted(false);
        taskDTO.setDueDate(timestamp);  

        try {
            System.out.println("Task Created Successfully");
            return createTaskWithReminder(email, taskDTO, timestamp);
        } catch (Exception e) {
            System.err.println("Error creating task with reminder: " + e.getMessage());
            throw new SchedulerException("Failed to create task with reminder.", e);
        }
    }

    public List<TaskDTO> getTasksByUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        return user.getTasks().stream()
            .map(task -> new TaskDTO(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getDueDate(),
                task.getCompleted()
            ))
            .collect(Collectors.toList());
    }

    public Optional<TaskDTO> getTaskByIdAndUser(Long id, String email) {
        return taskRepository.findById(id).filter(task -> task.getUser().getEmail().equals(email)).map(task -> {
            return new TaskDTO(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getDueDate(),
                task.getCompleted()
            );
        });
    }

    public TaskDTO updateTask(Long id, String email, TaskDTO updatedTaskDTO) {
        return taskRepository.findById(id).map(task -> {
            if (!task.getUser().getEmail().equals(email)) {
                throw new IllegalArgumentException("Task does not belong to user with email: " + email);
            }
            task.setName(updatedTaskDTO.getName());
            task.setDescription(updatedTaskDTO.getDescription());
            task.setDueDate(updatedTaskDTO.getDueDate());
            task.setCompleted(updatedTaskDTO.getCompleted());
            Task savedTask = taskRepository.save(task);

            return new TaskDTO(
                savedTask.getId(),
                savedTask.getName(),
                savedTask.getDescription(),
                savedTask.getDueDate(),
                savedTask.getCompleted()
            );
        }).orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }

    public void deleteTask(Long id, String email) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found with id " + id));
        if (!task.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Task does not belong to user with email: " + email);
        }
        taskRepository.deleteById(id);
    }
}
