package com.springai.springai.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.vectorstore.VectorStore;


public interface LLMService {

    /**
     * 获取对话模型
     * @return
     */
    ChatModel getChatModel();

    /**
     * 获取超长上下文对话模型
     * @return
     */
    ChatModel getLongContextChatModel();

    /**
     * 获取多模态对话模型
     * @return
     */
    ChatModel getMultimodalModel();

    /**
     * 向量化模型
     * @return
     */
    EmbeddingModel getEmbeddingModel();

    /**
     * 获取向量存储对象
     * @return
     */
    VectorStore getVectorStore();

}
