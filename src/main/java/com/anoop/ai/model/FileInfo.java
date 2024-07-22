package com.anoop.ai.model;

import lombok.Builder;

@Builder
public record FileInfo(String name, String url) {
}
