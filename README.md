# Spring AI Hybrid Gateway: Gemini & Ollama

This project demonstrates a Hybrid AI Orchestration architecture using Spring Boot 3 and Spring AI. It bridges the gap
between high-performance cloud intelligence and privacy-first local processing.

## Tech Stack

- **Framework:** Spring Boot 3.x
- **AI Integration:** Spring AI
- **Cloud LLM:** Google Gemini 2.5 Flash Lite
- **Local LLM:** Ollama
- **Models:**
    - **Chat:** `llama3.2:1b` (General reasoning & generation)
    - **Embeddings:** `nomic-embed-text:latest` (Converting text to vectors)
- **Vector Store:** SimpleVectorStore (In Memory)
- **Architecture:** **RAG (Retrieval-Augmented Generation)**

## Prerequisites

1. Install [Ollama](https://ollama.com/).
2. Download the lightweight Llama model:
   ```bash
   ollama pull llama3.2:1b
   ollama pull nomic-embed-text
3. Get a Google API Key from AI Studio.
4. Update application properties with api key and model.
   # Google Gemini (Cloud)

spring.ai.google.genai.api-key=${GOOGLE_AI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-2.5-flash-lite

## Core Concepts: What is RAG?

This project implements Retrieval-Augmented Generation (RAG). Instead of relying only on the AI's pre-trained knowledge,
the application:

-Retrieves relevant snippets from uploaded documents using nomic-embed-text.

-Augments the user's question with that specific context.

-Generates an answer using llama3.2 based strictly on the provided data.

## Key Features

1. Cloud Model Controller (CloudModelController):
   
A.Uses Google Gemini for high-performance generation.

Endpoint: GET /ai/v1/googleAi/generate

Example: curl "http://localhost:8080/ai/v1/googleAi/generate?question=What+is+2+plus+2?"

B.Multimodal Vision (Cloud)

Leverage Gemini's vision capabilities to analyze images, OCR documents, or explain complex diagrams.

Endpoint: POST /ai/v1/googleAi/vision

Testing on bash

curl -X POST http://localhost:8080/ai/v1/googleAi/vision -F "question=What is this error?" -F "file=@error.jpg"

2. General AI Controller (AIController):
   A standard implementation for general-purpose questions using the local LLM.

Endpoint: GET /ai/generate

Example: http://localhost:8080/ai/generate?message=Who+is+Ronaldo?

3. Customer Support Agent (SupportController):
   This controller is specialized using a System Prompt ("You are a customer support agent...") and supports real-time
   streaming for a better user experience.

A. Standard Response (Wait for full answer)
Endpoint: GET /support/ask

Example: http://localhost:8080/support/ask?question=How+do+I+reset+my+password?
B. Streaming Response (Words appear one-by-one)
This uses Server-Sent Events (SSE) to stream the AI response in real-time.

Endpoint: GET /support/stream

Testing via Terminal (Recommended):

Bash

curl -N "http://localhost:8080/support/stream?question=Explain+quantum+computing"

4. Knowledge Base (RAG):
   Supports document uploads (PDFs, etc.) to provide domain-specific context for the AI.

⚠️ Persistence Note: The current implementation uses an In-Memory store. Data is cleared upon application restart,
requiring documents to be re-uploaded to the knowledge base for each new session.

A. Upload a Document
Parses the document, chunks the text, and generates embeddings for search.

Endpoint: POST /api/v1/kb/documents

Command:

Bash

curl -X POST -F "file=@my_resume.pdf" http://localhost:8080/api/v1/kb/documents

B. Ask Questions (Streaming)
The AI will answer based only on the documents you uploaded.

Endpoint: GET /api/v1/kb/ask

Example: http://localhost:8080/api/v1/kb/ask?question=what+are+the+leadership+roles+smith+holds?
