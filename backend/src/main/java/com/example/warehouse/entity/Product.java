package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String productCode;
    
    @Column(nullable = false)
    private String productName;
    
    private String category;
    
    private String unit;
    
    private Double price;
    
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean enabled = true;
}