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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ChatConversationServiceImpl extends ServiceImpl<ChatConversationMapper, ChatConversation>
		implements IChatConversationService {
	private final ChatMessageMapper chatMessageMapper;
	private final OriginFileService originFileService;
	@Override
	public ChatConversationVO getConversation(Long conversationId) {
		ChatConversation chatConversation = this.getById(conversationId);
		if(chatConversation == null){
			return null;
		}
		return transferConversations(List.of(chatConversation)).get(0);
	}

	@Override
	public ChatConversationVO updateConversation(ChatConversationVO chatConversationVO) {
		//更新会话
		ChatConversation chatConversation = new ChatConversation();
		BeanUtils.copyProperties(chatConversationVO, chatConversation);
		updateById(chatConversation);
		return chatConversationVO;
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
	public Boolean removeConversation(Long conversationId) {
		//删除会话
		return removeById(conversationId);
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
			queryWrapper.eq(ChatMessage::getConversationId, String.valueOf(conversation.getId())).orderByDesc(ChatMessage::getCreateTime);
			List<ChatMessage> messageList = chatMessageMapper.selectList(queryWrapper);
			//把message转成messageVO
			List<ChatMessageVO> chatMessageVOList = transferMessages(messageList);
			chatConversationVO.setMessages(chatMessageVOList);
			// 检查消息列表是否为空
			if (!chatMessageVOList.isEmpty()) {
				chatConversationVO.setResourceIds(chatMessageVOList.get(0).getResourceIds());
			} else {
				// 如果消息列表为空，设置空的资源ID列表
				chatConversationVO.setResourceIds(new ArrayList<>());
				log.warn("会话ID={}没有关联的消息", conversation.getId());
			}
			return chatConversationVO;
		}).toList();
	}

	private List<ChatMessageVO> transferMessages(List<ChatMessage> messageList) {
		return messageList.stream().map(message -> {
			ChatMessageVO chatmessagevo = new ChatMessageVO();
			//把ChatMessage的属性复制到ChatConversationVO，但不包括resource_ids字段，因为字段名不匹配
			BeanUtils.copyProperties(message, chatmessagevo);
			//直接获取resource_ids列表
			List<String> resourceIds = message.getResource_ids() == null ? new ArrayList<>() : message.getResource_ids();
			// 添加日志记录，检查resourceIds是否为null
			log.info("消息ID={}, resourceIds={}, 消息内容={}", message.getId(), resourceIds, message.getContent());
			// 确保resourceIds不为null
			if (resourceIds != null) {
				List<ResourceVO> resourceVOList = originFileService.resourcesFromIds(resourceIds);
				chatmessagevo.setResources(resourceVOList);
				chatmessagevo.setResourceIds(resourceIds);
			} else {
				// 如果为null，设置为空列表
				chatmessagevo.setResources(new ArrayList<>());
				chatmessagevo.setResourceIds(new ArrayList<>());
				log.warn("消息ID={}的resource_ids为null", message.getId());
			}
			return chatmessagevo;
		}).toList();
	}
}