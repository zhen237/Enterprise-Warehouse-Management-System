package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 出库记录实体类
 * 对应 outbound_records 表，记录每一笔商品出库的详细信息
 * 采用两步确认制：创建时 confirmed=false，确认后才真正扣减库存
 */
@Entity
@Table(name = "outbound_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutboundRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键ID，自增
    
    @Column(unique = true, nullable = false)
    private String outboundNo; // 出库单号，唯一标识一次出库
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 出库商品
    
    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse; // 出库仓库
    
    @Column(nullable = false)
    private Integer quantity; // 出库数量
    
    @Column(nullable = false)
    private LocalDateTime outboundTime; // 出库时间
    
    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator; // 经办人
    
    private String remark; // 备注
    
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean confirmed = false; // 是否已确认（确认后才真正扣减库存）
}
