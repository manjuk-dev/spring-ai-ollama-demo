package com.example.demo.features.documents;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private final VectorStore vectorStore;

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Extracts text from an uploaded PDF, chunks it into tokens,
     * and persists it to the vector database for RAG.
     * * @param file The multipart PDF file from the user request.
     * @throws RuntimeException if the file cannot be read or processed.
     */
    public void processPDf(MultipartFile file) {
        try {
            // Use Tika to abstract away the PDF parsing complexity
            Resource resource = new InputStreamResource(file.getInputStream());
            TikaDocumentReader reader = new TikaDocumentReader(resource);

            // ETL Pipeline: Extract -> Transform (Split) -> Load (Add)
            List<Document> documents = reader.get();

            // Split text by tokens to ensure chunks fit within LLM context windows
            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> chunks = splitter.apply(documents);

            // Load chunks into the vector store for similarity search
            vectorStore.add(chunks);

        } catch (IOException e) {
            // Log the failure and wrap in a RuntimeException for clean upstream handling
            throw new RuntimeException("Failed to ingest PDF into the vector store", e);
        }
    }
}
