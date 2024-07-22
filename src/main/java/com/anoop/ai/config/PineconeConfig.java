package com.anoop.ai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("pinecone")
@Configuration
public class PineconeConfig {

    @Value("${pinecone.db.api.key}")
    private String apikey;

    @Value("${pinecone.db.region}")
    private String region;

    @Value("${pinecone.db.index}")
    private String index;

    @Value("${pinecone.db.project.id}")
    private String projectId;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return PineconeEmbeddingStore.builder()
                .apiKey(apikey)
                .environment(region)
                .projectId(projectId)
                .index(index)
                .build();
    }
}
