package org.qwb.ai.common.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class AiCloudFile {
    
    /**
	 * 文件地址
	 */
	private String link;
	/**
	 * 域名地址
	 */
	private String domain;
	/**
	 * 文件名
	 */
	private String name;
	/**
	 * 初始文件名
	 */
	private String originalName;
	/**
	 * 附件表ID
	 */
	private Long attachId;
}   
