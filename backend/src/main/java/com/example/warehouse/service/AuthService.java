package com.example.warehouse.service;

import com.example.warehouse.dto.LoginRequest;
import com.example.warehouse.dto.LoginResponse;

/**
 * 认证服务接口
 * 定义用户登录相关的业务操作
 */
public interface AuthService {
    /**
     * 用户登录
     * 根据用户名密码校验用户身份，成功后返回用户信息
     *
     * @param request 登录请求（包含用户名和密码）
     * @return 登录响应（包含用户基本信息）
     */
    LoginResponse login(LoginRequest request);
}
