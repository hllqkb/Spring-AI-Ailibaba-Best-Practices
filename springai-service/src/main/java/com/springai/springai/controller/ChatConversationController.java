package com.springai.springai.controller;

import com.springai.springai.service.ChatService;
import core.common.BaseResponse;
import core.pojo.vo.ChatConversationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 对话表 前端控制器
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@RestController
@RequestMapping("/conversation")
@Slf4j
@RequiredArgsConstructor
public class ChatConversationController {
    private final ChatService chatService;
    @PostMapping("/create")
    public BaseResponse<ChatConversationVO> createConversation(String userId, String userName, String userAvatar) {
        ChatConservationVO chatConservationVO = chatService.createConversation(userId, userName, userAvatar);
        return BaseResponse.success(chatConservationVO);
    }
}
