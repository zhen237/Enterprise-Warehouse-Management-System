package com.example.warehouse.service.impl;

import com.example.warehouse.dto.LoginRequest;
import com.example.warehouse.dto.LoginResponse;
import com.example.warehouse.entity.User;
import com.example.warehouse.exception.WarehouseException;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
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