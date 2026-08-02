package com.example.warehouse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置类
 * 配置HTTP安全规则和角色权限控制
 *
 * 权限规则：
 *   - 管理员 (ADMIN)：拥有全部权限，包括增删改商品/仓库、初始化数据、查看报表
 *   - 操作员 (OPERATOR)：只能查看和进行出入库操作，不能修改商品/仓库数据
 *   - 匿名访问：登录接口、所有 GET 查询接口允许匿名访问
 *   - 写操作：需要登录认证
 *   - 管理操作：需要管理员角色
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * 配置安全过滤链
     * - 关闭CSRF（前后端分离项目使用JWT/Token时通常关闭）
     * - 设置会话为STATELESS（无状态，不创建HttpSession）
     * - 按角色配置API访问权限
     *
     * @param http HttpSecurity实例
     * @return 构建好的SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 登录接口允许所有人访问
                .requestMatchers("/api/auth/**").permitAll()
                
                // 商品管理：仅管理员可增删改，查询允许所有人
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("POST /api/products")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("PUT /api/products/**")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("DELETE /api/products/**")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("GET /api/products/**")).permitAll()
                
                // 仓库管理：仅管理员可增删改，查询允许所有人
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("POST /api/warehouses")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("PUT /api/warehouses/**")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("DELETE /api/warehouses/**")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("GET /api/warehouses/**")).permitAll()
                
                // 出入库操作：仅登录用户可申请，查询允许所有人
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("POST /api/inventory/**")).authenticated()
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("GET /api/inventory/**")).permitAll()
                
                // 数据管理：仅管理员可初始化和重置，查询允许所有人
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("GET /api/data/init")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("GET /api/data/reset")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("GET /api/data/**")).permitAll()
                
                // 数据报表：仅管理员可访问
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("GET /api/reports/**")).hasRole("ADMIN")
                
                // 用户管理：仅管理员可管理用户，修改密码需要登录
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("GET /api/users/**")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("POST /api/users")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("PUT /api/users/**")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("DELETE /api/users/**")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("PATCH /api/users/**")).hasRole("ADMIN")
                .requestMatchers(org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("PUT /api/users/password")).authenticated()
                
                // 其他请求允许匿名访问（保证基本查询功能可用）
                .anyRequest().permitAll()
            );
        return http.build();
    }
    
    /**
     * 提供密码编码器Bean
     * 使用BCrypt算法对用户密码进行加密存储
     *
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
