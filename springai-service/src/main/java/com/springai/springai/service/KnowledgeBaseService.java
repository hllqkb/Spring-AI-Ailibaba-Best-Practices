package com.springai.springai.service;

import core.pojo.vo.KnowLedgeBaseVO;
import core.pojo.vo.SimpleBaseVO;

import java.util.List;

public interface KnowledgeBaseService {
    String create(KnowLedgeBaseVO knowledgeBaseVO);

    String update(KnowLedgeBaseVO knowledgeBaseVO);

    Integer delete(KnowLedgeBaseVO knowledgeBaseVO);

    List<KnowLedgeBaseVO> listKnowledgeBases();

    List<SimpleBaseVO> getSimple();
}
