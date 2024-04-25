package org.dingdang.common;

import lombok.Data;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/22 23:37 星期一
 * @Description: 通用API响应封装类
 */
@Data
public class ApiResponse<T> {
    private int code;           // 状态码
    private String message;     // 消息描述
    private T data;             // 返回的数据

    public ApiResponse() {
    }

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ofSuccess() {
        return new ApiResponse<>(200, "success");
    }

    public static <T> ApiResponse<T> ofSuccess(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> ofMessage(int code, String message) {
        return new ApiResponse<>(code, message);
    }


    public static <T> ApiResponse<T> ofData(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> ofData(int code, T data) {
        return new ApiResponse<>(code, "success", data);
    }

    public static <T> ApiResponse<T> ofData(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }


}