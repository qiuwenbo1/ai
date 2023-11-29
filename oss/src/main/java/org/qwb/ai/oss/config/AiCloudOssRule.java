package org.qwb.ai.oss.config;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;
import org.qwb.ai.common.utils.StringPool;

@AllArgsConstructor
public class AiCloudOssRule implements OssRule {

	@Override
	public String bucketName(String bucketName) {
		return bucketName;
	}

	@Override
	public String fileName(String originalFilename) {
		return "upload" + StringPool.SLASH + DateUtil.today() + StringPool.SLASH + IdUtil.randomUUID() + StringPool.DOT + FileUtil.getSuffix(originalFilename);
	}   
}
