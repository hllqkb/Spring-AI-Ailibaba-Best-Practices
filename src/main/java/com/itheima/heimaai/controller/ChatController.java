package com.itheima.heimaai.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Resource
    private ChatClient chatClient;

    //chatModel所有模型通用，但是缺少模型特性
    @Resource
    private ChatModel chatModel;

    /**
     * 单轮对话
     *
     * @param prompt 用户输入的消息
     * @return 机器人回复的消息
     */
    @GetMapping("/chat")
    public Object chat(String prompt) {
        String output = chatClient.prompt().user(prompt).call().content();
        return output;
    }

    /**
     * 流式api（推荐）
     * 异步流式对话
     *
     * @param prompt
     * @return
     */
    @GetMapping(value = "/stream/chat", produces = "text/html;charset=utf-8")
    public Flux<String> streamChat(String prompt) {
        Flux<String> output = chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
        return output;
    }
    @GetMapping("/demo")
    public String demo(String prompt){

        String response = chatClient.prompt(prompt).call().content();

        return response;
    }
    /**
     *  对话(温度0.4)
     * @param prompt
     * @return
     */
}