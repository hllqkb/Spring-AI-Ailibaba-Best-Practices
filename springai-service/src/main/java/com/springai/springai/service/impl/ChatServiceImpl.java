package com.springai.springai.service.impl;

import com.springai.springai.Constant.StringConstant;
import com.springai.springai.model.enums.ChatType;
import com.springai.springai.service.ChatService;
import com.springai.springai.service.DataBaseChatMemory;
import com.springai.springai.service.LLMService;
import com.springai.springai.service.OriginFileService;
import core.pojo.vo.ChatMessageVO;
import core.pojo.vo.ChatRequestVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.content.Media;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;

import static com.springai.springai.Constant.StringConstant.CHAT_MEDIAS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final LLMService llmService;
    private final OriginFileService originFileService;
    private final DataBaseChatMemory databaseChatMemory;
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
            case SIMPLE_RAG -> this.simpleRAGChat(chatMessageVO, chatRequestVO.getKnowledgeIds());
            case MULTIMODEL -> this.multimodalChat(chatMessageVO);
            case MULTIMODEL_RAG -> this.multimodalRAGChat(chatMessageVO, chatRequestVO.getKnowledgeIds());
            default -> Flux.error(new IllegalArgumentException("未知对话类型: " + chatType));
        };

    }

    /**
     * 简单对话
     * @param chatMessageVO
     * @return
     */
    public Flux<ChatResponse> simpleChat(ChatMessageVO chatMessageVO) {
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

    /**
     * 多模态对话
     * @param chatMessageVO
     * @return
     */
    @Override
    public Flux<ChatResponse> multimodalChat(ChatMessageVO chatMessageVO) {
        ChatModel chatModel = llmService.getMultimodalModel();
        List<String> resourceIds = chatMessageVO.getResourceIds();
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        Flux<ChatResponse> chatResponseFlux = chatClient.prompt().user(user -> {
            HashMap<String, Object> params = new HashMap<>();
            params.put(CHAT_MEDIAS, resourceIds);
            params.put(StringConstant.CHAT_CONSERVATION_NAME, chatMessageVO.getConversationId());
            user.text(chatMessageVO.getContent());
            user.params(params);
            log.info("params: {}", params);
            if(resourceIds!=null&& !resourceIds.isEmpty()){
                List<Media> medias = originFileService.formResourceIds(resourceIds);
                user.media(medias.toArray(new Media[0]));
            }
        }).advisors(MessageChatMemoryAdvisor.builder(databaseChatMemory).chatMemoryRetrieveSize(
                StringConstant.CHAT_MAX_LENGTH
        ).conversationId(chatMessageVO.getConversationId()).build()).stream().chatResponse();
        return chatResponseFlux;
    }

    /**
     * 简单RAG对话
     * @param chatMessageVO
     * @param baseIds
     * @return
     * @throws Exception
     */
    @Override
    public Flux<ChatResponse> simpleRAGChat(ChatMessageVO chatMessageVO, List<String> baseIds) {
//               return chatResponseFlux;
        return null;
    }

    /**
     * 多模态RAG对话
     * @param chatMessageVO
     * @param baseIds
     * @return
     * @throws Exception
     */
    @Override
    public Flux<ChatResponse> multimodalRAGChat(ChatMessageVO chatMessageVO, List<String> baseIds) {
        return null;    }
}
