package com.springai.springai.service;

import java.util.List;

import org.springframework.ai.content.Media;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;

import core.pojo.entity.OriginFileSource;
import core.pojo.vo.ResourceVO;

public interface OriginFileService extends IService<OriginFileSource> {
    //根据id获取源文件信息
    List<Media> formResourceIds(List<String> id);
    //对话附件
    String uploadFile(MultipartFile file);
    //知识库附件
    Long uploadFile(MultipartFile file, Long knowledgeId);
    //id转资源Vo
    List<ResourceVO> resourcesFromIds(List<String> ids);
}
