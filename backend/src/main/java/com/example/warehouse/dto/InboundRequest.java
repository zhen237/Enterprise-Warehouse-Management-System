package com.example.warehouse.dto;

import lombok.Data;

/**
 * 入库请求DTO
 * 前端提交入库申请时使用的数据传输对象
 */
@Data
public class InboundRequest {
    private Long productId; // 商品ID
    private Long warehouseId; // 仓库ID
    private Integer quantity; // 入库数量
    private Double unitPrice; // 入库单价
    private String remark; // 备注
}
