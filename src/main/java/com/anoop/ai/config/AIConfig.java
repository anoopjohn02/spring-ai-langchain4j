package com.anoop.ai.config;

import com.anoop.ai.services.Assistant;
import com.anoop.ai.services.StreamingAssistant;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.data.segment.TextSegmentTransformer;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

;

@Configuration
@RequiredArgsConstructor
public class AIConfig {
    private final ChatLanguageModel chatLanguageModel;
    private final StreamingChatLanguageModel streamingChatLanguageModel;
    private final ChatMemoryStore chatMemoryStore;
    private final EmbeddingStore<TextSegment> embeddingStore;

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Bean
    public Assistant assistant() {
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .chatMemoryStore(chatMemoryStore)
                .maxMessages(10)
                .build();
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }

    @Bean
    public StreamingAssistant streamingAssistant() {
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .chatMemoryStore(chatMemoryStore)
                .maxMessages(10)
                .build();
        return AiServices.builder(StreamingAssistant.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .retrievalAugmentor(retrievalAugmentor())
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("text-embedding-ada-002")
                .build();
    }

    @Bean
    public RetrievalAugmentor retrievalAugmentor() {
        return DefaultRetrievalAugmentor.builder()
                .contentRetriever(retriever())
                .contentInjector(contentInjector())
                .build();
    }

    @Bean
    public ContentInjector contentInjector() {
        return DefaultContentInjector.builder()
                // .promptTemplate(...) // Formatting can also be changed
                .metadataKeysToInclude(Arrays.asList("file_name", "userId"))
                .build();
    }

    @Bean
    public ContentRetriever retriever() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel())
                //.filter(metadataKey("userId").isEqualTo("anoop"))
                .dynamicFilter(query -> {
                    String userId = (String) query.metadata().chatMemoryId();
                    return metadataKey("userId").isEqualTo(userId);
                })
                .build();
    }

    @Bean
    public EmbeddingStoreIngestor ingestor() {
        return EmbeddingStoreIngestor.builder()
                .documentTransformer(documentTransformer())
                .documentSplitter(documentSplitter())
                .textSegmentTransformer(textTransformer())
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel())
                .build();
    }

    @Bean
    public DocumentSplitter documentSplitter() {
        return DocumentSplitters
                .recursive(500, 100, new OpenAiTokenizer());
    }

    @Bean
    public TextSegmentTransformer textTransformer() {
        return textSegment -> TextSegment.from(
                textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                textSegment.metadata()
        );
    }

    @Bean
    public DocumentTransformer documentTransformer() {
        return document -> document;
    }

}
