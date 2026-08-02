package com.example.warehouse.dto;

import lombok.Data;

/**
 * 修改密码请求 DTO
 * 用于用户修改自己的登录密码
 */
@Data
public class ChangePasswordRequest {
    private String oldPassword; // 旧密码
    private String newPassword; // 新密码
}
