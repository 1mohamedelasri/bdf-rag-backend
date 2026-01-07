package com.ezplatforms.ezcaisse.bdf.ragbackend.domain.port.in;

import org.springframework.web.multipart.MultipartFile;

public interface IngestUseCase {
    void ingestDocument(MultipartFile file);
    void deleteDocument(String filename);
}