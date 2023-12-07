package org.qwb.ai.gateway.dynamic;

public interface NacosConstant {
    /**
	 * nacos 地址
	 */
	String NACOS_ADDR = "192.168.50.27:8849";

	/**
	 * nacos 配置前缀
	 */
	String NACOS_CONFIG_PREFIX = "unify";

	/**
	 * nacos 组配置后缀
	 */
	String NACOS_GROUP_SUFFIX = "-group";

	/**
	 * nacos 配置文件类型
	 */
	String NACOS_CONFIG_FORMAT = "json";

	/**
	 * nacos json配置文件类型
	 */
	String NACOS_CONFIG_JSON_FORMAT = "json";

	/**
	 * nacos 是否刷新
	 */
	String NACOS_CONFIG_REFRESH = "true";

	/**
	 * nacos 分组
	 */
	String NACOS_CONFIG_GROUP = "DEFAULT_GROUP";

	/**
	 * seata 分组
	 */
	String NACOS_SEATA_GROUP = "SEATA_GROUP";

	/**
	 * 构建服务对应的 dataId
	 *
	 * @param appName 服务名
	 * @return dataId
	 */
	static String dataId(String appName) {
		return appName + "." + NACOS_CONFIG_FORMAT;
	}

	/**
	 * 构建服务对应的 dataId
	 *
	 * @param appName 服务名
	 * @param profile 环境变量
	 * @return dataId
	 */
	static String dataId(String appName, String profile) {
		return dataId(appName, profile, NACOS_CONFIG_FORMAT);
	}

	/**
	 * 构建服务对应的 dataId
	 *
	 * @param appName 服务名
	 * @param profile 环境变量
	 * @param format  文件类型
	 * @return dataId
	 */
	static String dataId(String appName, String profile, String format) {
		return appName + "-" + profile + "." + format;
	}

	/**
	 * 服务默认加载的配置
	 *
	 * @return sharedDataIds
	 */
	static String sharedDataId() {
		return NACOS_CONFIG_PREFIX + "." + NACOS_CONFIG_FORMAT;
	}

	/**
	 * 服务默认加载的配置
	 *
	 * @param profile 环境变量
	 * @return sharedDataIds
	 */
	static String sharedDataId(String profile) {
		return NACOS_CONFIG_PREFIX + "-" + profile + "." + NACOS_CONFIG_FORMAT;
	}

	/**
	 * 服务默认加载的配置
	 *
	 * @param profile 环境变量
	 * @return sharedDataIds
	 */
	static String sharedDataIds(String profile) {
		return NACOS_CONFIG_PREFIX + "." + NACOS_CONFIG_FORMAT + "," + NACOS_CONFIG_PREFIX + "-" + profile + "." + NACOS_CONFIG_FORMAT;
	}
}
