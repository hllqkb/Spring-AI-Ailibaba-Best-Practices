package com.itheima.heimaai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // 文本分割器
    @Bean
    public DocumentTransformer documentTransformer() {
        return new TokenTextSplitter();
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("你现在不是ChatGPT，我希望你是以一个全栈开发程序员来和我对话，你的名字叫做neko")
                .build();
    }

}