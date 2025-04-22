package com.springai.springai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springai.springai.mapper.DocumentEntityMapper;
import com.springai.springai.mapper.OriginFileSourceMapper;
import com.springai.springai.service.LLMService;
import com.springai.springai.service.OriginFileService;
import com.springai.springai.utils.SaTokenUtil;
import core.common.CoreCode;
import core.exception.BusinessException;
import core.pojo.entity.DocumentEntity;
import core.pojo.entity.OriginFileSource;
import core.pojo.entity.SystemUser;
import core.pojo.vo.ResourceVO;
import core.service.objectstore.ObjectStoreService;
import core.service.objectstore.StorageFile;
import core.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author hllqk
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OriginFileServiceImpl extends ServiceImpl<OriginFileSourceMapper, OriginFileSource> implements OriginFileService{
    private final ObjectStoreService objectStoreService;
    private final DocumentEntityMapper documentEntityMapper;
    private final SaTokenUtil saTokenUtil;
    private final TokenTextSplitter tokenTextSplitter;

    private final LLMService llmService;
    public static final String CHAT_BUCKET_NAME = "origin-file";

    public static final String KNOWLEDGE_BUCKET_NAME = "knowledge-file";
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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String uploadFile(MultipartFile file) {
        //上传文件到对象存储
        OriginFileSource upload=this.upload(file,CHAT_BUCKET_NAME);
        return upload.getId();
    }



    @Override
    public Long uploadFile(MultipartFile file, String knowledgeId) {
        //上传知识库
        //先上传文件到Minio
        OriginFileSource upload=this.upload(file,KNOWLEDGE_BUCKET_NAME);
        //保存到数据库
        DocumentEntity documentEntity=new DocumentEntity();
        documentEntity.setFileName(file.getOriginalFilename());
        documentEntity.setBaseId(knowledgeId);
        documentEntity.setPath(upload.getPath());
        documentEntity.setIsEmbedding(false);
        documentEntity.setResourceId(upload.getId());
        documentEntityMapper.insert(documentEntity);
        //向量化
        Resource resource;
        try{
            InputStream inputStream = objectStoreService.getFile(upload.getBucketName(), upload.getObjectName());
            byte[] bytes = inputStream.readAllBytes();
            resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
                
                @Override
                public String getDescription() {
                    return "File: " + file.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            throw new BusinessException(CoreCode.SYSTEM_ERROR, e.getMessage());
        }
        TikaDocumentReader tikaDocumentReader=new TikaDocumentReader(resource);
        List<Document> rawDocumentList = tikaDocumentReader.read();
        List<Document> splitDocumentList = tokenTextSplitter.split(rawDocumentList);
        List<Document> hasMetaDocumentList = splitDocumentList.stream().map(item -> {
            Map<String, Object> metadata = item.getMetadata();
            metadata.put("user_id", StpUtil.getLoginIdAsLong());
            metadata.put("knowledge_base_id", knowledgeId);
            metadata.put("document_id", documentEntity.getId());
            metadata.put("source", upload.getPath());
            metadata.put("filename", file.getOriginalFilename());
            return new Document(item.getText(), metadata);
        }).toList();
        VectorStore vectorStore = llmService.getVectorStore();
        vectorStore.accept(hasMetaDocumentList);
        // 4. 更新
        documentEntity.setIsEmbedding(true);
        documentEntityMapper.updateById(documentEntity);
        return documentEntity.getId();
    }

    @Override
    public List<ResourceVO> resourcesFromIds(List<String> ids) {
        return List.of();
    }
    private OriginFileSource upload(MultipartFile file, String bucketName) {
        String originalFilename = file.getOriginalFilename();
        String objectName = objectNameWithUserId(originalFilename);
        String id = FileUtil.generatorFileId(bucketName, objectName);
        String newObjectName = String.format("%s/%s", objectName, id);
        String path;
        String md5;
        try {
            File tmpFile = FileUtil.createTempFile("know", "_" + file.getOriginalFilename());
            file.transferTo(tmpFile);
            md5 = FileUtil.md5(tmpFile);
            path = objectStoreService.uploadFile(tmpFile, bucketName, newObjectName);
        }
        catch (IOException e) {
            throw new BusinessException(CoreCode.SYSTEM_ERROR, e.getMessage());
        }
        StorageFile fileInfo = objectStoreService.getFileInfo(bucketName, newObjectName);
        OriginFileSource originFileResource = new OriginFileSource();
        originFileResource.setMd5(md5);
        originFileResource.setFileName(originalFilename);
        originFileResource.setPath(path);
        originFileResource.setId(fileInfo.getId());
        originFileResource.setBucketName(bucketName);
        originFileResource.setObjectName(newObjectName);
        originFileResource.setIsImage(file.getContentType() != null && file.getContentType().startsWith("image"));
        originFileResource.setSize(fileInfo.getSize());
        originFileResource.setContentType(fileInfo.getContentType());
        this.saveOrUpdate(originFileResource);
        return originFileResource;
    }
    private String objectNameWithUserId(String filename) {
        SystemUser loginUser = saTokenUtil.getLoginUser();
        return loginUser.getId() + "/" + UUID.randomUUID().toString().replace("-", "") + "-" + filename;
    }

}
