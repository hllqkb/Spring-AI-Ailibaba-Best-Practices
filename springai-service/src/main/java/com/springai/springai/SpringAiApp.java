package com.springai.springai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.springai.springai.mapper")
@EnableCaching
public class SpringAiApp {

	public static void main(String[] args) {

		SpringApplication.run(SpringAiApp.class, args);
		System.out.println("SpringAI启动成功");


	}

}
