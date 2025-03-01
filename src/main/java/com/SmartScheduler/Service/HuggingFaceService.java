package com.SmartScheduler.Service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonProperty;

@Service
public class HuggingFaceService {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(HuggingFaceService.class);

    public HuggingFaceService(@Value("${huggingface.api.key}") String apiKey,
                              @Value("${huggingface.api.url}") String apiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String generateTaskTips(String taskDescription) {
        String prompt = """
            Generate practical tips to complete this task: "%s".
            
            Follow these rules:
            1. Tips must be directly related to the task description.
            2. Return tips as a numbered list (1., 2., 3.), each in a new line.
            3. For each tip, add a concise example implementation starting with "Example:".
            4. Avoid generic advice—focus on actionable steps.
            
            Example for task "Organize a study schedule":
            1. Use time-blocking for focused sessions.
            Example: Allocate 45-minute slots for math practice with 15-minute breaks.
            2. Prioritize high-impact topics first.
            Example: Start with algebra basics before tackling advanced calculus.
            
            Now generate tips for: "%s"
            """.formatted(taskDescription, taskDescription);

        Map<String, Object> requestBody = Map.of("inputs", prompt);

        try {
            HuggingFaceResponse[] responses = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(HuggingFaceResponse[].class)
                    .block();

            if (responses != null && responses.length > 0 && responses[0].getGeneratedText() != null) {
                return responses[0].getGeneratedText().trim();
            } else {
                logger.error("No valid response received from Hugging Face API");
                return "No tips could be generated.";
            }
        } catch (Exception e) {
            logger.error("Error generating task tips", e);
            return "An error occurred while generating tips.";
        }
    }

    public static class HuggingFaceResponse {
        @JsonProperty("generated_text")
        private String generatedText;

        public String getGeneratedText() {
            return generatedText;
        }

        public void setGeneratedText(String generatedText) {
            this.generatedText = generatedText;
        }
    }
}
