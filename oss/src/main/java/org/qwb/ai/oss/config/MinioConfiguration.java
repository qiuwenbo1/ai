/*
 * @Author: liu lichao
 * @Date: 2021-01-04 18:45:35
 * @LastEditors: liu lichao
 * @LastEditTime: 2021-12-22 09:36:53
 * @Description: file content
 */
package org.qwb.ai.oss.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;
import lombok.Data;
import lombok.SneakyThrows;

@Data
@Configuration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(value = "oss.name", havingValue = "minio")
@ConfigurationProperties(prefix = "oss")
public class MinioConfiguration {
    
    private final OssProperties ossProperties;

	@Bean
	@ConditionalOnMissingBean(OssRule.class)
	public OssRule ossRule() {
		return new AiCloudOssRule(ossProperties.getTenantMode());
	}

	@Bean
	@SneakyThrows
	@ConditionalOnMissingBean(MinioClient.class)
	public MinioClient minioClient() {
		return new MinioClient(
			ossProperties.getEndpoint(),
			ossProperties.getAccessKey(),
			ossProperties.getSecretKey()
		);
	}

	@Bean
	@ConditionalOnBean({MinioClient.class, OssRule.class})
	@ConditionalOnMissingBean(MinioTemplate.class)
	public MinioTemplate minioTemplate(MinioClient minioClient, OssRule ossRule) {
		return new MinioTemplate(minioClient, ossRule, ossProperties);
	}
}
