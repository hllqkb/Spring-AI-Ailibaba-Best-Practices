package com.itheima.heimaai.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Service
public class FileStoreService {
    private final VectorStore vectorStore;

    public FileStoreService(VectorStore vectorStore, DocumentTransformer documentTransformer) {
        this.vectorStore = vectorStore;
    }

    public Object saveFile(MultipartFile file) {
        try {
            if (isZipFile(file)) {
                return handleZipFile(file);
            } else {
                return handleRegularFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

    private boolean isZipFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/zip");
    }



    private Object handleRegularFile(MultipartFile file) throws Exception {
        // 从IO流中读取文件
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
        // 将文本内容划分成更小的块
        List<Document> splitDocuments = new TokenTextSplitter()
                .apply(tikaDocumentReader.read());
        // 存入向量数据库，这个过程会自动调用embeddingModel,将文本变成向量再存入。
        vectorStore.add(splitDocuments);
        return "上传成功";
    }

    private Object handleZipFile(MultipartFile file) throws Exception {
        try {
            // 首先尝试使用 UTF-8 编码读取
            try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
                return processZipEntries(zipInputStream);
            } catch (IllegalArgumentException e) {
                // 如果 UTF-8 编码读取失败，则尝试使用 GBK 编码读取
                try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream(), Charset.forName("GBK"))) {
                    return processZipEntries(zipInputStream);
                } catch (IllegalArgumentException ex) {
                    // 如果 GBK 编码也读取失败，则抛出异常
                    //throw new RuntimeException("无法读取压缩包，请检查压缩包编码格式", ex);
                    return "上传失败";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

    private Object processZipEntries(ZipInputStream zipInputStream) throws Exception {
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, len);
                    }
                    byte[] bytes = outputStream.toByteArray();

                    // 将字节数组转换为字符串，并去除首尾空白字符
                    String content = new String(bytes, StandardCharsets.UTF_8).trim();

                    // 检查文件内容是否为空字符串
                    if (!content.isEmpty()) {
                        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
                        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(byteArrayResource);
                        // 将文本内容划分成更小的块
                        List<Document> splitDocuments = new TokenTextSplitter()
                                .apply(tikaDocumentReader.read());
                        // 存入向量数据库，这个过程会自动调用embeddingModel,将文本变成向量再存入。
                        vectorStore.add(splitDocuments);
                    } else {
                        // 处理空文件
                        System.out.println("跳过空文件: " + entry.getName());
                    }
                } catch (IllegalArgumentException e) {
                    // 捕获 IllegalArgumentException 异常，并记录错误日志
                    System.out.println("处理文件 " + entry.getName() + " 时出错：" + e.getMessage());
                    // 跳过当前文件，继续处理下一个文件
                    continue;
                }
            }
            zipInputStream.closeEntry();
        }
        return "上传成功";
    }
}