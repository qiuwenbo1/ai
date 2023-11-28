package org.qwb.ai.common.constant;

public interface AppConstant {

    /**
	 * 应用版本
	 */
	String APPLICATION_VERSION = "0.0.1";

	/**
	 * 基础包
	 */
	String BASE_PACKAGES = "org.irm.ai";

	/**
	 * 应用名前缀
	 */
	String APPLICATION_NAME_PREFIX = "ai-";

	/**
	 * 租户数据库前缀
	 */
	String TENANT_DB_PREFIX = "ai_cloud_";


	/**
	 * 网关模块名称
	 */
	String APPLICATION_GATEWAY_NAME = APPLICATION_NAME_PREFIX + "gateway";

	
	/**
	 * 用户模块名称
	 */
	String APPLICATION_USER_NAME = APPLICATION_NAME_PREFIX + "user";

	/**
	 * 授权模块名称
	 */
	String APPLICATION_AUTH_NAME = APPLICATION_NAME_PREFIX + "auth";

	/**
	 * 后台系统模块名称
	 */
	String APPLICATION_SYSTEM_NAME = APPLICATION_NAME_PREFIX + "system";

	/**
	 * 后台数据接口
	 */
	String APPLICATION_API_NAME = APPLICATION_NAME_PREFIX + "api";

	/**
	 * 资源模块名称
	 */
	String APPLICATION_RESOURCE_NAME = APPLICATION_NAME_PREFIX + "resource";

	/**
	 * 资源模块名称
	 */
	String APPLICATION_LETTER_NAME = APPLICATION_NAME_PREFIX + "nlp-letter";


	/**
	 * 资源模型模块名称
	 */
	String APPLICATION_LETTER_MODEL_NAME = APPLICATION_NAME_PREFIX + "nlp-letter-model";

	/**
	 * 专有名词识别模块
	 */
	String APPLICATION_NER_SPECIFIC_NAME = APPLICATION_NAME_PREFIX + "nlp-ner";


	/**
	 * 专有名词识别模块
	 */
	String APPLICATION_NLP_RE = APPLICATION_NAME_PREFIX + "nlp-re";

	/**
	 * 自动问答模块
	 */
	String APPLICATION_QA_NAME = APPLICATION_NAME_PREFIX + "nlp-qa";

	String APPLICATION_QA_APP_NAME = APPLICATION_NAME_PREFIX + "qa";

	/**
	 * 自然语言相似度
	 */
	String APPLICATION_NLP_SIM = APPLICATION_NAME_PREFIX + "nlp-sim";

	/**
	 * 自然语言基础功能
	 */
	String APPLICATION_NLP_BASIC = APPLICATION_NAME_PREFIX + "nlp-basic";

	/**
	 * 专有名词识别模型模块
	 */
	String APPLICATION_NER_SPECIFIC_MODEL_NAME = APPLICATION_NAME_PREFIX + "nlp-ner-model";

	/**
	 * 人脸识别模块
	 */
	String APPLICATION_CV_FACE = APPLICATION_NAME_PREFIX + "cv-face";


	/**
	 * OCR服务
	 */
	String APPLICATION_CV_OCR = APPLICATION_NAME_PREFIX + "cv-ocr";

	/**
	 * 结构化服务
	 */
	String APPLICATION_CV_STRUCTURE = APPLICATION_NAME_PREFIX + "cv-structure";

	/**
	 * 照片相似度服务
	 */
	String APPLICATION_CV_SIM = APPLICATION_NAME_PREFIX + "cv-sim";

	/**
	 * 图像分类服务
	 */
	String APPLICATION_CV_CLS = APPLICATION_NAME_PREFIX + "cv-cls";

	/**
	 * 文本信息抽取服务
	 */
	String APPLICATION_NLP_IE = APPLICATION_NAME_PREFIX + "nlp-ie";

	String APPLICATION_DOC_QA_NAME = APPLICATION_NAME_PREFIX + "doc-qa";

	/**
	 * 敏感词模块
	 */
	String APPLICATION_SENSITIVE_NAME = APPLICATION_NAME_PREFIX + "sensitive";

	/**
	 * Python SideCar
	 */
	String APPLICATION_AI_NAME = APPLICATION_NAME_PREFIX + "sidecar";
	
	/**
	 * 开发环境
	 */
	String DEV_CODE = "dev";
	/**
	 * 生产环境
	 */
	String PROD_CODE = "prod";
	/**
	 * 测试环境
	 */
	String TEST_CODE = "test";

	/**
	 * 代码部署于 linux 上，工作默认为 mac 和 Windows
	 */
	String OS_NAME_LINUX = "LINUX";

	/**
	 * 顶级父节点id
	 */
	long TOP_PARENT_ID = 0L;

	/**
	 * 顶级父节点名称
	 */
	String TOP_PARENT_NAME = "顶级";

	String NLP_TEXT_SEP = "[SEP]";

	
    
}