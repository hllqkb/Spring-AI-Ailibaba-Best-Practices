package com.springai.springai.tools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import com.springai.springai.service.DataBaseChatMemory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SqlQueryTool  {

    private final DataBaseChatMemory databaseChatMemory;

    public SqlQueryTool(DataBaseChatMemory databaseChatMemory) {
        this.databaseChatMemory = databaseChatMemory;
    }
    @Tool
    public List<Message> conversationQuery(@ToolParam(description = "历史对话的id") String conversationId,
                                           @ToolParam(description = "查询对话的条数") int limit) {
        log.info("调用SqlQueryTool conversationQuery conversationId:{}, limit:{}", conversationId, limit);
        return databaseChatMemory.get(conversationId, limit);
    }
} 