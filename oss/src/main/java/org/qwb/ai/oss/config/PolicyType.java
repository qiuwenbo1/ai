package org.qwb.ai.oss.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PolicyType {
    
    /**
	 * 只读
	 */
	READ("read", "只读"),

	/**
	 * 只写
	 */
	WRITE("write", "只写"),

	/**
	 * 读写
	 */
	READ_WRITE("read_write", "读写");

	/**
	 * 类型
	 */
	private final String type;
	/**
	 * 描述
	 */
	private final String policy;
}
