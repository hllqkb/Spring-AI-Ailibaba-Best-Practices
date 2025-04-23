package com.springai.springai.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author hllqk
 */
@Slf4j
@Component
public class WeatherTool {
    @Tool(description = "获取指定省份的城市天气信息")
    public String getWeather(@ToolParam(description = "省份名称") String province, @ToolParam(description = "城市名称") String city) {
        try{
            String url=String.format("https://cn.apihz.cn/api/tianqi/tqyb.php?id=88888888&key=88888888&sheng={}&place={}",province,city);
            WebClient webClient = WebClient.create();
            return webClient.get().uri(url).retrieve().bodyToMono(String.class).block();
        }catch (Exception e){
            log.info("获取天气信息失败:{}", e.getMessage());
            return "获取天气信息失败";
        }
    }
}
