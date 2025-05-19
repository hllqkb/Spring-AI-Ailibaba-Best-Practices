package com.springai.springai.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.springai.springai.service.OriginFileService;

import core.common.BaseResponse;
import core.common.ResultUtils;
import core.pojo.vo.ResourceVO;
import lombok.RequiredArgsConstructor;

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
     * 根据ids获取资源
     * @param ids id列表
     * @return 资源列表
     */
    @PostMapping(value = "/get")
    public BaseResponse<List<ResourceVO>> resourcesFromIds(@RequestBody List<String> ids) {
        return ResultUtils.success(originFileService.resourcesFromIds(ids));
    }
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
     *
     * @param file
     * @param knowledgeId
     * @return
     */
    @PostMapping(value="/knowledge/{knowledgeId}")
    public BaseResponse<Long> uploadKnowledgeFile(MultipartFile file, @PathVariable("knowledgeId") Long knowledgeId) {
        return ResultUtils.success(originFileService.uploadFile(file, knowledgeId));
    }
}
