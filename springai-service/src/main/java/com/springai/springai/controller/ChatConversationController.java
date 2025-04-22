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
    @ApiModelProperty(value = "获取对话详情")
    @GetMapping("/detail")
    public BaseResponse<ChatConversationVO> detail(@RequestParam("id") String conversationId) {
        return ResultUtils.success(chatConversationService.getConversation(conversationId));
    }
    /**
     * 创建对话
     * @param chatConversationVO 对话详情
     *
     * */
    @ApiModelProperty(value = "创建对话")
    @PostMapping("/create")
    public BaseResponse<ChatConversationVO> createConversation(@RequestBody ChatConversationVO chatConversationVO) {
        return ResultUtils.success(chatConversationService.createConversation(chatConversationVO));
    }
    /**
     * 删除对话
     * @param chatConversationVO 对话详情
     *
     * */
    @ApiModelProperty(value = "删除对话")
    @DeleteMapping("/remove")
    public BaseResponse<Boolean> removeConversation(@RequestBody ChatConversationVO chatConversationVO) {
        return ResultUtils.success(chatConversationService.removeConversation(chatConversationVO));
    }

    /**
     * 获取对话列表
     *
     * @return
     */
    @ApiModelProperty(value = "获取对话列表")
    @PostMapping("/list")
    public BaseResponse<List<ChatConversationVO>> listConversation() {
        return ResultUtils.success(chatConversationService.listConversation());
    }
}
