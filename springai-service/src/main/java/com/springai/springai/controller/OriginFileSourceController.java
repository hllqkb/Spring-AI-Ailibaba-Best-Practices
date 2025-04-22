package com.springai.springai.controller;

import com.springai.springai.service.OriginFileService;
import core.common.BaseResponse;
import core.common.ResultUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 存储聊天文件
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class OriginFileSourceController {
    private final OriginFileService originFileService;
    /**
     * 上传聊天文件
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping(value = "/chat",headers = "Content-Type=multipart/form-data")
    public BaseResponse<String> uploadChatFile(MultipartFile file) {
        return ResultUtils.success(originFileService.uploadFile(file));
    }

    /**
     * 上传知识文件
     * @param file
     * @param knowledgeId
     * @return
     */
    @PostMapping(value="/knowledge/{knowledgeId}")
    public BaseResponse<Long> uploadKnowledgeFile(MultipartFile file, @PathVariable("knowledgeId") String knowledgeId) {
        return ResultUtils.success(originFileService.uploadFile(file, knowledgeId));
    }
}
