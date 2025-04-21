package com.springai.springai.service.impl;

import core.service.objectstore.ObjectStoreService;
import core.service.objectstore.StorageFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ObjectStoreServiceImpl implements ObjectStoreService {
    @Override
    public String uploadFile(InputStream inputStream, long fileSize, String bucketName, String objectName, String contentType) throws IOException {
        return "";
    }

    @Override
    public String uploadFile(MultipartFile file, String objectName) {
        return "";
    }

    @Override
    public String getTmpFileUrl(String bucketName, String objectName, int expirySeconds) {
        return "";
    }

    @Override
    public String getTmpFileUrl(String bucketName, String objectName) {
        return "";
    }

    @Override
    public InputStream getFile(String bucketName, String objectName) {
        return null;
    }

    @Override
    public void deleteFile(String bucketName, String objectName) {

    }

    @Override
    public List<String> listFiles(String bucketName) {
        return List.of();
    }

    @Override
    public StorageFile getFileInfo(String bucketName, String objectName) {
        return null;
    }

    @Override
    public boolean bucketExists(String bucketName) {
        return false;
    }

    @Override
    public void createBucket(String bucketName) {

    }
}
