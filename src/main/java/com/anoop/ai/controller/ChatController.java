package com.anoop.ai.controller;

import com.anoop.ai.model.AIChatMessage;
import com.anoop.ai.services.OpenAIService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller
public class ChatController {

    private final OpenAIService openAIService;

    @MessageMapping("/chat.register")
    @SendTo("/topic/public")
    public AIChatMessage register(@Payload AIChatMessage AIChatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", AIChatMessage.sender());
        return AIChatMessage;
    }

    @MessageMapping("/chat.send/{sender}")
    @SendTo("/topic/{sender}")
    public AIChatMessage sendMessage(@Payload AIChatMessage aiChatMessage) {
        openAIService.streamingChat(aiChatMessage);
        return aiChatMessage;
    }
}