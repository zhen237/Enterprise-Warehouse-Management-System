package com.example.warehouse.config;

import com.example.warehouse.entity.*;
import com.example.warehouse.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    
    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      ProductRepository productRepository,
                                      WarehouseRepository warehouseRepository,
                                      SupplierRepository supplierRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                admin.setName("管理员");
                admin.setPhone("13800138000");
                admin.setEnabled(true);
                userRepository.save(admin);
                
                User operator = new User();
                operator.setUsername("operator");
                operator.setPassword(passwordEncoder.encode("operator123"));
                operator.setRole("OPERATOR");
                operator.setName("操作员");
                operator.setPhone("13900139000");
                operator.setEnabled(true);
                userRepository.save(operator);
            }
            
            if (supplierRepository.count() == 0) {
                Supplier supplier1 = new Supplier();
                supplier1.setSupplierCode("S001");
                supplier1.setSupplierName("深圳电子科技有限公司");
                supplier1.setContact("张三");
                supplier1.setPhone("0755-12345678");
                supplier1.setAddress("深圳市南山区科技园");
                supplierRepository.save(supplier1);
                
                Supplier supplier2 = new Supplier();
                supplier2.setSupplierCode("S002");
                supplier2.setSupplierName("广州贸易有限公司");
                supplier2.setContact("李四");
                supplier2.setPhone("020-87654321");
                supplier2.setAddress("广州市天河区珠江新城");
                supplierRepository.save(supplier2);
            }
            
            if (productRepository.count() == 0) {
                Supplier supplier = supplierRepository.findBySupplierCode("S001").orElse(null);
                
                Product product1 = new Product();
                product1.setProductCode("P001");
                product1.setProductName("无线鼠标");
                product1.setCategory("电子产品");
                product1.setUnit("个");
                product1.setPrice(59.9);
                product1.setDescription("无线蓝牙鼠标，续航时间长");
                product1.setSupplier(supplier);
                productRepository.save(product1);
                
                Product product2 = new Product();
                product2.setProductCode("P002");
                product2.setProductName("机械键盘");
                product2.setCategory("电子产品");
                product2.setUnit("个");
                product2.setPrice(199.0);
                product2.setDescription("RGB背光机械键盘");
                product2.setSupplier(supplier);
                productRepository.save(product2);
                
                Product product3 = new Product();
                product3.setProductCode("P003");
                product3.setProductName("USB数据线");
                product3.setCategory("电子产品");
                product3.setUnit("条");
                product3.setPrice(29.9);
                product3.setDescription("Type-C数据线，1.5米");
                product3.setSupplier(supplier);
                productRepository.save(product3);
                
                Product product4 = new Product();
                product4.setProductCode("P004");
                product4.setProductName("办公笔记本");
                product4.setCategory("办公用品");
                product4.setUnit("本");
                product4.setPrice(15.0);
                product4.setDescription("A5尺寸，100页");
                product4.setSupplier(supplier);
                productRepository.save(product4);
                
                Product product5 = new Product();
                product5.setProductCode("P005");
                product5.setProductName("签字笔");
                product5.setCategory("办公用品");
                product5.setUnit("支");
                product5.setPrice(5.0);
                product5.setDescription("黑色中性笔");
                product5.setSupplier(supplier);
                productRepository.save(product5);
            }
            
            if (warehouseRepository.count() == 0) {
                Warehouse warehouse1 = new Warehouse();
                warehouse1.setWarehouseCode("WH001");
                warehouse1.setWarehouseName("A仓库");
                warehouse1.setLocation("一号厂区");
                warehouse1.setManager("王五");
                warehouseRepository.save(warehouse1);
                
                Warehouse warehouse2 = new Warehouse();
                warehouse2.setWarehouseCode("WH002");
                warehouse2.setWarehouseName("B仓库");
                warehouse2.setLocation("二号厂区");
                warehouse2.setManager("赵六");
                warehouseRepository.save(warehouse2);
            }
        };
    }
}