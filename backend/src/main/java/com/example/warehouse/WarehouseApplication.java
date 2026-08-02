package com.example.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 企业仓库管理系统启动类
 * Spring Boot 应用入口，自动扫描同包及子包下的所有组件
 *
 * @author Warehouse Management System Team
 * @version 1.0.0
 */
@SpringBootApplication
public class WarehouseApplication {
    public static void main(String[] args) {
        SpringApplication.run(WarehouseApplication.class, args);
    }
}