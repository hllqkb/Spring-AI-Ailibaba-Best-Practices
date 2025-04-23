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
 * AI对话统一接口
 */
@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

	/**
	 * 调用工具类的聊天服务
	 * @param chatRequestVO 聊天请求参数
	 * @return 聊天响应
	 */
	private final ChatService chatService;
	@PostMapping(value = "/chat/function",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Generation> getFunctionChat(@RequestBody ChatRequestVO chatRequestVO) {
		if(chatRequestVO == null){
			return Flux.error(new IllegalArgumentException("请求参数为空"));
		}
		return chatService.getFunctionChat(chatRequestVO).
				filter(response -> response != null && response.getResult() != null)
				.map(ChatResponse::getResult)
				.flatMapSequential(generation -> {
					if (generation == null) {
						log.warn("回复为空");
						return Flux.empty();
					}
					return Flux.just(generation);
				})
				.onErrorResume(error -> {
					log.error("聊天过程中出现错误: ", error);
					return Flux.error(error);
				});
	}
/**
 * 统一对话接口
 * @param chatRequestVO 聊天请求参数
 * @return 聊天响应
 */
	@PostMapping(value = "/chat/unify", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Generation> unifyChat(@RequestBody ChatRequestVO chatRequestVO) {
		if (chatRequestVO == null) {
			log.error("请求参数为空");
			return Flux.error(new IllegalArgumentException("请求参数为空"));
		}
		
		return chatService.unifyChat(chatRequestVO)
				.filter(response -> response != null && response.getResult() != null)
				.map(ChatResponse::getResult)
				.flatMapSequential(generation -> {
					if (generation == null) {
						log.warn("回复为空");
						return Flux.empty();
					}
					return Flux.just(generation);
				})
				.onErrorResume(error -> {
					log.error("聊天过程中出现错误: ", error);
					return Flux.error(error);
				});
	}
}
