package com.example.demo.Controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIController {

    private final ChatClient chatClient;

    public AIController(ChatClient.Builder builder) {
        this.chatClient = builder.build();

    }

    @GetMapping("/ai/generate")
    public String generate(@RequestParam(defaultValue = "Who is Ronaldo?") String message) {
        return chatClient.prompt().user(message)
                .call()
                .content();
    }
}
