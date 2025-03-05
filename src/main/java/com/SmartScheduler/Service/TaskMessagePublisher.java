package com.SmartScheduler.Service;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskMessagePublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = ""; 
    private static final String QUEUE_NAME = "taskQueue";

    public void sendTaskReminder(Map<String, String> payload) {
        try {
            System.out.println("Publishing task reminder message to RabbitMQ for task: " + payload.get("taskName"));
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, QUEUE_NAME, payload);
            System.out.println("Successfully published message to RabbitMQ");
        } catch (Exception e) {
            System.err.println("Failed to publish message to RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
