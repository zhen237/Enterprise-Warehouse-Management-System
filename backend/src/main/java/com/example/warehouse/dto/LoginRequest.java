package com.example.warehouse.dto;

import lombok.Data;

/**
 * 登录请求DTO
 * 前端提交登录表单时使用的数据传输对象
 */
@Data
public class LoginRequest {
    private String username; // 登录用户名
    private String password; // 登录密码
}
