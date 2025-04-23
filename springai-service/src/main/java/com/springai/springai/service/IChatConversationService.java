package com.springai.springai.service;

import core.pojo.entity.ChatConversation;
import com.baomidou.mybatisplus.extension.service.IService;
import core.pojo.vo.ChatConversationVO;

import java.util.List;

/**
 * <p>
 * 对话表 服务类
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
public interface IChatConversationService extends IService<ChatConversation> {

    ChatConversationVO createConversation(ChatConversationVO chatConversationVO);

    Boolean removeConversation(ChatConversationVO chatConversationVO);

    List<ChatConversationVO> listConversation();

    ChatConversationVO getConversation(Long conversationId);
}
