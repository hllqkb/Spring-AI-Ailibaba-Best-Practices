package com.springai.springai.controller;

import com.springai.springai.service.KnowledgeBaseService;
import core.common.BaseResponse;
import core.common.ResultUtils;
import core.pojo.vo.KnowLedgeBaseVO;
import core.pojo.vo.SimpleBaseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
*知识库管理
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/knowledge")
public class KnowledgeBaseController {
    private final KnowledgeBaseService knowledgeBaseService;
    /**
     * 创建知识库
     *
     * @param knowledgeBaseVO
     * @return
     */
    @PostMapping("/create")
    @CacheEvict(value = "knowledge", allEntries = true)
    public BaseResponse<Long> add(@RequestBody KnowLedgeBaseVO knowledgeBaseVO) {
        return ResultUtils.success(knowledgeBaseService.create(knowledgeBaseVO));
    }
    /**
     * 更新知识库
     *
     * @param knowledgeBaseVO
     * @return
     */
    @PutMapping("/update")
    @CacheEvict(value = "knowledge", allEntries = true)
    public BaseResponse<Long> update(@RequestBody KnowLedgeBaseVO knowledgeBaseVO) {
        return ResultUtils.success(knowledgeBaseService.update(knowledgeBaseVO));
    }
    /**
     * 删除知识库
     * @param knowledgeBaseVO
     * @return
     */
    @DeleteMapping("/remove")
    @CacheEvict(value = "knowledge", allEntries = true)
    public BaseResponse<Integer> delete(@RequestBody KnowLedgeBaseVO knowledgeBaseVO) {
        return ResultUtils.success(knowledgeBaseService.delete(knowledgeBaseVO));
    }
    /**
     * 获取知识库列表
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "knowledge", key = "'list'")
    public BaseResponse<List<KnowLedgeBaseVO>> listKnowledgeBases() {
        return ResultUtils.success(knowledgeBaseService.listKnowledgeBases());
    }
    /**
     * 获取知识库详情
     * @return
     */

    @GetMapping("/simple")
    @Cacheable(value = "knowledge", key = "'simple'")
    public BaseResponse<List<SimpleBaseVO>> simpleList() {
        return ResultUtils.success(knowledgeBaseService.getSimple());
    }
}
