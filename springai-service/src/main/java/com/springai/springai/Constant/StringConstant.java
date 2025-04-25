package com.springai.springai.Constant;

/**
 * @author hllqk
 * 字符串常量
 */
public interface StringConstant {
String CHAT_CONSERVATION_NAME = "CHAT_CONSERVATION_NAME";
String CHAT_MEDIAS="CHAT_MEDIAS";
//最大记忆对话轮数,15轮
Integer CHAT_MAX_LENGTH=15;
Integer RAG_TOP_K=5;
String MinioUrlPrefix = "http://127.0.0.1:9001/api/v1/buckets/origin-file/objects/download?preview=true&prefix=";
}
