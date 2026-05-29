package com.example.warehouse.service;

import com.example.warehouse.dto.LoginRequest;
import com.example.warehouse.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}