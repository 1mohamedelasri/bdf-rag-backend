package com.ezplatforms.ezcaisse.bdf.ragbackend.domain.service;

import com.ezplatforms.ezcaisse.bdf.ragbackend.domain.port.in.ChatUseCase;
import com.ezplatforms.ezcaisse.bdf.ragbackend.domain.port.in.IngestUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService implements IngestUseCase, ChatUseCase {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final JdbcTemplate jdbcTemplate; // Required for deletion

    @Override
    public void ingestDocument(MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            log.info("Starting ingestion for file: {}", filename);

            // 1. Read file content
            var reader = new TikaDocumentReader(file.getResource());
            var documents = reader.read();

            // 2. CRITICAL: Tag every document chunk with the filename
            // This allows us to find and delete them later!
            documents.forEach(doc -> doc.getMetadata().put("filename", filename));

            // 3. Split content into smaller chunks
            var splitter = new TokenTextSplitter(800, 350, 5, 10000, true);
            var chunks = splitter.apply(documents);

            // 4. Store Embeddings
            vectorStore.accept(chunks);

            log.info("Successfully stored {} chunks for file: {}", chunks.size(), filename);

        } catch (Exception e) {
            log.error("Error ingesting document", e);
            throw new RuntimeException("Ingestion failed", e);
        }
    }

    @Override
    public void deleteDocument(String filename) {
        // Direct SQL delete to remove vectors associated with this specific file
        // Postgres stores metadata as JSONB, so we query inside the JSON structure
        String sql = "DELETE FROM vector_store WHERE metadata->>'filename' = ?";

        int rows = jdbcTemplate.update(sql, filename);
        log.info("Deleted {} vector chunks for file: {}", rows, filename);
    }

    @Override
    public String answerQuestion(String query) {
        log.info("Received question: {}", query);

        return chatClient.prompt()
                .user(query)
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .call()
                .content();
    }
}