package org.qwb.ai.common.constant;

public interface AuthConstant {

    /**
     * JWT存储权限前缀
     */
    String AUTHORITY_PREFIX = "ROLE_";

    /**
     * JWT存储权限属性
     */
    String AUTHORITY_CLAIM_NAME = "authorities";

    /**
     * 后台管理client_id
     */
    String ADMIN_CLIENT_ID = "ai-admin";

    String ADMIN_CLIENT_SECRET = "RUCLAB#2020";

    /**
     * 敏感词client_id
     */
    String SENSITIVE_CLIENT_ID = "ai-sensitive";

    String SENSITIVE_CLIENT_SECRET = "RUCLAB#2020";

    /**
     * 问答系统client_id
     */
    String QA_CLIENT_ID = "ai-qa";
    String CORPUS_CLIENT_ID = "CORPUS_CLIENT_ID";
    String CORPUS_CLIENT_SECRET = "CORPUS_CLIENT_SECRET";

    String QA_CLIENT_SECRET = "RUCLAB#2022";

    /**
     * 前台client_id
     */
    String PORTAL_CLIENT_ID = "portal-app";


    String SYS_URL_PATTERN = "/" + AppConstant.APPLICATION_SYSTEM_NAME + "/**";
    String USER_URL_PATTERN = "/" + AppConstant.APPLICATION_USER_NAME + "/**";
    String RESOURCE_URL_PATTERN = "/" + AppConstant.APPLICATION_OSS + "/**";
    String LETTER_URL_PATTERN = "/" + AppConstant.APPLICATION_LETTER_NAME + "/**";
    String LETTER_MODEL_URL_PATTERN = "/" + AppConstant.APPLICATION_LETTER_MODEL_NAME + "/**";
    String NER_URL_PATTERN = "/" + AppConstant.APPLICATION_NER_SPECIFIC_NAME + "/**";
    String NER_MODEL_URL_PATTERN = "/" + AppConstant.APPLICATION_NER_SPECIFIC_MODEL_NAME + "/**";
    String QA_URL_PATTERN = "/" + AppConstant.APPLICATION_QA_NAME + "/**";
    String API_URL_PATTERN = "/" + AppConstant.APPLICATION_API_NAME + "/**";
    String FACE_URL_PATTERN = "/" + AppConstant.APPLICATION_CV_FACE + "/**";
    String OCR_URL_PATTERN = "/" +AppConstant.APPLICATION_CV_OCR + "/**";
    String STRUCTURE_URL_PATTERN = "/" + AppConstant.APPLICATION_CV_STRUCTURE + "/**";
    String IMAGE_SIM_URL_PATTERN = "/" + AppConstant.APPLICATION_CV_SIM + "/**";
    String IE_URL_PATTERN = "/" + AppConstant.APPLICATION_NLP_IE + "/**";
    String NLP_SIM_PATTERN = "/" + AppConstant.APPLICATION_NLP_SIM + "/**";
    String NLP_RE_PATTERN = "/" + AppConstant.APPLICATION_NLP_RE + "/**";
    String IMAGE_CLS_URL_PATTERN = "/" + AppConstant.APPLICATION_CV_CLS + "/**";
    String NLP_BASIC_URL_PATTERN = "/" + AppConstant.APPLICATION_NLP_BASIC + "/**";

    String SENSITIVE_URL_PATTERN = "/" + AppConstant.APPLICATION_SENSITIVE_NAME + "/**";

    String QA_APP_URL_PATTERN = "/" + AppConstant.APPLICATION_QA_APP_NAME + "/**";
    String QA_DOC_URL_PATTERN = "/" + AppConstant.APPLICATION_DOC_QA_NAME + "/**";

    /**
     * 后台接口数组
     */
    String[] ADMIN_URL_PATTERNS = new String[] { SYS_URL_PATTERN, USER_URL_PATTERN, RESOURCE_URL_PATTERN,
            LETTER_URL_PATTERN, LETTER_MODEL_URL_PATTERN, NER_URL_PATTERN, NER_MODEL_URL_PATTERN, API_URL_PATTERN,
            SENSITIVE_URL_PATTERN, FACE_URL_PATTERN, OCR_URL_PATTERN, STRUCTURE_URL_PATTERN, QA_URL_PATTERN,
            IMAGE_SIM_URL_PATTERN, IE_URL_PATTERN, NLP_SIM_PATTERN, IMAGE_CLS_URL_PATTERN, NLP_BASIC_URL_PATTERN,NLP_RE_PATTERN
            ,QA_DOC_URL_PATTERN
    };

    /**
     * 敏感词接口数组
     */
    String[] SENSITIVE_URL_PATTERNS = new String[] {
            SENSITIVE_URL_PATTERN
    };

    /**
     * QA接口数组
     */
    String[] QA_APP_URL_PATTERNS = new String[] {
            QA_APP_URL_PATTERN
    };

    /**
     * Redis缓存权限规则key
     */
    String RESOURCE_ROLES_MAP_KEY = "ai:auth:resourceRolesMap";

    /**
     * 认证信息Http请求头
     */
    String JWT_TOKEN_HEADER = "Authorization";

    /**
     * JWT令牌前缀
     */
    String JWT_TOKEN_PREFIX = "Bearer ";

    /**
     * 用户信息Http请求头
     */
    String USER_TOKEN_HEADER = "user";

    /**
	 * 认证请求头前缀
	 */
	String BASIC_HEADER_PREFIX = "Basic ";

    /**
     * appid
     */
    String APP_HEADER_NAME = "X-APP-ID";

	/**
	 * 认证请求头前缀
	 */
	String BASIC_HEADER_PREFIX_EXT = "Basic%20";
    
    String CAPTCHA_KEY = "ai:auth::captcha:";

    public final static String CAPTCHA_HEADER_KEY = "Captcha-Key";
	public final static String CAPTCHA_HEADER_CODE = "Captcha-Code";
    public final static String CAPTCHA_NOT_CORRECT = "验证码不正确";
    public final static String USER_TYPE_HEADER_KEY = "User-Type";
    public final static String DEFAULT_USER_TYPE = "admin";
    
    String USER_ID = "user_id";
    String ACCOUNT = "account";
    String ROLE_ID = "role_id";
	String ROLES = "roles";
    String USER_NAME = "user_name";
    String ROLE_NAME = "role_name";
    String CLIENT_ID = "client_id";
    String DEFAULT_AVATAR = "https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png";
    String EXPIRES_IN = "expires_in";
    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";
    String TOKEN_TYPE = "token_type";
    String NAME = "name";
    String EMAIL = "email";
    String AVATAR = "avatar";
    String CLIENT_AUTH_HEADER_KEY = "CLIENT_AUTH";
    String TENANT_ID = "tenant_id";
    String TENANT_NAME = "tenant_name";
}