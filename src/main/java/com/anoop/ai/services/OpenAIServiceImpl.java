package com.anoop.ai.services;

import com.anoop.ai.model.AIChatMessage;
import com.anoop.ai.model.Answer;
import com.anoop.ai.model.MessageType;
import com.anoop.ai.model.Question;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenAIServiceImpl implements OpenAIService{

    private final ChatLanguageModel chatLanguageModel;

    private final Assistant aiAssistant;
    private final StreamingAssistant streamingAssistant;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final String WS_MESSAGE_DESTINATION = "/topic";

    @Override
    public Answer answer(Question question) {
        List<ChatMessage> messages = List.of(
                //SystemMessage.systemMessage("Answer in one line"),
                UserMessage.userMessage(question.question())
        );
        Response<AiMessage> response = chatLanguageModel.generate(messages);
        return Answer.builder()
                .answer(response.content().text())
                .build();
    }

    @Override
    public Answer answerWithMemory(Question question, String id) {
        log.info("User: {}", id);
        return Answer.builder()
                .answer(aiAssistant.chat(id, question.question()))
                .build();
    }

    @Async
    @Override
    public void streamingChat(AIChatMessage message) {
        UUID messageId = UUID.randomUUID();
        TokenStream stream = streamingAssistant.chat(message.sender(), message.content());
        stream.onNext(token -> tokenReceived(message.sender(), messageId, token))
                .onComplete(aiMessageResponse -> completed(message.sender(), messageId))
                .onError(error -> log.error("Error", error))
                .start();
    }

    private void tokenReceived(String userId, UUID messageId, String token) {
        log.info(token);
        AIChatMessage message = AIChatMessage.builder()
                .messageId(messageId)
                .content(token)
                .sender("AI")
                .type(MessageType.CHAT)
                .build();
        send(userId, message);
    }

    private void completed(String userId, UUID messageId) {
        AIChatMessage message = AIChatMessage.builder()
                .messageId(messageId)
                .content("\n")
                .sender("AI")
                .type(MessageType.STREAM_COMPLETED)
                .build();
        send(userId, message);
    }

    private void send(String userId, AIChatMessage message){
        String destination = WS_MESSAGE_DESTINATION + "/" + userId;
        simpMessagingTemplate.convertAndSend(destination, message);
    }
}
