#!/bin/bash
set -e

# --- Configuration ---
# Must match docker-compose (SPRING_AI_OLLAMA_CHAT_OPTIONS_MODEL)
CHAT_MODEL="llama3"
EMBED_MODEL="nomic-embed-text"

echo "🛑 Stopping containers and wiping data (DB + Ollama models + everything)..."
docker-compose down -v

echo "🚀 Building and starting containers..."
docker-compose up -d --build

echo "⏳ Waiting 10 seconds for Ollama to wake up..."
sleep 10

echo "🧠 Downloading AI Models (prevents model-not-found 500s)..."
echo "   - Pulling chat model: $CHAT_MODEL..."
docker exec -it bdf-ollama ollama pull "$CHAT_MODEL"

echo "   - Pulling embedding model: $EMBED_MODEL..."
docker exec -it bdf-ollama ollama pull "$EMBED_MODEL"

echo "🔄 Restarting Backend to sync with models..."
docker restart bdf-backend

echo "✅ System is Ready!"
echo "   - Frontend: http://localhost:4200"
echo "   - Backend:  http://localhost:8080"
