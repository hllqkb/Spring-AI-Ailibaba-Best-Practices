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

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@ApiModel(value = "Ai对话接口")
public class ChatController {

	private final ChatService chatService;

	@ApiModelProperty(value = "对话接口", notes = "接受前端的请求，调用chatService的chat方法，返回Flux<ChatResponse>")
	@PostMapping(value = "/chat/unify", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Generation> unifyChat(@RequestBody ChatRequestVO chatRequestVO) {
		//接受前端的请求，调用chatService的unifyChat方法，返回Flux<Generation>
		return chatService.unifyChat(chatRequestVO).map(ChatResponse::getResult)
				.flatMapSequential(Flux::just);
	}

}
