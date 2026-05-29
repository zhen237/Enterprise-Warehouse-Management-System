package com.example.warehouse.dto;

import lombok.Data;

@Data
public class OutboundRequest {
    private Long productId;
    private Long warehouseId;
    private Integer quantity;
    private String remark;
}