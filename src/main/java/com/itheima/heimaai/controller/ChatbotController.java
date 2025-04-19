package com.itheima.heimaai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.itheima.heimaai.repository.ChatHistoryRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/ai")
public class ChatbotController {

    private final DashScopeChatModel dashScopeChatModel;
    public ChatbotController(ChatClient chatClient, InMemoryChatMemory inMemoryChatMemory, ChatHistoryRepository chatHistoryRepository, DashScopeChatModel dashScopeChatModel) {
        this.dashScopeChatModel = dashScopeChatModel;
    }

    @GetMapping("/chat1/{prompt}")
    public String chat1(@PathVariable("prompt") String prompt) {
        ChatResponse response = dashScopeChatModel.call(new Prompt(prompt));
        if(!response.getResults().isEmpty()){
            Map<String,Object> metadata = response.getResults().get(0).getOutput().getMetadata();
            log.info("metadata:{}",metadata.get("reasoningContent"));
        }
        return response.getResult().getOutput().getContent();
    }
}
