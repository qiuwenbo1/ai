package org.qwb.ai.gateway;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.qwb.ai.common.constant.AppConstant;
import org.qwb.ai.gateway.dynamic.NacosConstant;
import org.qwb.ai.gateway.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class AiApplicationStarter {

    /**
	 * Create an application context
	 * java -jar app.jar --spring.profiles.active=prod --server.port=2333
	 *
	 * @param appName application name
	 * @param source  The sources
	 */
	public static <T>void run(String appName, Class<T> source, String... args) {
		SpringApplicationBuilder builder = createSpringApplicationBuilder(appName, source, args);
		builder.run(args);
	}

	public static <T>SpringApplicationBuilder createSpringApplicationBuilder(String appName, Class<T> source, String... args) {
		Assert.hasText(appName, "[appName]服务名不能为空");
		// 读取环境变量，使用spring boot的规则
		ConfigurableEnvironment environment = new StandardEnvironment();
		MutablePropertySources propertySources = environment.getPropertySources();
		propertySources.addFirst(new SimpleCommandLinePropertySource(args));
		propertySources.addLast(new MapPropertySource(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, environment.getSystemProperties()));
		propertySources.addLast(new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, environment.getSystemEnvironment()));
		// 获取配置的环境变量
		String[] activeProfiles = environment.getActiveProfiles();
		// 判断环境:dev、test、prod
		List<String> profiles = Arrays.asList(activeProfiles);
		// 当前使用
		List<String> activeProfileList = new ArrayList<>(profiles);
		Function<Object[], String> joinFun = StringUtils::arrayToCommaDelimitedString;
		SpringApplicationBuilder builder = new SpringApplicationBuilder(source);
		String profile;
		if (activeProfileList.isEmpty()) {
			// 默认dev开发
			profile = AppConstant.DEV_CODE;
			activeProfileList.add(profile);
			builder.profiles(profile);
		} else if (activeProfileList.size() == 1) {
			profile = activeProfileList.get(0);
		} else {
			// 同时存在dev、test、prod环境时
			throw new RuntimeException("同时存在环境变量:[" + StringUtils.arrayToCommaDelimitedString(activeProfiles) + "]");
		}
		String startJarPath = AiApplicationStarter.class.getResource("/").getPath().split("!")[0];
		String activePros = joinFun.apply(activeProfileList.toArray());
		System.out.printf("----启动中，读取到的环境变量:[%s]，jar地址:[%s]----%n", activePros, startJarPath);
		Properties props = System.getProperties();
		props.setProperty("spring.application.name", appName);
		props.setProperty("spring.profiles.active", profile);
		props.setProperty("info.version", AppConstant.APPLICATION_VERSION);
		props.setProperty("info.desc", appName);
		props.setProperty("file.encoding", StandardCharsets.UTF_8.name());
		props.setProperty("blade.env", profile);
		props.setProperty("blade.name", appName);
		props.setProperty("blade.is-local", String.valueOf(isLocalDev()));
		props.setProperty("blade.dev-mode", profile.equals(AppConstant.PROD_CODE) ? "false" : "true");
		props.setProperty("blade.service.version", AppConstant.APPLICATION_VERSION);
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty("spring.main.allow-bean-definition-overriding", "true");
		defaultProperties.setProperty("spring.sleuth.sampler.percentage", "1.0");
		defaultProperties.setProperty("spring.cloud.alibaba.seata.tx-service-group", appName.concat(NacosConstant.NACOS_GROUP_SUFFIX));
		defaultProperties.setProperty("spring.cloud.nacos.config.file-extension", NacosConstant.NACOS_CONFIG_FORMAT);
		defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[0].data-id", NacosConstant.sharedDataId());
		defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[0].group", NacosConstant.NACOS_CONFIG_GROUP);
		defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[0].refresh", NacosConstant.NACOS_CONFIG_REFRESH);
		defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[1].data-id", NacosConstant.sharedDataId(profile));
		defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[1].group", NacosConstant.NACOS_CONFIG_GROUP);
		defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[1].refresh", NacosConstant.NACOS_CONFIG_REFRESH);
		builder.properties(defaultProperties);
		// 加载自定义组件
		List<LauncherService> launcherList = new ArrayList<>();
		ServiceLoader.load(LauncherService.class).forEach(launcherList::add);
		launcherList.stream().sorted(Comparator.comparing(LauncherService::getOrder)).collect(Collectors.toList())
			.forEach(launcherService -> launcherService.launcher(builder, appName, profile, isLocalDev()));
		return builder;
	}

	/**
	 * 判断是否为本地开发环境
	 *
	 * @return boolean
	 */
	public static boolean isLocalDev() {
		String osName = System.getProperty("os.name");
		return StringUtils.hasText(osName) && !(AppConstant.OS_NAME_LINUX.equalsIgnoreCase(osName));
	}
    
}
