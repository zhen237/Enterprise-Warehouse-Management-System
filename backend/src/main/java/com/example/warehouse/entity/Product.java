package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 商品实体类
 * 对应数据库 products 表，存储商品基本信息
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // 主键ID，自增

    @Column(unique = true, nullable = false)
    private String productCode;  // 商品编码，唯一不可重复（如 P001）

    @Column(nullable = false)
    private String productName;  // 商品名称（如 华为Mate 60 Pro）

    private String category;     // 商品分类（如 手机、笔记本、耳机）

    private String unit;         // 计量单位（如 台、副、块）

    private Double price;        // 商品单价（单位：元）

    private String description;  // 商品描述

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;  // 所属供应商（外键关联 suppliers 表）

    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean enabled = true;  // 是否启用（true=正常，false=已删除/停用，实现软删除）
}
