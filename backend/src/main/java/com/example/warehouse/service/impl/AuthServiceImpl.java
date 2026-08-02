package com.example.warehouse.service.impl;

import com.example.warehouse.dto.LoginRequest;
import com.example.warehouse.dto.LoginResponse;
import com.example.warehouse.entity.User;
import com.example.warehouse.exception.WarehouseException;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 * 实现用户登录的业务逻辑：校验用户名是否存在、是否启用、密码是否匹配
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 用户登录
     * 按顺序校验：用户名存在性 → 启用状态 → 密码匹配
     *
     * @param request 登录请求（包含用户名和密码）
     * @return 登录成功返回用户基本信息
     * @throws WarehouseException 用户名不存在、用户已禁用或密码错误时抛出
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new WarehouseException("用户名不存在"));
        
        if (!user.getEnabled()) {
            throw new WarehouseException("用户已被禁用");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WarehouseException("密码错误");
        }
        
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setRole(user.getRole());
        return response;
    }
}
