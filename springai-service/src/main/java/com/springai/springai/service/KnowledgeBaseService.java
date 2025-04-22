package com.springai.springai.service;

import core.pojo.vo.KnowLedgeBaseVO;
import core.pojo.vo.SimpleBaseVO;

import java.util.List;

public interface KnowledgeBaseService {
    Long create(KnowLedgeBaseVO knowledgeBaseVO);

    Long update(KnowLedgeBaseVO knowledgeBaseVO);

    Integer delete(KnowLedgeBaseVO knowledgeBaseVO);

    List<KnowLedgeBaseVO> listKnowledgeBases();

    List<SimpleBaseVO> getSimple();
}
