package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 入库记录实体类
 * 对应 inbound_records 表，记录每一笔商品入库的详细信息
 * 采用两步确认制：创建时 confirmed=false，确认后才真正增加库存
 */
@Entity
@Table(name = "inbound_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InboundRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键ID，自增
    
    @Column(unique = true, nullable = false)
    private String inboundNo; // 入库单号，唯一标识一次入库
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 入库商品
    
    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse; // 入库仓库
    
    @Column(nullable = false)
    private Integer quantity; // 入库数量
    
    private Double unitPrice; // 入库单价
    
    @Column(nullable = false)
    private LocalDateTime inboundTime; // 入库时间
    
    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator; // 经办人
    
    private String remark; // 备注
    
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean confirmed = false; // 是否已确认（确认后才真正增加库存）
}
