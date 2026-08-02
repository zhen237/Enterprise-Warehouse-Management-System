package com.example.warehouse.exception;

/**
 * 自定义业务异常类
 * 用于封装业务逻辑中的错误信息，由GlobalExceptionHandler统一处理
 * 继承RuntimeException，支持在运行时抛出并携带错误消息
 */
public class WarehouseException extends RuntimeException {
    /**
     * 构造方法
     *
     * @param message 异常消息描述
     */
    public WarehouseException(String message) {
        super(message);
    }
}
