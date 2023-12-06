package org.qwb.ai.common.config.db.mysql;


import org.qwb.ai.common.constant.AppConstant;

public class DBContextHolder {

    // 当前数据源
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();


    //改变当前数据源
    public static void setDB(String tenantId) {
        contextHolder.set(AppConstant.TENANT_DB_PREFIX + tenantId);
    }

    //获取当前数据源
    public static String getDB() {
        return contextHolder.get();
    }

    public static void clearDB() {
        contextHolder.remove();
    }
}
