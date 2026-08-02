package com.example.warehouse.service;

import com.example.warehouse.dto.InboundRequest;
import com.example.warehouse.dto.InventoryCheckRequest;
import com.example.warehouse.dto.OutboundRequest;
import com.example.warehouse.entity.InboundRecord;
import com.example.warehouse.entity.Inventory;
import com.example.warehouse.entity.InventoryCheck;
import com.example.warehouse.entity.OutboundRecord;
import java.util.List;

/**
 * 出入库盘点服务接口
 * 定义入库、出库、盘点、库存查询等业务操作
 * 所有写操作采用两步确认制：申请时创建记录，确认时才真正变更库存
 */
public interface InventoryService {
    /**
     * 申请入库
     * 创建入库记录（未确认），不实际增加库存
     *
     * @param request    入库请求
     * @param operatorId 操作员ID
     * @return 创建的入库记录
     */
    InboundRecord inbound(InboundRequest request, Long operatorId);

    /**
     * 确认入库
     * 确认入库记录并实际增加库存（若库存已存在则累加，否则新建）
     *
     * @param id 入库记录ID
     */
    void confirmInbound(Long id);

    /**
     * 查询所有入库记录
     *
     * @return 入库记录列表
     */
    List<InboundRecord> getInboundRecords();
    
    /**
     * 申请出库
     * 创建出库记录（未确认），校验库存是否充足但不实际扣减
     *
     * @param request    出库请求
     * @param operatorId 操作员ID
     * @return 创建的出库记录
     */
    OutboundRecord outbound(OutboundRequest request, Long operatorId);

    /**
     * 确认出库
     * 确认出库记录并实际扣减库存
     *
     * @param id 出库记录ID
     */
    void confirmOutbound(Long id);

    /**
     * 查询所有出库记录
     *
     * @return 出库记录列表
     */
    List<OutboundRecord> getOutboundRecords();
    
    /**
     * 查询库存列表
     *
     * @param warehouseId 仓库ID（可选，为空则查询所有仓库库存）
     * @return 库存列表
     */
    List<Inventory> getInventory(Long warehouseId);

    /**
     * 查询指定商品在指定仓库的库存
     *
     * @param productId   商品ID
     * @param warehouseId 仓库ID
     * @return 库存记录
     */
    Inventory getInventory(Long productId, Long warehouseId);
    
    /**
     * 申请盘点
     * 创建盘点记录（未确认），计算系统数量与实际数量的差异
     *
     * @param request    盘点请求
     * @param operatorId 操作员ID
     * @return 创建的盘点记录
     */
    InventoryCheck checkInventory(InventoryCheckRequest request, Long operatorId);

    /**
     * 确认盘点
     * 确认盘点记录并以实际数量覆盖系统库存
     *
     * @param id 盘点记录ID
     */
    void confirmCheck(Long id);

    /**
     * 查询所有盘点记录
     *
     * @return 盘点记录列表
     */
    List<InventoryCheck> getCheckRecords();
}
