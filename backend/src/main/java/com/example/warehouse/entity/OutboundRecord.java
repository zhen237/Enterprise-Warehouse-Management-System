package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbound_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutboundRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String outboundNo;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private LocalDateTime outboundTime;
    
    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator;
    
    private String remark;
    
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean confirmed = false;
}