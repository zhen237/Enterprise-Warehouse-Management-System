package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户实体类
 * 对应 users 表，记录系统登录用户的账号信息
 * 角色：ADMIN（管理员）、OPERATOR（操作员）、EMPLOYEE（普通员工）
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键ID，自增
    
    @Column(unique = true, nullable = false)
    private String username; // 登录用户名，唯一
    
    @Column(nullable = false)
    private String password; // 登录密码（BCrypt加密存储）
    
    @Column(nullable = false)
    private String role; // 角色（ADMIN / OPERATOR 等）
    
    @Column(nullable = false)
    private String name; // 真实姓名
    
    private String phone; // 联系电话
    
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean enabled = true; // 是否启用（禁用后无法登录）
}
