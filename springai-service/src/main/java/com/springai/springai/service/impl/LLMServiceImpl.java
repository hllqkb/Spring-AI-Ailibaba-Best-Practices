package com.springai.springai.service.impl;

import com.springai.springai.service.LLMService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LLMServiceImpl implements LLMService {
    /**
     * 简单对话的API基础URL
     */
    @Value("${chat.simple.base-url}")
    private String simpleBaseUrl;

    /**
     * 简单对话的API密钥
     */
    @Value("${chat.simple.api-key}")
    private String simpleApiKey;

    /**
     * 简单对话使用的模型名称
     */
    @Value("${chat.simple.model}")
    private String simpleModel;

    /**
     * 长对话的API基础URL
     */
    @Value("${chat.long.base-url}")
    private String longBaseUrl;

    /**
     * 长对话的API密钥
     */
    @Value("${chat.long.api-key}")
    private String longApiKey;

    /**
     * 长对话使用的模型名称
     */
    @Value("${chat.long.model}")
    private String longModel;

    /**
     * 多模态对话的API基础URL
     */
    @Value("${chat.multimodal.base-url}")
    private String multimodalBaseUrl;

    /**
     * 多模态对话的API密钥
     */
    @Value("${chat.multimodal.api-key}")
    private String multimodalApiKey;

    /**
     * 多模态对话使用的模型名称
     */
    @Value("${chat.multimodal.model}")
    private String multimodalModel;

    /**
     * 向量嵌入的API基础URL
     */
    @Value("${embedding.base-url}")
    private String embeddingBaseUrl;

    /**
     * 向量嵌入的API密钥
     */
    @Value("${embedding.api-key}")
    private String embeddingApiKey;

    /**
     * 向量嵌入使用的模型名称
     */
    @Value("${embedding.model}")
    private String embeddingModel;

    public ChatModel getModel(String baseUrl,String key,String baseModel){
        OpenAiApi openAiApi = OpenAiApi.builder()
               .baseUrl(baseUrl)
               .apiKey(key)
               .build();
        return OpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(OpenAiChatOptions.builder().
                model(baseModel).build()).build();
    }

    @Override
    public ChatModel getChatModel() {
        return getModel(simpleBaseUrl,simpleApiKey,simpleModel);
    }

    @Override
    public ChatModel getLongContextModel() {
        return null;
    }

    @Override
    public ChatModel getMultimodalModel() {
        return null;
    }

    @Override
    public ChatModel getVectorModel() {
        return null;
    }

    @Override
    public VectorStore getVectorStorage() {
        return null;
    }

    @Override
    public VectorStore getVectorStore() {
        return null;
    }

}
