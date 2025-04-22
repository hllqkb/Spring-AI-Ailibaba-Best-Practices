package com.springai.springai.controller;

import com.springai.springai.service.ChatService;
import core.pojo.vo.ChatRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author hllqk
 */
@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@ApiModel(value = "Ai对话接口")
public class ChatController {

	private final ChatService chatService;
/**
 * 统一对话接口
 * @param chatRequestVO 聊天请求参数
 * @return 聊天响应
 */
	@PostMapping(value = "/chat/unify", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Generation> unifyChat(@RequestBody ChatRequestVO chatRequestVO) {
		if (chatRequestVO == null) {
			log.error("ChatRequestVO is null");
			return Flux.error(new IllegalArgumentException("ChatRequestVO is null"));
		}
		
		return chatService.unifyChat(chatRequestVO)
				.filter(response -> response != null && response.getResult() != null)
				.map(ChatResponse::getResult)
				.flatMapSequential(generation -> {
					if (generation == null) {
						log.warn("Received null generation from ChatResponse");
						return Flux.empty();
					}
					return Flux.just(generation);
				})
				.onErrorResume(error -> {
					log.error("Error in chat processing: ", error);
					return Flux.error(error);
				});
	}
}
