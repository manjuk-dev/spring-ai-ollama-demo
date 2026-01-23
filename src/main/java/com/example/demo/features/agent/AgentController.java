package com.example.demo.features.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/v1/agent")
public class AgentController {

    String toolSystemPrompt = """
                You are a system monitor.
                1. If the user asks about the computer, use the 'getSystemStatus' tool.
                2. Read the result and explain it to the user in one simple sentence.
                3. Do NOT output JSON, code, or technical schemas. Just plain English.
                """;

    private final ChatClient googleAgentClient;

    private final ChatClient ollamaAgentClient;

    public AgentController(@Qualifier("ollamaChatModel") ChatModel ollamaModel, @Qualifier("googleGenAiChatModel") ChatModel googlemodel, SystemInfoTool systemTool) {

        this.googleAgentClient = ChatClient.builder(googlemodel)  // using google gemini
                .defaultTools(systemTool)  // register the tool
                .build();

        this.ollamaAgentClient = ChatClient.builder(ollamaModel)
                .defaultSystem(toolSystemPrompt)
                .defaultTools(systemTool)
                .build();
    }

    @GetMapping("/ask")
    public String askWithFallback(@RequestParam String question) {
        try {
            String resp = googleAgentClient.prompt()
                    .user(question)
                    .call()
                    .content();
            return resp + " provided by google gemini model";
        } catch (Exception ex) {

            // Check if it's a Quota/429 error
            System.err.println("Gemini Quota Exceeded! Falling back to Ollama...");
            String resp = ollamaAgentClient
                    .prompt()
                    .user(question)
                    .call().content();
            return resp + " provided by ollama model";
        }
    }
}
