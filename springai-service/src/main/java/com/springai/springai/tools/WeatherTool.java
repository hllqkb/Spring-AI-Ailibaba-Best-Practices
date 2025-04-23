package com.springai.springai.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

/**
 * 天气查询工具类
 * @author hllqk
 */
@Slf4j
@Component
public class WeatherTool {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${weather.api-id}")
    private String apiId;

    @Value("${weather.api-key}")
    private String apiKey;

    @Value("${weather.timeout}")
    private int timeout;

    @Value("${weather.max-retries}")
    private int maxRetries;

    @Value("${weather.retry-delay}")
    private int retryDelay;

    public WeatherTool(@Value("${weather.base-url}") String baseUrl, ObjectMapper objectMapper) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.objectMapper = objectMapper;
    }

    @Tool(description = "查询中国城市实时天气数据，输入必须包含省份和城市名称（例如：四川省绵阳市）。")
    public String getWeather(
            @ToolParam(description = "省份名称，如 '四川省'") String province,
            @ToolParam(description = "城市名称，如 '绵阳市'") String city
    ) {
        try {
            // 如果输入是 JSON 字符串，尝试解析
            if (province.startsWith("{")) {
                Map<String, String> params = objectMapper.readValue(province, Map.class);
                province = params.get("province");
                city = params.get("city");
            }

            log.info("正在查询天气信息 - 省份: {}, 城市: {}", province, city);
            //调用信息打印，提示工具类调用成功
            
            final String finalProvince = province;
            final String finalCity = city;
            
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/tqyb.php")
                            .queryParam("id", apiId)
                            .queryParam("key", apiKey)
                            .queryParam("sheng", finalProvince)
                            .queryParam("place", finalCity)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(timeout))
                    .retryWhen(Retry.backoff(maxRetries, Duration.ofSeconds(retryDelay)))
                    .onErrorResume(e -> {
                        log.error("获取天气信息失败: {}", e.getMessage());
                        return Mono.just("获取天气信息失败，请稍后重试");
                    })
                    .block();

            log.info("天气查询结果: {}", response);
            return response;
        } catch (Exception e) {
            log.error("获取天气信息发生异常: {}", e.getMessage());
            return "获取天气信息失败，请稍后重试";
        }
    }
}
