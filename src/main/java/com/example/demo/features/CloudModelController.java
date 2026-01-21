package com.example.demo.features;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/v1/googleAi")
public class CloudModelController {

    private final ChatClient googleClient;
    private final ChatClient ollamaClient;

    public CloudModelController(@Qualifier("googleGenAiChatModel") ChatModel googleModel, @Qualifier("googleGenAiChatModel") ChatModel ollamaModel) {
        this.googleClient = ChatClient.builder(googleModel).build();
        this.ollamaClient = ChatClient.builder(ollamaModel).build();
    }

    @GetMapping("/generate")
    public String generate(@RequestParam String question) {
        try {
            // first use google Gemini 2.5 Lite first
            return googleClient.prompt()
                    .user(question)
                    .call()
                    .content();
        } catch (Exception ex) {
            // in case any exception occurs while using cloud gemini ai, like 404 error or 429
            // fall back to local ollama ai model
            System.err.println("Google AI unavailable: " + ex.getMessage());
            return "[Ollama Fallback] " + ollamaClient.prompt()
                    .user(question)
                    .call()
                    .content();
        }
    }
}
