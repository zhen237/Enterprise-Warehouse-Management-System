package com.example.warehouse.dto;

import lombok.Data;

/**
 * 统一API响应封装类
 * 用于所有控制器返回值的统一包装，包含状态码、消息和数据
 *
 * @param <T> 响应数据的泛型类型
 */
@Data
public class ApiResponse<T> {
    private Integer code; // 响应状态码（200成功，500服务器错误等）
    private String message; // 响应消息
    private T data; // 响应数据体
    
    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 返回成功响应（默认消息 "success"）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功的ApiResponse实例
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }
    
    /**
     * 返回成功响应（自定义消息）
     *
     * @param message 自定义消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 成功的ApiResponse实例
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }
    
    /**
     * 返回错误响应（默认500状态码）
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 错误的ApiResponse实例
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }
    
    /**
     * 返回错误响应（自定义状态码）
     *
     * @param code    自定义状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 错误的ApiResponse实例
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
