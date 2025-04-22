package com.springai.springai.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
/**
 * 聊天类型枚举类
 * @author hllqkb
 */
//枚举类，用于定义聊天类型
@Getter
@RequiredArgsConstructor // lombok注解，自动生成构造方法
public enum ChatType {
    //定义枚举值
    UNKNOWN("unknown"),SIMPLE("simple"),SIMPLE_RAG("simpleRAG"),MULTIMODEL("multimodal"),MULTIMODEL_RAG("multimodalRAG")
    ,LONGMODEL("longmodel");
    //定义枚举值对应的字符串值
    private final String value;
    public static ChatType parse(String value){
        return switch (value){
            case "simple" -> ChatType.SIMPLE;
            case "simpleRAG" -> ChatType.SIMPLE_RAG;
            case "multimodal", "multimodel" -> ChatType.MULTIMODEL;
            case "multimodalRAG", "multimodelRAG" -> ChatType.MULTIMODEL_RAG;
            case "longmodel"->ChatType.LONGMODEL;
            default -> ChatType.UNKNOWN;
        };
    }
}
