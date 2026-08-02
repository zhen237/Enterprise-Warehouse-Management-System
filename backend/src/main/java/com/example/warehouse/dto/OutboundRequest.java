package com.example.warehouse.dto;

import lombok.Data;

/**
 * 出库请求DTO
 * 前端提交出库申请时使用的数据传输对象
 */
@Data
public class OutboundRequest {
    private Long productId; // 商品ID
    private Long warehouseId; // 仓库ID
    private Integer quantity; // 出库数量
    private String remark; // 备注
}
