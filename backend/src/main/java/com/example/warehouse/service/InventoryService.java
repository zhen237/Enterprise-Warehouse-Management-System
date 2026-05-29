package com.example.warehouse.service;

import com.example.warehouse.dto.InboundRequest;
import com.example.warehouse.dto.InventoryCheckRequest;
import com.example.warehouse.dto.OutboundRequest;
import com.example.warehouse.entity.InboundRecord;
import com.example.warehouse.entity.Inventory;
import com.example.warehouse.entity.InventoryCheck;
import com.example.warehouse.entity.OutboundRecord;
import java.util.List;

public interface InventoryService {
    InboundRecord inbound(InboundRequest request, Long operatorId);
    void confirmInbound(Long id);
    List<InboundRecord> getInboundRecords();
    
    OutboundRecord outbound(OutboundRequest request, Long operatorId);
    void confirmOutbound(Long id);
    List<OutboundRecord> getOutboundRecords();
    
    List<Inventory> getInventory(Long warehouseId);
    Inventory getInventory(Long productId, Long warehouseId);
    
    InventoryCheck checkInventory(InventoryCheckRequest request, Long operatorId);
    void confirmCheck(Long id);
    List<InventoryCheck> getCheckRecords();
}