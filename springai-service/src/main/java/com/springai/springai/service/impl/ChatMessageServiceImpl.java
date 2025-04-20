package com.springai.springai.service.impl;

import core.pojo.entity.ChatMessage;
import com.springai.springai.mapper.ChatMessageMapper;
import com.springai.springai.service.IChatMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 对话消息 服务实现类
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {

}
