package com.springai.springai.service;

import core.pojo.vo.ChatRequestVO;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

public interface ChatService {
	Flux<ChatResponse> unifyChat(ChatRequestVO chatRequestVO);
}
