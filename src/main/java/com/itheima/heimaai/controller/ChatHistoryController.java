package com.itheima.heimaai.controller;

import com.itheima.heimaai.entity.vo.MessageVo;
import com.itheima.heimaai.repository.ChatHistoryRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {
    private final ChatHistoryRepository chatHistoryRepository;
    private final ChatMemory chatMemory;

    public ChatHistoryController(ChatHistoryRepository chatHistoryRepository, ChatMemory chatMemory) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.chatMemory = chatMemory;
    }
    @GetMapping("/{type}")
    public List<String> getHistory(@PathVariable("type") String type) {
        return chatHistoryRepository.getMessage(type);
    }
    @GetMapping("/{type}/{chatId}")
    public List<MessageVo> getHistoryById(@PathVariable("type") String type, @PathVariable("chatId") String chatId) {
        List<Message> messages=chatMemory.get(chatId, Integer.MAX_VALUE);
        if(messages==null){
            return List.of();
        }
        return messages.stream().map(MessageVo::new).toList();
    }
}
