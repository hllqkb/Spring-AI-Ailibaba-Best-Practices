package com.springai.springai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springai.springai.service.DocumentEntityService;

import core.common.BaseResponse;
import core.common.ResultUtils;
import core.pojo.vo.DocumentVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 知识库文件管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/document")
public class DocumentController {

	private final DocumentEntityService documentEntityService;
	private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

	/**
	 * 获取知识库文件列表
	 *
	 * @param documentVO
	 * @return
	 */
	@GetMapping("/list")
//	@Cacheable(value = "document", key = "#documentVO.knowledgeBaseId + ':' + #documentVO.fileName + ':' + #documentVO.pageNo + ':' + #documentVO.pageSize")
	public BaseResponse<Page<DocumentVO>> listDocument(DocumentVO documentVO) {
		log.info("Request arrived at listDocument controller with documentVO: {}", documentVO);
		// 将 knowledgeBaseId 的值赋给 baseId，确保 service 层能正确获取到知识库ID
		if (documentVO.getKnowledgeBaseId() != null) {
			documentVO.setBaseId(documentVO.getKnowledgeBaseId());
		}
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


}
