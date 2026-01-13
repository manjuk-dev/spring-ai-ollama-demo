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

Once the application is running, you can test the AI controller using your browser or a terminal.

### 1. Using the Browser
Simply open the following URL in your browser:
`http://localhost:8080/ai/generate?message=who is Ronaldo ?`

### 2. Using Terminal (cURL)
Run this command to get a response directly in your terminal:
```bash
curl "http://localhost:8080/ai/generate?message=What+is+Spring+AI?"