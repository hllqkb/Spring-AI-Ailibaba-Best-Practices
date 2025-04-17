package com.itheima.heimaai.controller;

import groovy.util.logging.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin("*")
@Slf4j
public class ChatbotController {

    private final ChatClient chatClient;
    private final InMemoryChatMemory inMemoryChatMemory;

    public ChatbotController(ChatClient chatClient, InMemoryChatMemory inMemoryChatMemory) {
        this.chatClient = chatClient;
        this.inMemoryChatMemory = inMemoryChatMemory;
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest request) {
        //用户id
        String userId = request.userId();

        return chatClient.prompt(request.message())
                .advisors(new MessageChatMemoryAdvisor(inMemoryChatMemory, userId,10),new
                        SimpleLoggerAdvisor())
                .stream().content().map(content -> ServerSentEvent.builder(content).event("message").build())
                //问题回答结速标识,以便前端消息展示处理
                .concatWithValues(ServerSentEvent.builder("[DONE]").build())
                .onErrorResume(e -> Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).event("error").build()));
    }

    record ChatRequest(String userId, String message) {

    }
}
