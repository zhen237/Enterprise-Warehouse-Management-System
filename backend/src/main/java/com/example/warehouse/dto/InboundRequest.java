package com.example.warehouse.dto;

import lombok.Data;

@Data
public class InboundRequest {
    private Long productId;
    private Long warehouseId;
    private Integer quantity;
    private Double unitPrice;
    private String remark;
}