package com.springai.springai.controller;

import com.springai.springai.service.KnowledgeBaseService;
import core.common.BaseResponse;
import core.common.ResultUtils;
import core.pojo.vo.KnowLedgeBaseVO;
import core.pojo.vo.SimpleBaseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/knowledge")
public class KnowledgeBaseController {
    private final KnowledgeBaseService knowledgeBaseService;
    @PostMapping("/create")
    public BaseResponse<String> add(@RequestBody KnowLedgeBaseVO knowledgeBaseVO) {
        return ResultUtils.success(knowledgeBaseService.create(knowledgeBaseVO));
    }
    @PutMapping("/update")
    public BaseResponse<String> update(@RequestBody KnowLedgeBaseVO knowledgeBaseVO) {
        return ResultUtils.success(knowledgeBaseService.update(knowledgeBaseVO));
    }
    @DeleteMapping("/remove")
    public BaseResponse<Integer> delete(@RequestBody KnowLedgeBaseVO knowledgeBaseVO) {
        return ResultUtils.success(knowledgeBaseService.delete(knowledgeBaseVO));
    }
    @GetMapping("/list")
    public BaseResponse<List<KnowLedgeBaseVO>> listKnowledgeBases() {
        return ResultUtils.success(knowledgeBaseService.listKnowledgeBases());
    }

    @GetMapping("/simple")
    public BaseResponse<List<SimpleBaseVO>> simpleList() {
        return ResultUtils.success(knowledgeBaseService.getSimple());
    }
}
