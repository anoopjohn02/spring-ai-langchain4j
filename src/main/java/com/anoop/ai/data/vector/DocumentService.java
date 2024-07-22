package com.anoop.ai.data.vector;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final EmbeddingStoreIngestor ingestor;

    @Async
    public void loadSingleDocument(String documentPath, String userId) {
        Document document = loadDocument(documentPath, new ApacheTikaDocumentParser());
        document.metadata().put("userId", userId);
        ingestor.ingest(document);
    }
}
