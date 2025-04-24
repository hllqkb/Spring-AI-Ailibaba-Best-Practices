package com.springai.springai.service.impl;

import com.springai.springai.service.OriginFileService;
import core.common.CoreCode;
import core.exception.BusinessException;
import core.pojo.PageResult;
import core.pojo.entity.ChatMessage;
import com.springai.springai.mapper.ChatMessageMapper;
import com.springai.springai.service.IChatMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 对话消息 服务实现类
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {
    private final OriginFileService originFileService;

    @Override
    public List<Message> toMessage(List<ChatMessage> chatMessageList) {
        //按照messageNo升序排序
        chatMessageList.sort(Comparator.comparingInt(ChatMessage::getMessageNo));
        return chatMessageList.stream().map(chatMessage -> {
            String content = chatMessage.getContent();
            String role = chatMessage.getRole().toLowerCase();
            Message message = switch (role) {
                //用户输入
                case "user" -> new UserMessage(content, originFileService.formResourceIds((List<String>) chatMessage.getResource_ids()));
                //系统指令如提示词
                case "system" -> new SystemMessage(content);
                //AI生成的回复消息
                case "assistant" ->
                        new AssistantMessage(content, Map.of(), List.of(), originFileService.formResourceIds((List<String>) chatMessage.getResource_ids()));
                default -> throw new BusinessException(CoreCode.SYSTEM_ERROR, "未知消息类型");
            };
            return message;
        }).toList();
    }
}
