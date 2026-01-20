package com.example.demo.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    // Creates and manages a SimpleVectorStore as a Spring Bean.
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // SimpleVectorStore uses  Ollama EmbeddingModel(nomic-embed-text) to turn text into math.
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}