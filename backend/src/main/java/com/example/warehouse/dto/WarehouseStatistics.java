package com.example.warehouse.dto;

import lombok.Data;

@Data
public class WarehouseStatistics {
    private Long warehouseId;
    private String warehouseName;
    private String warehouseCode;
    private String location;
    private String manager;
    private Boolean enabled;
    private Long totalItems;
    private Double totalValue;
    
    public WarehouseStatistics() {}
    
    public WarehouseStatistics(Long warehouseId, String warehouseName, String warehouseCode, 
                             String location, String manager, Boolean enabled,
                             Long totalItems, Double totalValue) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.warehouseCode = warehouseCode;
        this.location = location;
        this.manager = manager;
        this.enabled = enabled;
        this.totalItems = totalItems;
        this.totalValue = totalValue;
    }
}
