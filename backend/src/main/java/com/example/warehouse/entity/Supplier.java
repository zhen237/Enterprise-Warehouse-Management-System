package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 供应商实体类
 * 对应 suppliers 表，记录商品供应商的基本联系信息
 */
@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键ID，自增
    
    @Column(unique = true, nullable = false)
    private String supplierCode; // 供应商编码，唯一
    
    @Column(nullable = false)
    private String supplierName; // 供应商名称
    
    private String contact; // 联系人
    
    private String phone; // 联系电话
    
    private String address; // 地址
    
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean enabled = true; // 是否启用（逻辑删除标志）
}
