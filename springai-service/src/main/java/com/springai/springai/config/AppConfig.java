package com.springai.springai.config;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class AppConfig {

	@Bean
	public TokenTextSplitter tokenTextSplitter() {
		return new TokenTextSplitter();

	}

}
