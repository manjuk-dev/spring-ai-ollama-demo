package com.example.demo.features.general;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIController {

    private final ChatClient chatClient;

    public AIController(@Qualifier("ollamaChatModel") ChatModel ollamaModel) {
        this.chatClient = ChatClient.builder(ollamaModel).build();

    }

    @GetMapping("/ai/generate")
    public String generate(@RequestParam(defaultValue = "Who is Ronaldo?") String message) {
        return chatClient.prompt().user(message)
                .call()
                .content();
    }
}
