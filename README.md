# BDF RAG System

**A full-stack Retrieval-Augmented Generation (RAG) system for chatting with your documents locally.**

This project allows users to upload PDF documents, automatically vectorizes them, and enables natural language chat with the content using local LLMs. It is built with **Spring AI**, **Angular**, and **Ollama**, ensuring complete data privacy by running entirely on your local machine.

![Architecture Diagram](image_ffcb0c.png)  
<img width="1024" height="565" alt="image" src="https://github.com/user-attachments/assets/33e7463d-2035-47d0-9465-01654266ddec" />

> *System Architecture Overview*

---

## 🚀 Features

- **Document Ingestion**: Seamlessly upload PDF files. The system uses **Apache Tika** to extract text and **TokenTextSplitter** to chunk data for optimal retrieval.
- **Vector Search**: Stores document embeddings in **PostgreSQL** using the `pgvector` extension for efficient similarity search.
- **Contextual Chat**: Ask questions about your uploaded documents. The system retrieves relevant chunks and uses **Llama 3** to generate accurate answers.
- **Management**: View uploaded files and delete them to remove their context from the knowledge base.
- **Local Privacy**: Powered by **Ollama**, ensuring no data leaves your infrastructure.

---

## 🛠️ Tech Stack

### Backend (`bdf-rag-backend`)
- **Framework**: Spring Boot 3 (Java 21)
- **AI Integration**: Spring AI
- **Database**: PostgreSQL 16 + `pgvector`
- **LLM Engine**: Ollama (Dockerized)

### Frontend (`bdf-frontend`)
- **Framework**: Angular 17+
- **Architecture**: Standalone Components, Signal-based State Management
- **Styling**: Custom CSS (Responsive Grid Layout)

---

## 📋 Prerequisites

- **Docker Desktop** (required for running containers)
- **Java 21** (optional, for local development)
- **Node.js / npm** (optional, for local development)

---

## ⚡ Quick Start

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd bdf-rag-system
```

### 2. Start with Docker Compose
```bash
docker compose up -d --build
```

### 3. Open the App
- Frontend: http://localhost:4200  
- Backend API: http://localhost:8080 (or whatever port you configured)

---

## 📖 Usage Guide

### Upload
Navigate to http://localhost:4200. Drag and drop your PDF files into the **Documents** sidebar.

### Process
The system ingests the file, splits it into chunks, and stores vector embeddings in the database.

### Chat
Type your query in the chat window. The AI will answer **based strictly on the content** of your uploaded files.

### Manage
Click the **×** button next to a file to delete it and its associated vectors from the database.

---

## 🔌 API Reference

The backend exposes the following REST endpoints:

| Method | Endpoint      | Description |
|-------:|---------------|-------------|
| POST   | `/api/upload` | Upload a file (`multipart/form-data`) for ingestion. |
| POST   | `/api/chat`   | Send a chat query. Body: `{ "query": "your question" }` |
| DELETE | `/api/delete` | Delete a document. Query param: `?filename=example.pdf` |

---

## 📤 Exporter vers Sheets

> TODO: describe how you export data to Google Sheets (what data, where, and how).

---

## 🔧 Configuration

Key environment variables in `docker-compose.yml`:

- `SPRING_AI_OLLAMA_BASE_URL`: URL for the Ollama service.
- `SPRING_AI_OLLAMA_CHAT_OPTIONS_MODEL`: Model used for chat (default: `llama3`).
- `SPRING_AI_OLLAMA_EMBEDDING_OPTIONS_MODEL`: Model used for embeddings (default: `nomic-embed-text`).
- `SPRING_DATASOURCE_URL`: PostgreSQL connection string.

---

## 📂 Project Structure

```text
bdf-rag-system/
├── bdf-rag-backend/       # Spring Boot Application
│   ├── src/main/java/     # Hexagonal Architecture (Domain, Ports, Adapters)
│   └── Dockerfile         # Backend container definition
├── bdf-frontend/          # Angular Application
│   ├── src/app/features/  # Chat and Upload components
│   └── Dockerfile         # Frontend container definition
├── docker-compose.yml     # Orchestration for App, DB, and Ollama
└── start.sh               # Automation script
```
