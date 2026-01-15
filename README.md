# 🚀 Spring AI + Ollama: Local LLM Demo

This project demonstrates a "Privacy-First" AI implementation using **Spring Boot 3** and **Ollama**. It allows you to run a Large Language Model (LLM) entirely on your local machine—no API keys or internet connection required for inference.

## 🛠️ Tech Stack
- **Framework:** Spring Boot 3.x
- **AI Integration:** Spring AI
- **LLM Runner:** Ollama
- **Model:** Llama 3.2 (1B Parameters)

## 📋 Prerequisites
1. Install [Ollama](https://ollama.com/).
2. Download the lightweight Llama model:
   ```bash
   ollama pull llama3.2:1b

## 🚀 Usage & Testing
The application now supports two types of AI interactions: general queries and a specialized, streaming customer support agent.

1. General AI Controller (AIController)
   A standard implementation for general-purpose questions.

Endpoint: GET /ai/generate

Example: http://localhost:8080/ai/generate?message=Who+is+Ronaldo?

2. Customer Support Agent (SupportController)
   This controller is specialized using a System Prompt ("You are a customer support agent...") and supports real-time streaming.

A. Standard Response (Wait for full answer)
Endpoint: GET /support/ask

Example: http://localhost:8080/support/ask?question=How+do+I+reset+my+password?

B. Streaming Response (Words appear one-by-one)
This uses Server-Sent Events (SSE) to stream the AI response in real-time.

Endpoint: GET /support/stream

Testing via Terminal (Recommended):

Bash

curl -N "http://localhost:8080/support/stream?question=Explain+quantum+computin