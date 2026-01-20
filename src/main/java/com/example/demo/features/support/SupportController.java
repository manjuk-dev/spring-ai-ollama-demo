package com.example.demo.features.support;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/support") // This prefixes all URLs in this class
public class SupportController {

    private final ChatClient chatClient;

    public SupportController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("You are a customer support agent for a tech company.")
                .build();
    }

    // 1. ORIGINAL METHOD (Wait for full answer)
    // URL: http://localhost:8080/support/ask
    @GetMapping("/ask")
    public String handleSupport(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    // 2. NEW STREAMING METHOD (Words appear one by one)
    // URL: http://localhost:8080/support/stream
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamSupport(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                .stream()
                .content();
    }
}