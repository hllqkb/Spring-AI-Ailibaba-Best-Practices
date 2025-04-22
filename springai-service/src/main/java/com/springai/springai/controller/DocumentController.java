package com.springai.springai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springai.springai.service.DocumentEntityService;
import core.common.BaseResponse;
import core.common.ResultUtils;
import core.pojo.vo.DocumentVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/document")
public class DocumentController {

	private final DocumentEntityService documentEntityService;

	@GetMapping("/list")
	@Cacheable(value = "document", key = "#documentVO.knowledgeBaseId + ':' + #documentVO.fileName + ':' + #documentVO.pageNo + ':' + #documentVO.pageSize")
	public BaseResponse<Page<DocumentVO>> listDocument(DocumentVO documentVO) {
		return ResultUtils.success(documentEntityService.listDocuments(documentVO));
	}

	@PostMapping("/delete")
	@CacheEvict(value = "document", allEntries = true)
	public BaseResponse<Boolean> deleteKnowledgeFile(@RequestBody DocumentVO documentVO) {
		return ResultUtils.success(documentEntityService.deleteKnowledgeFile(documentVO));
	}

	@GetMapping("/download/{fileId}")
	public void downloadDocument(@PathVariable Long fileId, HttpServletResponse response) {
		documentEntityService.download(fileId, response);
	}

}
