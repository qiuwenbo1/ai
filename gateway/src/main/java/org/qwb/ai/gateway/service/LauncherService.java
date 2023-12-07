package org.qwb.ai.gateway.service;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.Ordered;


public interface LauncherService extends Ordered, Comparable<LauncherService> {

	/**
	 * 启动时 处理 SpringApplicationBuilder
	 *
	 * @param builder    SpringApplicationBuilder
	 * @param appName    SpringApplicationAppName
	 * @param profile    SpringApplicationProfile
	 * @param isLocalDev SpringApplicationIsLocalDev
	 */
	void launcher(SpringApplicationBuilder builder, String appName, String profile, boolean isLocalDev);

	/**
	 * 获取排列顺序
	 *
	 * @return order
	 */
	@Override
	default int getOrder() {
		return 0;
	}

	/**
	 * 对比排序
	 *
	 * @param o LauncherService
	 * @return compare
	 */
	@Override
	default int compareTo(LauncherService o) {
		return Integer.compare(this.getOrder(), o.getOrder());
	}

}