package com.anoop.ai.services;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface StreamingAssistant {
    @SystemMessage(
            """
             My name is {{name}}. Use my name to greet me.
             You are a helpful assistant. Try to respond in a fair and warm manner.
             If you don't know answer, just tell it.
             """
    )
    TokenStream chat(@MemoryId @V("name") String memoryId, @UserMessage String userMessage);
}
