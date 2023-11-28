package org.qwb.ai.oss.config;

import org.irm.ai.common.utils.StringPool;
import org.irm.ai.user.utils.AuthUtil;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AiCloudOssRule implements OssRule {
    /**
	 * 租户模式
	 */
	private final Boolean tenantMode;

	@Override
	public String bucketName(String bucketName) {
		String suffix = (tenantMode) ? AuthUtil.getTenantId() : StringPool.EMPTY;
		return bucketName + suffix;
	}

	@Override
	public String fileName(String originalFilename) {
		return "upload" + StringPool.SLASH + DateUtil.today() + StringPool.SLASH + IdUtil.randomUUID() + StringPool.DOT + FileUtil.getSuffix(originalFilename);
	}   
}
