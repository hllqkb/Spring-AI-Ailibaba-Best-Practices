package com.springai.springai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.springai.springai.Constant.StringConstant;
import com.springai.springai.mapper.ChatMessageMapper;
import core.pojo.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.springai.springai.Constant.StringConstant.CHAT_MEDIAS;
import static org.springframework.ai.chat.messages.AbstractMessage.MESSAGE_TYPE;

/**
 * @author hllqk
 * 添加message到数据库
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DataBaseChatMemory implements ChatMemory {
    private final ChatMessageMapper chatMessageMapper;
    private final IChatMessageService chatMessageService;
    //资源缓存Map
    private Map<String, List<String>> conversationResourceMap = new ConcurrentHashMap<>();

    //任意情况的事务回滚
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(String conversationId, List<Message> messages) {
        log.info("messages:{}", messages);
        log.info("params:{}", messages.get(0).getMetadata());
        //添加消息到数据库
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        LambdaQueryWrapper<ChatMessage> qw = new LambdaQueryWrapper<>();
        qw.eq(ChatMessage::getIsClean, false).eq(ChatMessage::getConversationId, conversationId);
        long cnt = chatMessageMapper.selectCount(qw);
        List<ChatMessage> chatMessageList = new ArrayList<>(messages.size());
        //遍历消息，添加到ChatMessage对象中
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            ChatMessage chatMessage = new ChatMessage();
            //自增
//            chatMessage.setId(UUID.randomUUID().toString());
            //设置消息内容
            chatMessage.setContent(message.getText());
            //设置对话编号
            chatMessage.setConversationId(conversationId);
            //设置消息编号
            chatMessage.setMessageNo((int) (cnt + i + 1));
            //设置角色

            chatMessage.setRole(message.getMetadata().get(MESSAGE_TYPE).toString());
            //开始添加资源
            //从metadata中获取CHAT_MEDIAS参数
            List<String> resourceList = conversationResourceMap.getOrDefault(conversationId, List.of());
            
            if (resourceList != null && !resourceList.isEmpty()) {
                chatMessage.setHasMedia(true);
                chatMessage.setResourceIds(resourceList);
            } else {
                chatMessage.setHasMedia(false);
                chatMessage.setResourceIds(List.of());
            }
            chatMessageList.add(chatMessage);
        }
        //批量插入
        chatMessageMapper.insert(chatMessageList);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        //从数据库中获取消息
        LambdaQueryWrapper<ChatMessage> qw = new LambdaQueryWrapper<>();
        //原子性查询
        qw.eq(ChatMessage::getConversationId, conversationId)
                .eq(ChatMessage::getIsClean, false).orderByAsc(ChatMessage::getCreateTime)
                .last("limit " + lastN);
        List<ChatMessage> chatMessageList = chatMessageMapper.selectList(qw);
        return chatMessageList.isEmpty() ? List.of() : chatMessageService.toMessage(chatMessageList);
    }

    @Override
    public void clear(String conversationId) {

    }

    public void setConversationResources(String conversationId, List<String> resourceIds) {
        if (resourceIds != null && !resourceIds.isEmpty()) {
            conversationResourceMap.put(conversationId, resourceIds);
        }
    }
}
