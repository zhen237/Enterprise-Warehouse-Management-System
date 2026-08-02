package com.example.warehouse.dto;

import lombok.Data;

/**
 * 盘点请求DTO
 * 前端提交盘点申请时使用的数据传输对象
 */
@Data
public class InventoryCheckRequest {
    private Long productId; // 商品ID
    private Long warehouseId; // 仓库ID
    private Integer actualQuantity; // 实际盘点数量
    private String remark; // 备注
}
