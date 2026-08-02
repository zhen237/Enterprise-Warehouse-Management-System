package com.example.warehouse.service;

import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.dto.UserRequest;
import com.example.warehouse.dto.ChangePasswordRequest;
import java.util.List;

/**
 * 用户管理服务接口
 * 定义用户管理的业务操作：增删改查、修改密码、启用/禁用
 */
public interface UserService {
    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    List<UserDTO> findAll();
    
    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserDTO findById(Long id);
    
    /**
     * 创建新用户
     *
     * @param request 用户创建请求
     * @return 创建后的用户信息
     */
    UserDTO createUser(UserRequest request);
    
    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param request 用户更新请求
     * @return 更新后的用户信息
     */
    UserDTO updateUser(Long id, UserRequest request);
    
    /**
     * 删除用户（物理删除）
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 启用/禁用用户
     *
     * @param id 用户ID
     * @param enabled 是否启用
     */
    void toggleUserStatus(Long id, boolean enabled);
    
    /**
     * 修改密码
     *
     * @param username 用户名
     * @param request 修改密码请求
     */
    void changePassword(String username, ChangePasswordRequest request);
}
