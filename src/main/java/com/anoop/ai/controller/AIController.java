package com.anoop.ai.controller;

import com.anoop.ai.model.Answer;
import com.anoop.ai.model.Question;
import com.anoop.ai.services.OpenAIService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/v1/ai")
public class AIController {
    private final OpenAIService openAIService;

    @PostMapping("/chat")
    public Answer generation(@RequestBody Question question) {
        return openAIService.answer(question);
    }
    @PostMapping("/chat/memory/{id}")
    public Answer generation(@RequestBody Question question, @PathVariable(name = "id") String id) {
        return openAIService.answerWithMemory(question, id);
    }

}
