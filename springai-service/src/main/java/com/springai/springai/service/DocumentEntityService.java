package com.springai.springai.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import core.pojo.vo.DocumentVO;
import jakarta.servlet.http.HttpServletResponse;

public interface DocumentEntityService {

	Page<DocumentVO> listDocuments(DocumentVO document);

	Boolean deleteKnowledgeFile(DocumentVO documentVO);

	void download(Long fileId, HttpServletResponse response);

}
