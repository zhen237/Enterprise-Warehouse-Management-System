package com.example.warehouse.dto;

import lombok.Data;

/**
 * 用户信息 DTO
 * 用于用户管理模块的增删改查操作
 */
@Data
public class UserDTO {
    private Long id; // 用户ID
    private String username; // 登录用户名
    private String name; // 真实姓名
    private String role; // 角色（ADMIN / OPERATOR / EMPLOYEE）
    private String phone; // 联系电话
    private Boolean enabled; // 是否启用
}
