package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 盘点记录实体类
 * 对应 inventory_check 表，记录仓库盘点时系统数量与实际数量的对比结果
 * 采用两步确认制：盘点确认后以实际数量覆盖系统库存
 */
@Entity
@Table(name = "inventory_check")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键ID，自增
    
    @Column(unique = true, nullable = false)
    private String checkNo; // 盘点单号，唯一标识一次盘点
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 盘点的商品
    
    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse; // 盘点的仓库
    
    @Column(nullable = false)
    private Integer systemQuantity; // 系统记录数量
    
    @Column(nullable = false)
    private Integer actualQuantity; // 实际盘点数量
    
    private Integer difference; // 差异数量 = actualQuantity - systemQuantity
    
    @Column(nullable = false)
    private LocalDateTime checkTime; // 盘点时间
    
    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator; // 盘点经办人
    
    private String remark; // 备注
    
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean confirmed = false; // 是否已确认（确认后以实际数量覆盖库存）
}
