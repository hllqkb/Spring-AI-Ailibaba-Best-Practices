package com.springai.springai.service;

import core.pojo.entity.ChatMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * <p>
 * 对话消息 服务类
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
public interface IChatMessageService extends IService<ChatMessage> {

    List<Message> toMessage(List<ChatMessage> chatMessageList);
}
