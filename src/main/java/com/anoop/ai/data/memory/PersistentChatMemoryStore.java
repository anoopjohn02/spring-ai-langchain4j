package com.anoop.ai.data.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PersistentChatMemoryStore implements ChatMemoryStore {

    Map<Object, List<ChatMessage>> messages = new HashMap<>();

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return messages.getOrDefault((String) memoryId, new ArrayList<>());
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        messages.put((String) memoryId, list);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        messages.remove((String) memoryId);
    }
}
