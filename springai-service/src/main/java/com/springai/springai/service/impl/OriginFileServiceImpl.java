package com.springai.springai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springai.springai.mapper.OriginFileSourceMapper;
import com.springai.springai.service.OriginFileService;
import core.common.CoreCode;
import core.exception.BusinessException;
import core.pojo.entity.OriginFileSource;
import core.pojo.vo.ResourceVO;
import core.service.objectstore.ObjectStoreService;
import core.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author hllqk
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OriginFileServiceImpl extends ServiceImpl<OriginFileSourceMapper, OriginFileSource> implements OriginFileService{
    private final ObjectStoreService objectStoreService;
    @Override
    public List<Media> formResourceIds(List<String> ids) {
        //下载链接的资源
        if(ids==null||ids.isEmpty()){
            return List.of();
        }
        //获取原始文件列表
        List<OriginFileSource> originFileSources = this.listByIds(ids);
        List<Media> medias=originFileSources.stream().map(
                item-> {
                    //获取文件的临时下载链接
                    String url = objectStoreService.getTmpFileUrl(item.getBucketName(), item.getObjectName());
                    //按照.分割文件名，获取文件扩展名
                    String[] split = item.getFileName().split("\\.");
                    //获取文件扩展名
                    String suffix = split[split.length - 1];
                    try {
                        //下载文件
                        File file = FileUtil.downloadToTempFile(url, "chat_", suffix);
                        //获取文件mimeType类型
                        String mimeType = FileUtil.detectMimeType(file);
                        //返回媒体对象
                        return Media.builder().data(new ByteArrayResource(Files.readAllBytes(Path.of(file.getPath()))))
                                .mimeType(MimeTypeUtils.parseMimeType(mimeType))
                                .build();

                    } catch (Exception e) {
                        throw new BusinessException(CoreCode.SYSTEM_ERROR, "下载文件失败");
                    }
                }
        ).toList();
        return medias;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        return "";
    }

    @Override
    public Long uploadFile(MultipartFile file, String knowledgeId) {
        return 0L;
    }

    @Override
    public List<ResourceVO> resourcesFromIds(List<String> ids) {
        return List.of();
    }
}
