package com.springai.springai.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;

public interface LLMService {
    //获取聊天模型
    ChatModel getChatModel();
    //获取长上下文模型
    ChatModel getLongContextModel();
    //获取多模态模型
    ChatModel getMultimodalModel();
    //获取向量化模型
    ChatModel getVectorModel();
    //获取向量储存对象
    VectorStore getVectorStorage();
}
