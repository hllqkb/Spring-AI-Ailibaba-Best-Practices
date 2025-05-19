package com.springai.springai.service.impl;


import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springai.springai.mapper.DocumentEntityMapper;
import com.springai.springai.mapper.KnowledgeBaseMapper;
import com.springai.springai.mapper.OriginFileSourceMapper;
import com.springai.springai.service.DocumentEntityService;
import com.springai.springai.service.LLMService;

import core.common.CoreCode;
import core.exception.BusinessException;
import core.pojo.entity.DocumentEntity;
import core.pojo.entity.KnowledgeBase;
import core.pojo.entity.OriginFileSource;
import core.pojo.vo.DocumentVO;
import core.service.objectstore.ObjectStoreService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentEntityServiceImpl implements DocumentEntityService {

	private final DocumentEntityMapper documentEntityMapper;

	private final OriginFileSourceMapper originFileResourceMapper;

	private final KnowledgeBaseMapper knowledgeBaseMapper;

	private final ObjectStoreService objectStoreService;

	private final LLMService llmService;

	private final ObjectStoreServiceImpl minIOService;

	@Override
	public Page<DocumentVO> listDocuments(DocumentVO document) {
		log.info("Received document query: {}", document);
		if (document.getBaseId() == null) {
			throw new BusinessException(CoreCode.PARAMS_ERROR);
		}
		LambdaQueryWrapper<DocumentEntity> qw = new LambdaQueryWrapper<>();
		if (document.getFileName() != null && !document.getFileName().isEmpty()) {
			qw.like(DocumentEntity::getFileName, document.getFileName());
		}
		qw.eq(DocumentEntity::getBaseId, document.getBaseId());
		qw.orderByDesc(DocumentEntity::getCreateTime);
		log.info("QueryWrapper: {}", qw.getExpression());
		Page<DocumentEntity> page = Page.of(document.getPageNo(), document.getPageSize());
		Page<DocumentEntity> documentPage = documentEntityMapper.selectPage(page, qw);
		log.info("Query result count: {}", documentPage.getTotal());
		List<DocumentVO> vos = transfer(documentPage.getRecords());
		Page<DocumentVO> res = new Page<>();
		BeanUtils.copyProperties(documentPage, res);
		res.setRecords(vos);
		return res;
	}

	@Override
	public Boolean deleteKnowledgeFile(DocumentVO documentVO) {
		Long fileId = documentVO.getId();
		Long baseId = documentVO.getBaseId();

		QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("base_id", baseId);
		queryWrapper.eq("id", fileId);

		DocumentEntity document = documentEntityMapper.selectOne(queryWrapper);

		try {
			// 删除文件
			documentEntityMapper.deleteById(document);
			// 删除向量数据
			VectorStore vectorStore = llmService.getVectorStore();

			Filter.Expression filterExpression = new FilterExpressionBuilder().eq("document_id", fileId.toString()).build();

			vectorStore.delete(filterExpression);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(CoreCode.SYSTEM_ERROR, e.getMessage());
		}
	}

	@Override
	public void download(Long fileId, HttpServletResponse response) {
		DocumentEntity document = documentEntityMapper.selectById(fileId);
		if (document == null) {
			throw new BusinessException(CoreCode.FILE_NOT_FOUND);
		}
		OriginFileSource originFileResource = originFileResourceMapper.selectById(document.getResourceId());
		if (originFileResource == null) {
			throw new BusinessException(CoreCode.FILE_NOT_FOUND);
		}
		InputStream file = minIOService.getFile(originFileResource.getBucketName(), originFileResource.getObjectName());

		// 设置响应头
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ URLEncoder.encode(originFileResource.getFileName(), StandardCharsets.UTF_8) + "\"");

		try (ServletOutputStream out = response.getOutputStream()) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = file.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			out.flush();
		}
		catch (IOException e) {
			throw new RuntimeException("文件下载失败", e);
		}
	}

	private List<DocumentVO> transfer(List<DocumentEntity> documentEntities) {
		return documentEntities.stream().map(item -> {
			String resourceId = item.getResourceId();
			OriginFileSource originFileResource = originFileResourceMapper.selectById(resourceId);
			String path = objectStoreService.getTmpFileUrl(originFileResource.getBucketName(),
					originFileResource.getObjectName());
			KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(item.getBaseId());
			DocumentVO documentVO = new DocumentVO();
			documentVO.setId(item.getId());
			documentVO.setFileName(item.getFileName());
			documentVO.setIsEmbedding(item.getIsEmbedding());
			documentVO.setBaseId(Long.valueOf(item.getBaseId()));
			documentVO.setPath(path);
			documentVO.setKnowledgeBaseName(knowledgeBase.getName());
			documentVO.setFileType(originFileResource.getContentType());
			documentVO.setUploadTime(item.getCreateTime());
			return documentVO;
		}).toList();
	}

}
