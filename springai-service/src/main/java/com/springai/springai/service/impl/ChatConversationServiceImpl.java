package com.springai.springai.service.impl;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springai.springai.mapper.ChatConversationMapper;
import com.springai.springai.mapper.ChatMessageMapper;
import com.springai.springai.service.IChatConversationService;
import com.springai.springai.service.OriginFileService;
import core.pojo.entity.ChatConversation;
import core.pojo.entity.ChatMessage;
import core.pojo.vo.ChatConversationVO;
import core.pojo.vo.ChatMessageVO;
import core.pojo.vo.ResourceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 对话表 服务实现类
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@RequiredArgsConstructor
@Service
public class ChatConversationServiceImpl extends ServiceImpl<ChatConversationMapper, ChatConversation>
		implements IChatConversationService {
	private final ChatMessageMapper chatMessageMapper;
	private final OriginFileService originFileService;
	@Override
	public ChatConversationVO getConversation(String conversationId) {
		ChatConversation chatConversation = this.getById(conversationId);
		return transferConversations(List.of(chatConversation)).get(0);
	}
	@Transactional(rollbackFor = Exception.class)
	@Override
	public ChatConversationVO createConversation(ChatConversationVO chatConversationVO) {
		//创建会话
		String title = chatConversationVO.getTitle();
		if (title.length() > 16) {
			title = title.substring(0, 16);
		}
		ChatConversation chatConversation = new ChatConversation();
		Long userId = StpUtil.getLoginIdAsLong();
		chatConversation.setTitle(title);
		chatConversation.setUserId(userId);
		saveOrUpdate(chatConversation);
		chatConversationVO.setId(chatConversation.getId());
		chatConversationVO.setTitle(title);
		chatConversationVO.setCreateTime(chatConversation.getCreateTime());
		chatConversationVO.setMessages(new ArrayList<>());
		return chatConversationVO;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Boolean removeConversation(ChatConversationVO chatConversationVO) {
		//删除会话
		return removeById(chatConversationVO.getId());
	}

	@Override
	public List<ChatConversationVO> listConversation() {
		Long userId = StpUtil.getLoginIdAsLong();
		//获取会话列表，获取用户的最近三十轮对话
		List<ChatConversation> conversationList = query().eq("user_id", userId).last("LIMIT 30").list();
		//用Stream流将ChatConversation转换为ChatConversationVO
		return transferConversations(conversationList);
	}

	private List<ChatConversationVO> transferConversations(List<ChatConversation> conversationList) {
	return conversationList.stream().map(conversation -> {
		ChatConversationVO chatConversationVO = new ChatConversationVO();
		//把ChatConversation的属性复制到ChatConversationVO
		BeanUtils.copyProperties(conversation, chatConversationVO);
		//查询消息表关联的会话id的全部消息，按照创建时间排序
		LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
		//根据会话id获取messages和根据创建时间排序
		queryWrapper.eq(ChatMessage::getConversationId, conversation.getId()).orderByDesc(ChatMessage::getCreateTime);
		List<ChatMessage> messageList = chatMessageMapper.selectList(queryWrapper);
			//把message转成messageVO
		List<ChatMessageVO> chatMessageVOList = transferMessages(messageList);
		chatConversationVO.setMessages(chatMessageVOList);
		return chatConversationVO;
	}).toList();
	}

	private List<ChatMessageVO> transferMessages(List<ChatMessage> messageList) {
		return messageList.stream().map(message -> {
			ChatMessageVO chatmessagevo = new ChatMessageVO();
			//把ChatMessage的属性复制到ChatConversationVO
			BeanUtils.copyProperties(message, chatmessagevo);
			//把List resourceIds转成List ResourceVO
			List<String> resourceIds = message.getResourceIds();
			List<ResourceVO> resourceVOList = originFileService.resourcesFromIds(resourceIds);
			chatmessagevo.setResources(resourceVOList);
			return chatmessagevo;
		}).toList();
}
}