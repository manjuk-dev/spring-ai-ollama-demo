package com.example.demo.features.ChatMemory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient ollamaClient;

    public ChatController(@Qualifier("ollamaChatModel") ChatModel ollamaModel, ChatMemory chatMemory) {
        this.ollamaClient = ChatClient.builder(ollamaModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @GetMapping("/ai/chat")
    public String chat(@RequestParam String message, @RequestParam String userId) {
        return this.ollamaClient.prompt().user(message).advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                .call().content();
    }

}
