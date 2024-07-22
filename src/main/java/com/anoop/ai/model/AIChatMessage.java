package com.anoop.ai.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AIChatMessage(UUID messageId, String content, String sender, MessageType type) {
}
