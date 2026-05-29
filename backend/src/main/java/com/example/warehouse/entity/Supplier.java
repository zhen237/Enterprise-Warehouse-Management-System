package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String supplierCode;
    
    @Column(nullable = false)
    private String supplierName;
    
    private String contact;
    
    private String phone;
    
    private String address;
    
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean enabled = true;
}