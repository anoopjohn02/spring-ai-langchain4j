package com.anoop.ai.services;

import com.anoop.ai.model.AIChatMessage;
import com.anoop.ai.model.Answer;
import com.anoop.ai.model.Question;

public interface OpenAIService {
    Answer answer(Question question);
    Answer answerWithMemory(Question question, String id);

    void streamingChat(AIChatMessage message);
}
