package com.springai.springai.service;

import core.pojo.vo.ChatMessageVO;
import core.pojo.vo.ChatRequestVO;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

	/**
	 * 简单的流式对话
	 * @param chatMessageVO
	 * @return
	 */
	Flux<ChatResponse> simpleChat(ChatMessageVO chatMessageVO);

	/**
	 * 多模态对话
	 * @param chatMessageVO
	 * @return
	 */
	Flux<ChatResponse> multimodalChat(ChatMessageVO chatMessageVO);

	/**
	 * RAG对话
	 * @param chatMessageVO
	 * @param baseIds 知识库ID列表
	 * @return
	 */
	Flux<ChatResponse> simpleRAGChat(ChatMessageVO chatMessageVO, List<Long> baseIds);

	/**
	 * 多模态的RAG对话
	 * @param chatMessageVO
	 * @return
	 */
	Flux<ChatResponse> multimodalRAGChat(ChatMessageVO chatMessageVO, List<Long> baseIds);

	/**
	 * 统一接口对话
	 * @param chatRequestVO
	 * @return
	 */
	Flux<ChatResponse> unifyChat(ChatRequestVO chatRequestVO);

}
