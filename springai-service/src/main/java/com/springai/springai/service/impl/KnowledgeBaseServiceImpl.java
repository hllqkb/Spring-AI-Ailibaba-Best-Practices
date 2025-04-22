package com.springai.springai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.springai.springai.service.KnowledgeBaseService;
import core.pojo.entity.KnowledgeBase;
import com.springai.springai.mapper.KnowledgeBaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import core.pojo.vo.KnowLedgeBaseVO;
import core.pojo.vo.SimpleBaseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Service
public class KnowledgeBaseServiceImpl extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBase>
		implements KnowledgeBaseService {

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Long create(KnowLedgeBaseVO knowledgeBaseVO) {
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.setName(knowledgeBaseVO.getName());
		knowledgeBase.setDescription(knowledgeBaseVO.getDescription());
		saveOrUpdate(knowledgeBase);
		return knowledgeBase.getId();
		}

		@Transactional(rollbackFor = Exception.class)
	@Override
	public Long update(KnowLedgeBaseVO knowledgeBaseVO) {
		KnowledgeBase knowledgeBase = new KnowledgeBase();
		if(knowledgeBaseVO.getId() == null){
			return 0L;
		}
		knowledgeBase.setId(knowledgeBaseVO.getId());
		knowledgeBase.setName(knowledgeBaseVO.getName());
		knowledgeBase.setDescription(knowledgeBaseVO.getDescription());
		saveOrUpdate(knowledgeBase);
		return knowledgeBase.getId();
	}

	@Override
	public Integer delete(KnowLedgeBaseVO knowledgeBaseVO) {
		boolean result = removeById(knowledgeBaseVO.getId());
		return result? 1 : 0;
	}

	@Override
	public List<KnowLedgeBaseVO> listKnowledgeBases() {
		LambdaQueryWrapper<KnowledgeBase> qw=new LambdaQueryWrapper<>();
		qw.orderByAsc(KnowledgeBase::getCreateTime);
		List<KnowledgeBase> knowledgeBases = list(qw);
        return convertList(knowledgeBases);
	}

	@Override
	public List<SimpleBaseVO> getSimple() {
		List<KnowledgeBase> knowledgeBases = list();
		return knowledgeBases.stream().map(item -> {
			SimpleBaseVO simpleBaseVO = new SimpleBaseVO();
			simpleBaseVO.setId(String.valueOf(item.getId()));
			simpleBaseVO.setName(item.getName());
			return simpleBaseVO;
		}).toList();
	}

	public static List<KnowLedgeBaseVO> convertList(List<KnowledgeBase> knowledgeBases) {
		//经典的流转换KnowLedgeBase -> KnowLedgeBaseVO
		return knowledgeBases.stream().map(item -> {
			KnowLedgeBaseVO knowLedgeBaseVO = new KnowLedgeBaseVO();
			knowLedgeBaseVO.setId(item.getId());
			knowLedgeBaseVO.setName(item.getName());
			knowLedgeBaseVO.setDescription(item.getDescription());
			return knowLedgeBaseVO;
		}).toList();
	}}
