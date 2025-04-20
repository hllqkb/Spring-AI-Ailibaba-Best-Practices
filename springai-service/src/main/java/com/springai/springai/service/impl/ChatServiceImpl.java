package com.springai.springai.service.impl;

import com.springai.springai.Constant.StringConstant;
import com.springai.springai.model.enums.ChatType;
import com.springai.springai.service.ChatService;
import com.springai.springai.service.LLMService;
import core.pojo.vo.ChatMessageVO;
import core.pojo.vo.ChatRequestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final LLMService llmService;
    @Override
    public Flux<ChatResponse> unifyChat(ChatRequestVO chatRequestVO) {
        //获取前端的请求聊天类型
        String chatType = chatRequestVO.getChatType();
        //创建一个消息
        ChatMessageVO chatMessageVO = new ChatMessageVO();
        //把ChatMessageVO的内容拷贝到chatRequestVO中
        BeanUtils.copyProperties(chatRequestVO, chatMessageVO);
        //根据聊天类型，调用不同的聊天服务
        //获取枚举类型
        ChatType type = ChatType.parse(chatType);
        return switch (type) {
            case SIMPLE -> this.simpleChat(chatMessageVO);
            case SIMPLE_RAG -> this.simpleRagChat(chatMessageVO);
            case MULTIMODEL -> this.multimodelChat(chatMessageVO);
            case MULTIMODEL_RAG -> this.multimodelRagChat(chatMessageVO);
            default -> Flux.error(new IllegalArgumentException("未知对话类型: " + chatType));
        };

    }

    private Flux<ChatResponse> multimodelRagChat(ChatMessageVO chatMessageVO) {
        return null;
    }

    private Flux<ChatResponse> multimodelChat(ChatMessageVO chatMessageVO) {
        return null;
    }

    private Flux<ChatResponse> simpleRagChat(ChatMessageVO chatMessageVO) {
        return null;
    }
    //简单聊天
    private Flux<ChatResponse> simpleChat(ChatMessageVO chatMessageVO) {
        ChatModel chatModel = llmService.getChatModel();
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        Flux<ChatResponse> chatResponseFlux = chatClient.prompt().user(user -> {
            user.param(StringConstant.CHAT_CONSERVATION_NAME, chatMessageVO.getConversationId());
            user.text(chatMessageVO.getContent());
        }).advisors(MessageChatMemoryAdvisor.builder(databaseChatMemory).chatMemoryRetrieveSize(
                StringConstant.CHAT_MAX_LENGTH
        ).conversationId(chatMessageVO.getConversationId()).build()).stream().chatResponse();
        return chatResponseFlux;
    }
}
