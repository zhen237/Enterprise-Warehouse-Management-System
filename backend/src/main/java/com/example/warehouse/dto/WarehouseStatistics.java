package com.example.warehouse.dto;

import lombok.Data;

/**
 * 仓库统计信息DTO
 * 用于在仓库基本信息的基础上附加统计数据（商品总数、总价值等）
 */
@Data
public class WarehouseStatistics {
    private Long warehouseId; // 仓库ID
    private String warehouseName; // 仓库名称
    private String warehouseCode; // 仓库编码
    private String location; // 仓库地址
    private String manager; // 仓库负责人
    private Boolean enabled; // 是否启用
    private Long totalItems; // 该仓库库存商品种类数
    private Double totalValue; // 该仓库库存总价值
    
    public WarehouseStatistics() {}
    
    /**
     * 全参构造方法
     *
     * @param warehouseId   仓库ID
     * @param warehouseName 仓库名称
     * @param warehouseCode 仓库编码
     * @param location      仓库地址
     * @param manager       仓库负责人
     * @param enabled       是否启用
     * @param totalItems    库存商品种类数
     * @param totalValue    库存总价值
     */
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
