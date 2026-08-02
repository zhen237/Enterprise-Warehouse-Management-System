package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 库存实体类
 * 对应 inventory 表，记录商品在各仓库中的当前库存数量及上下限
 * 唯一约束：product + warehouse 组合（同一商品在同一仓库只有一条库存记录）
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键ID，自增
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 库存对应的商品
    
    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse; // 库存所在仓库
    
    @Column(nullable = false)
    private Integer quantity; // 当前库存数量
    
    private Integer minStock; // 库存下限（低于该值预警）
    
    private Integer maxStock; // 库存上限（高于该值预警）
}
