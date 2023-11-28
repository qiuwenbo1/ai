/*
 * @Author: liu lichao
 * @Date: 2021-01-04 19:11:31
 * @LastEditors: liu lichao
 * @LastEditTime: 2021-12-22 09:37:26
 * @Description: file content
 */
package org.qwb.ai.oss.config;

public interface OssRule {
    /**
	 * 获取存储桶规则
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	String bucketName(String bucketName);

	/**
	 * 获取文件名规则
	 *
	 * @param originalFilename 文件名
	 * @return String
	 */
	String fileName(String originalFilename);
}
