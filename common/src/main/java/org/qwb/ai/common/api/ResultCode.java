/*
 * @Author: liu lichao
 * @Date: 2020-11-12 15:58:58
 * @LastEditors: liu lichao
 * @LastEditTime: 2021-12-17 10:35:22
 * @Description: 常用API操作码
 */
package org.qwb.ai.common.api;

public enum ResultCode implements IErrorCode {
    
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限");
    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    
}