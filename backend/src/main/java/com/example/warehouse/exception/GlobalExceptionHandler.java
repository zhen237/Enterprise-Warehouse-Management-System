package com.example.warehouse.exception;

import com.example.warehouse.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理系统抛出的异常，转换为标准的ApiResponse响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理自定义业务异常 WarehouseException
     * 返回400 BAD_REQUEST状态码及异常消息
     *
     * @param e 捕获到的WarehouseException异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(WarehouseException.class)
    public ResponseEntity<ApiResponse<Void>> handleWarehouseException(WarehouseException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 处理未捕获的其他异常
     * 返回500 INTERNAL_SERVER_ERROR状态码及通用错误消息
     *
     * @param e 捕获到的Exception异常
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("服务器内部错误"));
    }
}
