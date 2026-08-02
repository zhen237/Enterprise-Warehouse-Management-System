package com.example.warehouse.dto;

import lombok.Data;

/**
 * 登录响应DTO
 * 登录成功后返回给前端的用户基本信息
 */
@Data
public class LoginResponse {
    private Long id; // 用户ID
    private String username; // 登录用户名
    private String name; // 真实姓名
    private String role; // 用户角色
}
