package com.itheima.heimaai.controller;

import com.itheima.heimaai.repository.ChatHistoryRepository;
import groovy.util.logging.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
@RequestMapping("/ai")
public class ChatbotController {

    private final ChatClient chatClient;
    private final InMemoryChatMemory inMemoryChatMemory;
    private final ChatHistoryRepository chatHistoryRepository;

    public ChatbotController(ChatClient chatClient, InMemoryChatMemory inMemoryChatMemory, ChatHistoryRepository chatHistoryRepository) {
        this.chatClient = chatClient;
        this.inMemoryChatMemory = inMemoryChatMemory;
        this.chatHistoryRepository = chatHistoryRepository;
    }

    @PostMapping(value = "/v1/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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
    @PostMapping(value = "/chat",produces="text/html;charset=UTF-8")
    public Flux<String> streamChat1(@RequestParam("prompt") String prompt,@RequestParam("chatId") String chatId) {
        chatHistoryRepository.save("chat",chatId);
        return chatClient.prompt()
                .advisors(a->a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,chatId))
                .user(prompt).stream().content();
    }

}
