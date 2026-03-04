package com.hms.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(
    origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500"
    }
)
public class ChatController {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max-tokens}")
    private int maxTokens;

    @Value("${openai.temperature}")
    private double temperature;

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        Map<String, Object> result = new HashMap<>();

        try {
            RestTemplate rest = new RestTemplate();

            String systemPrompt = "You are a helpful healthcare assistant for Smart Healthcare system. "
                + "Provide general health information, symptom guidance (always recommend professional "
                + "consultation for serious issues), medicine reminders, and wellness tips. Keep responses "
                + "concise (under 100 words), friendly, and professional. Never prescribe medication or "
                + "give definitive diagnoses. Be encouraging and empathetic.";

            Map<String, Object> systemInstruction = new HashMap<>();
            systemInstruction.put("parts", List.of(Map.of("text", systemPrompt)));

            Map<String, Object> content = new HashMap<>();
            content.put("role", "user");
            content.put("parts", List.of(Map.of("text", userMessage)));

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("maxOutputTokens", maxTokens);
            generationConfig.put("temperature", temperature);

            Map<String, Object> body = new HashMap<>();
            body.put("systemInstruction", systemInstruction);
            body.put("contents", List.of(content));
            body.put("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = rest.exchange(
                "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey,
                HttpMethod.POST,
                entity,
                Map.class
            );

            Map<String, Object> geminiBody = response.getBody();
            Map<String, Object> fakeOpenai = new HashMap<>();
            List<Map<String, Object>> choices = new ArrayList<>();

            if (geminiBody != null && geminiBody.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) geminiBody.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> contentObj = (Map<String, Object>) candidate.get("content");
                    if (contentObj != null) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentObj.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            String text = (String) parts.get(0).get("text");

                            Map<String, Object> message = new HashMap<>();
                            message.put("role", "assistant");
                            message.put("content", text);

                            Map<String, Object> choice = new HashMap<>();
                            choice.put("index", 0);
                            choice.put("message", message);

                            choices.add(choice);
                        }
                    }
                }
            }
            fakeOpenai.put("choices", choices);
            result.put("reply", fakeOpenai);
        } catch (Exception e) {
            System.err.println("Gemini API error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", e.getMessage());
            result.put("reply", errorBody);
        }

        return result;
    }
}
