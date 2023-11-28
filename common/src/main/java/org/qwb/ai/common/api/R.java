package org.qwb.ai.common.api;

import java.io.Serializable;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.qwb.ai.common.constant.MessageConstant;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private long code;
    private String message;
    private boolean success;
    private T data;

    private R(ResultCode resultCode){
        this(resultCode, resultCode.getMessage(), null);
    }

    private R(ResultCode resultCode, String message) {
		this(resultCode, message, null);
	}

	private R(ResultCode resultCode, T data) {
		this(resultCode, resultCode.getMessage(), null);
	}

    private R(ResultCode resultCode, String message, T data){
        this(resultCode.getCode(), message, data);
    }

    protected R(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = ResultCode.SUCCESS.getCode() == code;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> R<T> data(T data) {
        return data(data, ResultCode.SUCCESS.getMessage());
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static  <T> R<T> data(T data, String message) {
        return data(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 数据返回结果
     * @param <T>
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static <T> R<T> data(long code, String message, T data){
        return new R<>(code, message, data);
    }
    
    /**
     * 成功返回结果
     *
     * @param message 获取的数据
     */
    public static <T> R<T> success(String message) {
        return new R<>(ResultCode.SUCCESS, message);
    }

    public static <T> R<T> success() {
        return success(ResultCode.SUCCESS.getMessage());
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     */
    public static  <T> R<T> failed(IErrorCode errorCode) {
        return new R<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static  <T> R<T> failed(String message) {
        return new R<>(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static  <T> R<T> failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static  <T> R<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     * @param message 提示信息
     */
    public static  <T> R<T> validateFailed(String message) {
        return new R<>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static  <T> R<T> unauthorized(T data) {
        return new R<>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     */
    public static  <T> R<T> forbidden(T data) {
        return new R<>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

    public static <T> R<T> status(boolean flag) {
        return flag ? success(MessageConstant.DEFAULT_SUCCESS_MESSAGE) : failed(MessageConstant.DEFAULT_FAILURE_MESSAGE);
    }
    
}