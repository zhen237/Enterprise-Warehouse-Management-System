package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 仓库实体类
 * 对应 warehouses 表，记录仓库的基本信息
 */
@Entity
@Table(name = "warehouses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键ID，自增
    
    @Column(unique = true, nullable = false)
    private String warehouseCode; // 仓库编码，唯一
    
    @Column(nullable = false)
    private String warehouseName; // 仓库名称
    
    private String location; // 仓库地址
    
    private String manager; // 仓库负责人
    
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean enabled = true; // 是否启用（逻辑删除标志）
}
