package com.example.warehouse.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Long id;
    private String username;
    private String name;
    private String role;
}