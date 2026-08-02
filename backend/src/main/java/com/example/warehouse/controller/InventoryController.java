package com.example.warehouse.controller;

import com.example.warehouse.dto.ApiResponse;
import com.example.warehouse.dto.*;
import com.example.warehouse.entity.InboundRecord;
import com.example.warehouse.entity.Inventory;
import com.example.warehouse.entity.InventoryCheck;
import com.example.warehouse.entity.OutboundRecord;
import com.example.warehouse.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 出入库与盘点控制器
 * 提供入库、出库、盘点的申请/确认流程，以及库存查询接口
 *
 * 核心业务流程：
 *   入库：POST /inbound（申请）→ POST /inbound/{id}/confirm（确认）
 *   出库：POST /outbound（申请）→ POST /outbound/{id}/confirm（确认）
 *   盘点：POST /check（申请）→ POST /check/{id}/confirm（确认）
 *
 * 设计说明：
 *   - 两步确认制：申请时生成记录但不改变库存，确认时才真正变动
 *   - 事务保证：确认操作使用 @Transactional 保证数据一致性
 *   - 库存查询：支持按仓库过滤，支持单商品单仓库精确查询
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    /**
     * 申请入库
     * 创建入库记录，不实际增加库存
     *
     * @param request    入库请求体（包含productId、warehouseId、quantity、unitPrice等）
     * @param operatorId 操作员ID
     * @return 创建的入库记录
     */
    @PostMapping("/inbound")
    public ResponseEntity<ApiResponse<InboundRecord>> inbound(@RequestBody InboundRequest request, 
                                                              @RequestParam Long operatorId) {
        InboundRecord record = inventoryService.inbound(request, operatorId);
        return ResponseEntity.ok(ApiResponse.success("入库申请成功", record));
    }
    
    /**
     * 确认入库
     * 确认入库记录并实际增加库存
     *
     * @param id 入库记录ID
     * @return 空数据响应
     */
    @PostMapping("/inbound/{id}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmInbound(@PathVariable Long id) {
        inventoryService.confirmInbound(id);
        return ResponseEntity.ok(ApiResponse.success("入库确认成功", null));
    }
    
    /**
     * 查询所有入库记录
     *
     * @return 入库记录列表
     */
    @GetMapping("/inbound")
    public ResponseEntity<ApiResponse<List<InboundRecord>>> getInboundRecords() {
        List<InboundRecord> records = inventoryService.getInboundRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    /**
     * 申请出库
     * 创建出库记录，校验库存但不实际扣减
     *
     * @param request    出库请求体（包含productId、warehouseId、quantity等）
     * @param operatorId 操作员ID
     * @return 创建的出库记录
     */
    @PostMapping("/outbound")
    public ResponseEntity<ApiResponse<OutboundRecord>> outbound(@RequestBody OutboundRequest request,
                                                                @RequestParam Long operatorId) {
        OutboundRecord record = inventoryService.outbound(request, operatorId);
        return ResponseEntity.ok(ApiResponse.success("出库申请成功", record));
    }
    
    /**
     * 确认出库
     * 确认出库记录并实际扣减库存
     *
     * @param id 出库记录ID
     * @return 空数据响应
     */
    @PostMapping("/outbound/{id}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmOutbound(@PathVariable Long id) {
        inventoryService.confirmOutbound(id);
        return ResponseEntity.ok(ApiResponse.success("出库确认成功", null));
    }
    
    /**
     * 查询所有出库记录
     *
     * @return 出库记录列表
     */
    @GetMapping("/outbound")
    public ResponseEntity<ApiResponse<List<OutboundRecord>>> getOutboundRecords() {
        List<OutboundRecord> records = inventoryService.getOutboundRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    /**
     * 查询库存列表
     *
     * @param warehouseId 仓库ID（可选，为空则查询所有仓库）
     * @return 库存列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Inventory>>> getInventory(@RequestParam(required = false) Long warehouseId) {
        List<Inventory> inventory = inventoryService.getInventory(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }
    
    /**
     * 查询指定商品在指定仓库的库存
     *
     * @param productId   商品ID
     * @param warehouseId 仓库ID
     * @return 库存记录
     */
    @GetMapping("/{productId}/{warehouseId}")
    public ResponseEntity<ApiResponse<Inventory>> getInventoryByProductAndWarehouse(@PathVariable Long productId,
                                                                                   @PathVariable Long warehouseId) {
        Inventory inventory = inventoryService.getInventory(productId, warehouseId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }
    
    /**
     * 申请盘点
     * 创建盘点记录，记录系统数量与实际数量的差异
     *
     * @param request    盘点请求体（包含productId、warehouseId、actualQuantity等）
     * @param operatorId 操作员ID
     * @return 创建的盘点记录
     */
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<InventoryCheck>> checkInventory(@RequestBody InventoryCheckRequest request,
                                                                      @RequestParam Long operatorId) {
        InventoryCheck check = inventoryService.checkInventory(request, operatorId);
        return ResponseEntity.ok(ApiResponse.success("盘点申请成功", check));
    }
    
    /**
     * 确认盘点
     * 确认盘点记录并以实际数量覆盖系统库存
     *
     * @param id 盘点记录ID
     * @return 空数据响应
     */
    @PostMapping("/check/{id}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmCheck(@PathVariable Long id) {
        inventoryService.confirmCheck(id);
        return ResponseEntity.ok(ApiResponse.success("盘点确认成功", null));
    }
    
    /**
     * 查询所有盘点记录
     *
     * @return 盘点记录列表
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<List<InventoryCheck>>> getCheckRecords() {
        List<InventoryCheck> records = inventoryService.getCheckRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
