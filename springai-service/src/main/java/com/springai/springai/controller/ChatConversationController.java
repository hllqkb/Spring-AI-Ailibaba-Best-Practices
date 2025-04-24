package com.springai.springai.controller;

import com.springai.springai.service.ChatService;
import com.springai.springai.service.IChatConversationService;
import core.common.BaseResponse;
import core.common.ResultUtils;
import core.pojo.vo.ChatConversationVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对话轮数
 * @author hllqkb
 * @since 2025-04-20
 */
@RestController
@RequestMapping("/conversation")
@Slf4j
@ApiModel(value = "聊天对话服务", description = "聊天对话服务")
@RequiredArgsConstructor
public class ChatConversationController {
    private final IChatConversationService chatConversationService;

    /**
     * 获取对话详情
     *
     * @param conversationId 对话id
     * @return 对话详情
     */
//    @Cacheable(value = "conversationDetail",key = "#conversationId")
    @ApiModelProperty(value = "获取对话详情")
    @GetMapping("/detail")
    public BaseResponse<ChatConversationVO> detail(@RequestParam("id") Long conversationId) {
        ChatConversationVO conversation = chatConversationService.getConversation(conversationId);
        if(conversation == null){
            return ResultUtils.error(404,"对话不存在");
        }
        return ResultUtils.success(conversation);
    }
    /**
     * 创建对话
     * @param chatConversationVO 对话详情
     *
     * */
    @CacheEvict(value = "conversationList", allEntries = true)
    @ApiModelProperty(value = "创建对话")
    @PostMapping("/create")
    public BaseResponse<ChatConversationVO> createConversation(@RequestBody ChatConversationVO chatConversationVO) {
        return ResultUtils.success(chatConversationService.createConversation(chatConversationVO));
    }
    /**
     * 删除对话
     *
     *
     * */
    @CacheEvict(value = "conversationList", allEntries = true)
    @ApiModelProperty(value = "删除对话")
    @PostMapping("/remove")
    public BaseResponse<Boolean> removeConversation(@RequestBody ChatConversationVO chatConversationVO) {
        return ResultUtils.success(chatConversationService.removeConversation(chatConversationVO.getId()));
    }

    /**
     * 获取对话列表
     *
     * @return
     */
    @Cacheable(value = "conversationList", key = "'allConversations'")
    @ApiModelProperty(value = "获取对话列表")
    @GetMapping("/list")
    public BaseResponse<List<ChatConversationVO>> listConversation() {
        return ResultUtils.success(chatConversationService.listConversation());
    }
    /**
     * 更新对话
     * @param chatConversationVO 对话详情
     */
    @Cacheable(value = "conversationDetail",key = "#chatConversationVO.getId()")
    @CacheEvict(value = "conversationList", allEntries = true)
    @ApiModelProperty(value = "更新对话")
    @PostMapping("/update")
    public BaseResponse<ChatConversationVO> updateConversation(@RequestBody ChatConversationVO chatConversationVO) {
        return ResultUtils.success(chatConversationService.updateConversation(chatConversationVO));
    }
}
