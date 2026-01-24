package com.example.demo.features.MultimodalCloud;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/ai/v1/googleAi")
public class CloudModelController {

    private final ChatClient googleClient;
    private final ChatClient ollamaClient;

    public CloudModelController(@Qualifier("googleGenAiChatModel") ChatModel googleModel, @Qualifier("ollamaChatModel") ChatModel ollamaModel) {
        this.googleClient = ChatClient.builder(googleModel).build();
        this.ollamaClient = ChatClient.builder(ollamaModel).build();
    }

    // API to query in general with cloud model
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

    // API to analyse the image
    // eg: curl -X POST http://localhost:8080/ai/v1/googleAi/vision -F "question=What is this error?" -F "file=@C:\Users\YourName\Desktop\error.png"
    @PostMapping("/vision")
    public String analyseImage(@RequestParam String question,
                               @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return "Error: Please upload a valid image file.";
        }

        String finalQuestion = (question == null || question.isEmpty()) ? "Analyze this image in detail." : question;

        // Mapping the incoming MultipartFile to a Spring AI Media resource
        var imageMedia = new Media(
                MimeTypeUtils.parseMimeType(file.getContentType()),
                file.getResource()
        );

        // Construct Multimodal Prompt using the Builder Pattern
        var userMessage = UserMessage.builder()
                .text(finalQuestion)
                .media(imageMedia)
                .build();

        // dispatch to google model
        return googleClient.prompt(new Prompt(userMessage))
                .call()
                .content();
    }
}
