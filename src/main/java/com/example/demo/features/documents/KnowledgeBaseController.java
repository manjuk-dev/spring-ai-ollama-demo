package com.example.demo.features.documents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/kb") // base url
public class KnowledgeBaseController {

    private final DocumentService documentService;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public KnowledgeBaseController(DocumentService documentService, ChatClient.Builder builder, VectorStore vectorStore) {
        this.documentService = documentService;
        this.vectorStore = vectorStore;
        this.chatClient = builder.build();
    }

    /**
     * Endpoint to ingest new documents into the local knowledge base.
     * Accepts a PDF file, parses it into chunks, and stores the resulting
     * embeddings in the vector database for future retrieval.
     *
     * @param file The PDF document to be added to the AI context.
     * @return Success message upon completion of the ingestion pipeline.
     */
    @PostMapping("/documents")
    public String uploadDocument(@RequestParam MultipartFile file) {
        documentService.processPDf(file);
        return "File uploaded to knowledge base!";
    }

    /**
     * Streams an AI-generated response based on the uploaded knowledge base.
     * This endpoint implements the RAG (Retrieval-Augmented Generation) pattern.
     *
     * @param question The user's natural language query.
     * @return A Flux stream of text chunks directly from the local LLM.
     */
    @GetMapping(value = "/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> ask(@RequestParam String question) {

        // 1. Configure the Retrieval parameters
        // topK(3): We fetch the 3 most relevant snippets to stay within Llama 1B's context limits.
        // similarityThreshold(0.4): Filters out low-quality matches (0.0 = everything, 1.0 = exact match).
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(3)
                .similarityThreshold(0.4)
                .build();

        // 2. Perform Vector Search (The "Retrieval" in RAG)
        List<Document> contextDocs = vectorStore.similaritySearch(searchRequest);

        // Logging for traceability in local development
        System.out.println("DEBUG: Retrieval initiated for query: [" + question + "]");
        System.out.println("DEBUG: Found " + contextDocs.size() + " relevant document chunks.");

        // 3. Fallback/Debugging Logic
        // If no results meet the threshold, we perform a 'raw' search to inspect the similarity scores.
        if (contextDocs.isEmpty()) {
            List<Document> rawDocs = vectorStore.similaritySearch(
                    SearchRequest.builder().query(question).topK(3).build()
            );
            rawDocs.forEach(d ->
                    System.out.println("DEBUG: Discarded match score: " + d.getMetadata().get("distance"))
            );
        }

        // 4. Transform retrieved documents into a single context String
        String context = contextDocs.stream()
                .map(Document::getText)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n\n"));

        // 5. Augment the Prompt and Generate (The "Augmentation" & "Generation" in RAG)
        return chatClient.prompt()
                .user(u -> u.text("""
                            You are a helpful assistant. Use ONLY the following context to answer the question.
                            If the answer is not found in the context, clearly state that you do not know.
                            
                            ---
                            CONTEXT:
                            {context}
                            ---
                            
                            QUESTION: 
                            {question}
                            """)
                        .param("context", context)
                        .param("question", question))
                .stream()
                .content();
    }
}
