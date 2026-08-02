package com.example.warehouse.controller;

import com.example.warehouse.dto.ApiResponse;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.dto.UserRequest;
import com.example.warehouse.dto.ChangePasswordRequest;
import com.example.warehouse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 用户管理控制器
 * 处理用户相关的CRUD操作
 *
 * 权限控制：
 *   - 仅管理员 (ADMIN) 可以管理用户（增删改查、启用/禁用）
 *   - 所有用户都可以修改自己的密码
 *
 * @api GET /api/users          获取所有用户列表（管理员）
 * @api GET /api/users/{id}     获取指定用户（管理员）
 * @api POST /api/users         创建新用户（管理员）
 * @api PUT /api/users/{id}     更新用户信息（管理员）
 * @api DELETE /api/users/{id}  删除用户（管理员）
 * @api PATCH /api/users/{id}/toggle  启用/禁用用户（管理员）
 * @api PUT /api/users/password 修改密码（所有用户）
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 获取所有用户列表
     * 仅管理员可访问
     *
     * @return 用户列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(userService.findAll()));
    }
    
    /**
     * 根据ID获取用户
     * 仅管理员可访问
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.findById(id)));
    }
    
    /**
     * 创建新用户
     * 仅管理员可访问
     *
     * @param request 用户创建请求
     * @return 创建后的用户信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.createUser(request)));
    }
    
    /**
     * 更新用户信息
     * 仅管理员可访问
     *
     * @param id 用户ID
     * @param request 用户更新请求
     * @return 更新后的用户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, request)));
    }
    
    /**
     * 删除用户
     * 仅管理员可访问
     *
     * @param id 用户ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 启用/禁用用户
     * 仅管理员可访问
     *
     * @param id 用户ID
     * @param enabled 是否启用
     */
    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        userService.toggleUserStatus(id, enabled);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 修改密码
     * 所有登录用户都可以修改自己的密码
     *
     * @param request 修改密码请求
     */
    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody ChangePasswordRequest request) {
        // TODO: 从SecurityContext获取当前登录用户名
        userService.changePassword("current_user", request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
