package com.itheima.heimaai.repository;

import java.util.List;

public interface ChatHistoryRepository {
    /**
     * 保存聊天记录
     * @param type 聊天类型
     * @param chatId 聊天ID
     */
    void save(String type,String chatId);
    /**
     * 获取聊天记录
     * @param type 聊天类型
     * @return 聊天记录
     */
    List<String> getMessage(String type);
}
