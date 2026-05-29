package com.example.warehouse.dto;

import lombok.Data;

@Data
public class InventoryCheckRequest {
    private Long productId;
    private Long warehouseId;
    private Integer actualQuantity;
    private String remark;
}