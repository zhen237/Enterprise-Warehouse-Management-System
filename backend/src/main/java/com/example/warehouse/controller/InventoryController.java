package com.example.warehouse.controller;

import com.example.warehouse.dto.*;
import com.example.warehouse.entity.InboundRecord;
import com.example.warehouse.entity.Inventory;
import com.example.warehouse.entity.InventoryCheck;
import com.example.warehouse.entity.OutboundRecord;
import com.example.warehouse.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @PostMapping("/inbound")
    public ResponseEntity<ApiResponse<InboundRecord>> inbound(@RequestBody InboundRequest request, 
                                                              @RequestParam Long operatorId) {
        InboundRecord record = inventoryService.inbound(request, operatorId);
        return ResponseEntity.ok(ApiResponse.success("入库申请成功", record));
    }
    
    @PostMapping("/inbound/{id}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmInbound(@PathVariable Long id) {
        inventoryService.confirmInbound(id);
        return ResponseEntity.ok(ApiResponse.success("入库确认成功", null));
    }
    
    @GetMapping("/inbound")
    public ResponseEntity<ApiResponse<List<InboundRecord>>> getInboundRecords() {
        List<InboundRecord> records = inventoryService.getInboundRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    @PostMapping("/outbound")
    public ResponseEntity<ApiResponse<OutboundRecord>> outbound(@RequestBody OutboundRequest request,
                                                                @RequestParam Long operatorId) {
        OutboundRecord record = inventoryService.outbound(request, operatorId);
        return ResponseEntity.ok(ApiResponse.success("出库申请成功", record));
    }
    
    @PostMapping("/outbound/{id}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmOutbound(@PathVariable Long id) {
        inventoryService.confirmOutbound(id);
        return ResponseEntity.ok(ApiResponse.success("出库确认成功", null));
    }
    
    @GetMapping("/outbound")
    public ResponseEntity<ApiResponse<List<OutboundRecord>>> getOutboundRecords() {
        List<OutboundRecord> records = inventoryService.getOutboundRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Inventory>>> getInventory(@RequestParam(required = false) Long warehouseId) {
        List<Inventory> inventory = inventoryService.getInventory(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }
    
    @GetMapping("/{productId}/{warehouseId}")
    public ResponseEntity<ApiResponse<Inventory>> getInventoryByProductAndWarehouse(@PathVariable Long productId,
                                                                                   @PathVariable Long warehouseId) {
        Inventory inventory = inventoryService.getInventory(productId, warehouseId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }
    
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<InventoryCheck>> checkInventory(@RequestBody InventoryCheckRequest request,
                                                                      @RequestParam Long operatorId) {
        InventoryCheck check = inventoryService.checkInventory(request, operatorId);
        return ResponseEntity.ok(ApiResponse.success("盘点申请成功", check));
    }
    
    @PostMapping("/check/{id}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmCheck(@PathVariable Long id) {
        inventoryService.confirmCheck(id);
        return ResponseEntity.ok(ApiResponse.success("盘点确认成功", null));
    }
    
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<List<InventoryCheck>>> getCheckRecords() {
        List<InventoryCheck> records = inventoryService.getCheckRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}