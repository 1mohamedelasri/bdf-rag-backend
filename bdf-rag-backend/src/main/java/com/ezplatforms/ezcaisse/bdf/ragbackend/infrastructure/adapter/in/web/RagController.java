package com.ezplatforms.ezcaisse.bdf.ragbackend.infrastructure.adapter.in.web;

import com.ezplatforms.ezcaisse.bdf.ragbackend.domain.port.in.ChatUseCase;
import com.ezplatforms.ezcaisse.bdf.ragbackend.domain.port.in.IngestUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RagController {

    private final IngestUseCase ingestUseCase;
    private final ChatUseCase chatUseCase;

    public record ChatRequest(String query) {}

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is missing or empty"));
        }
        ingestUseCase.ingestDocument(file);
        return ResponseEntity.ok(Map.of("message", "File processed successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> delete(@RequestParam("filename") String filename) {
        if (!StringUtils.hasText(filename)) {
            return ResponseEntity.badRequest().body(Map.of("message", "filename is required"));
        }
        ingestUseCase.deleteDocument(filename.trim());
        return ResponseEntity.ok(Map.of("message", "File deleted from context: " + filename));
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody(required = false) ChatRequest payload) {
        if (payload == null || !StringUtils.hasText(payload.query())) {
            return ResponseEntity.badRequest().body(Map.of("answer", "Query is missing"));
        }

        String response = chatUseCase.answerQuestion(payload.query().trim());
        return ResponseEntity.ok(Map.of("answer", response));
    }
}
