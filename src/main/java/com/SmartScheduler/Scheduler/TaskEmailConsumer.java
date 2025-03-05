package com.SmartScheduler.Scheduler;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class TaskEmailConsumer {

    private final JavaMailSender mailSender;

    public TaskEmailConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(queues = "taskQueue")
    public void processTaskReminder(Map<String, String> emailPayload) {
        try {
            String recipientEmail = emailPayload.get("email");
            String subject = "Task Reminder: " + emailPayload.get("taskName");
            String message = "Task Description: " + emailPayload.get("taskDescription");
            String tips = "Task Tips: " + emailPayload.get("taskTips");

            // Send email
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(recipientEmail);
            mailMessage.setSubject(subject);
            if(tips == "null"){mailMessage.setText(message);}
            else{mailMessage.setText(message + " \n\n" +  tips);}
            mailSender.send(mailMessage);
            System.out.println("Reminder email sent to: " + recipientEmail);
        } catch (Exception e) {
            System.err.println("Error while sending reminder email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
