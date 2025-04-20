package com.springai.springai.service.impl;

import core.pojo.entity.ChatConversation;
import com.springai.springai.mapper.ChatConversationMapper;
import com.springai.springai.service.IChatConversationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 对话表 服务实现类
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Service
public class ChatConversationServiceImpl extends ServiceImpl<ChatConversationMapper, ChatConversation>
		implements IChatConversationService {

}
