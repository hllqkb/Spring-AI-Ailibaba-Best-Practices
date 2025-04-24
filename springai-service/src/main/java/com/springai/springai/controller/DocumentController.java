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

/**
 * 知识库文件管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/document")
public class DocumentController {

	private final DocumentEntityService documentEntityService;

	/**
	 * 获取知识库文件列表
	 *
	 * @param documentVO
	 * @return
	 */
	@GetMapping("/list")
	@Cacheable(value = "document", key = "#documentVO.knowledgeBaseId + ':' + #documentVO.fileName + ':' + #documentVO.pageNo + ':' + #documentVO.pageSize")
	public BaseResponse<Page<DocumentVO>> listDocument(DocumentVO documentVO) {
		return ResultUtils.success(documentEntityService.listDocuments(documentVO));
	}

	/**
	 * 删除知识库文件
	 *
	 * @param documentVO
	 * @return
	 */
	@PostMapping("/delete")
	@CacheEvict(value = "document", allEntries = true)
	public BaseResponse<Boolean> deleteKnowledgeFile(@RequestBody DocumentVO documentVO) {
		return ResultUtils.success(documentEntityService.deleteKnowledgeFile(documentVO));
	}
/**
	 * 下载知识库文件
	 *
	 * @param fileId
	 * @param response
	 */
	@GetMapping("/download/{fileId}")
	public void downloadDocument(@PathVariable Long fileId, HttpServletResponse response) {
		documentEntityService.download(fileId, response);
	}
	/**
	 * 根据id查询文档的具体信息
	 */
	@GetMapping("/{id}")
	public BaseResponse<DocumentVO> getDocumentById(@PathVariable String id) {
		return ResultUtils.success(documentEntityService.getDocumentById(id));
	}

}
