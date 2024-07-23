package com.anoop.ai.data.vector;

import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.filter.Filter;
import org.springframework.stereotype.Component;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Component
public class DocumentQueryBuilder {

    public Filter filter(Query query) {
        String userId = (String) query.metadata().chatMemoryId();
        return metadataKey("userId").isEqualTo(userId);
    }
}
