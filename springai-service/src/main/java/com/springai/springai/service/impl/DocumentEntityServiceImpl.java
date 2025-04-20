package com.springai.springai.service.impl;

import core.pojo.entity.DocumentEntity;
import com.springai.springai.mapper.DocumentEntityMapper;
import com.springai.springai.service.IDocumentEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hllqkb
 * @since 2025-04-20
 */
@Service
public class DocumentEntityServiceImpl extends ServiceImpl<DocumentEntityMapper, DocumentEntity>
		implements IDocumentEntityService {

}
