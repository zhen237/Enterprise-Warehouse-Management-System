package com.example.warehouse.dto;

import lombok.Data;

/**
 * 创建/更新用户请求 DTO
 * 用于新增用户和修改用户信息
 */
@Data
public class UserRequest {
    private Long id; // 用户ID（更新时必填）
    private String username; // 登录用户名
    private String password; // 登录密码（新增时必填，更新时选填）
    private String name; // 真实姓名
    private String role; // 角色（ADMIN / OPERATOR / EMPLOYEE）
    private String phone; // 联系电话
    private Boolean enabled; // 是否启用
}
