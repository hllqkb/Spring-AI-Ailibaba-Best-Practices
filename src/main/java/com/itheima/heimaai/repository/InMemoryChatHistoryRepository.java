package com.itheima.heimaai.repository;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryChatHistoryRepository implements ChatHistoryRepository {
    private final Map<String, List<String> > chatHistory=new HashMap<>();
    @Override
    public void save(String type, String chatId) {
        List<String> chatHistoryList=chatHistory.computeIfAbsent(type, k -> new ArrayList<>());
       if(chatHistoryList.contains(chatId)) {
           return;
       }
       chatHistoryList.add(chatId);
    }
    @Override
    public List<String> getMessage(String type) {
        return chatHistory.getOrDefault(type, List.of());
    }
}
