package com.ezplatforms.ezcaisse.bdf.ragbackend;

import com.ezplatforms.ezcaisse.bdf.ragbackend.domain.port.in.ChatUseCase;
import com.ezplatforms.ezcaisse.bdf.ragbackend.domain.port.in.IngestUseCase;
import com.ezplatforms.ezcaisse.bdf.ragbackend.infrastructure.adapter.in.web.RagController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RagController.class)
class BdfRagBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngestUseCase ingestUseCase;

    @MockBean
    private ChatUseCase chatUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Common setup if needed
    }

    @Test
    void upload_WithValidFile_ShouldReturnSuccess() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );
        doNothing().when(ingestUseCase).ingestDocument(any());

        // When & Then
        mockMvc.perform(multipart("/api/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("File processed successfully"));
    }

    @Test
    void upload_WithNullFile_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/upload"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("File is missing or empty"));
    }

    @Test
    void upload_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        // When & Then
        mockMvc.perform(multipart("/api/upload")
                        .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("File is missing or empty"));
    }

    @Test
    void delete_WithValidFilename_ShouldReturnSuccess() throws Exception {
        // Given
        String filename = "test.pdf";
        doNothing().when(ingestUseCase).deleteDocument(anyString());

        // When & Then
        mockMvc.perform(delete("/api/delete")
                        .param("filename", filename))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("File deleted from context: " + filename));
    }

    @Test
    void delete_WithEmptyFilename_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/delete")
                        .param("filename", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("filename is required"));
    }

    @Test
    void delete_WithNullFilename_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/delete"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("filename is required"));
    }

    @Test
    void delete_WithWhitespaceFilename_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/delete")
                        .param("filename", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("filename is required"));
    }

    @Test
    void chat_WithValidQuery_ShouldReturnAnswer() throws Exception {
        // Given
        String query = "What is the capital of France?";
        String expectedAnswer = "The capital of France is Paris.";
        RagController.ChatRequest request = new RagController.ChatRequest(query);
        when(chatUseCase.answerQuestion(query.trim())).thenReturn(expectedAnswer);
        String requestBody = Objects.requireNonNull(objectMapper.writeValueAsString(request));

        // When & Then
        mockMvc.perform(post("/api/chat")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(expectedAnswer));
    }

    @Test
    void chat_WithNullPayload_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/chat")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.answer").value("Query is missing"));
    }

    @Test
    void chat_WithEmptyQuery_ShouldReturnBadRequest() throws Exception {
        // Given
        RagController.ChatRequest request = new RagController.ChatRequest("");
        String requestBody = Objects.requireNonNull(objectMapper.writeValueAsString(request));

        // When & Then
        mockMvc.perform(post("/api/chat")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.answer").value("Query is missing"));
    }

    @Test
    void chat_WithNullQuery_ShouldReturnBadRequest() throws Exception {
        // Given
        RagController.ChatRequest request = new RagController.ChatRequest(null);
        String requestBody = Objects.requireNonNull(objectMapper.writeValueAsString(request));

        // When & Then
        mockMvc.perform(post("/api/chat")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.answer").value("Query is missing"));
    }

    @Test
    void chat_WithWhitespaceQuery_ShouldReturnBadRequest() throws Exception {
        // Given
        RagController.ChatRequest request = new RagController.ChatRequest("   ");
        String requestBody = Objects.requireNonNull(objectMapper.writeValueAsString(request));

        // When & Then
        mockMvc.perform(post("/api/chat")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.answer").value("Query is missing"));
    }

    @Test
    void chat_WithQueryContainingWhitespace_ShouldTrimAndReturnAnswer() throws Exception {
        // Given
        String query = "  What is the capital of France?  ";
        String trimmedQuery = "What is the capital of France?";
        String expectedAnswer = "The capital of France is Paris.";
        RagController.ChatRequest request = new RagController.ChatRequest(query);
        when(chatUseCase.answerQuestion(trimmedQuery)).thenReturn(expectedAnswer);
        String requestBody = Objects.requireNonNull(objectMapper.writeValueAsString(request));

        // When & Then
        mockMvc.perform(post("/api/chat")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(expectedAnswer));
    }

}
