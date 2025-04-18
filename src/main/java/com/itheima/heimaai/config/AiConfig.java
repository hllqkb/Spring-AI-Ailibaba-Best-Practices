package com.itheima.heimaai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AiConfig {


    @Bean
    ChatClient chatClient(ChatClient.Builder builder,ChatMemory chatMemory) {
        return builder.defaultSystem("你是一个智能机器人,你的名字叫 Spring AI智能机器人")
                .defaultAdvisors(new SimpleLoggerAdvisor()
                , new MessageChatMemoryAdvisor(chatMemory))
                .build();

    }
    @Bean
    InMemoryChatMemory inMemoryChatMemory() {
        return new InMemoryChatMemory();
    }
}
