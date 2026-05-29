package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "warehouses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String warehouseCode;
    
    @Column(nullable = false)
    private String warehouseName;
    
    private String location;
    
    private String manager;
    
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean enabled = true;
}