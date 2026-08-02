package com.example.warehouse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Web 跨域配置类
 * 配置全局 CORS 策略，允许前端应用（不同端口/域名）访问后端 API
 *
 * 配置方式：
 *   - 本地开发：默认允许所有来源（*）
 *   - 生产环境：通过环境变量 CORS_ALLOWED_ORIGINS 指定允许的前端域名
 *     例如：CORS_ALLOWED_ORIGINS=https://warehouse-frontend.onrender.com
 *     支持多个域名，用逗号分隔
 *
 * 允许的 HTTP 方法：GET、POST、PUT、DELETE、PATCH、OPTIONS
 * 允许的请求头：所有
 * 支持携带 Cookie 凭证
 */
@Configuration
public class WebConfig {

    /**
     * 允许的源地址，从配置文件读取
     * 默认值 * 表示允许所有源（本地开发用）
     */
    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    /**
     * 配置跨域过滤器
     * - 本地开发：允许所有来源（*）
     * - 生产环境：仅允许环境变量指定的域名
     * - 允许所有 HTTP 方法（GET/POST/PUT/DELETE/PATCH/OPTIONS）
     * - 允许所有请求头
     * - 允许携带 Cookie 和认证凭证
     *
     * @return 配置好的 CorsFilter 实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 从环境变量读取允许的源地址（支持多个，用逗号分隔）
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        for (String origin : origins) {
            config.addAllowedOriginPattern(origin.trim());
        }

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
