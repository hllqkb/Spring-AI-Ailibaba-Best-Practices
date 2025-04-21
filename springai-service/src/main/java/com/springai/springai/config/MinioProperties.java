package com.springai.springai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

	private String endpoint;

	private String accessKey;

	private String secretKey;

	private String defaultBucket = "default";

	private int defaultExpiry = 3600; // 默认1小时有效期

}
