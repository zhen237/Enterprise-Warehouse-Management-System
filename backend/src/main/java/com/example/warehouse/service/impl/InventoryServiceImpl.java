package com.example.warehouse.service.impl;

import com.example.warehouse.dto.InboundRequest;
import com.example.warehouse.dto.InventoryCheckRequest;
import com.example.warehouse.dto.OutboundRequest;
import com.example.warehouse.entity.*;
import com.example.warehouse.exception.WarehouseException;
import com.example.warehouse.repository.*;
import com.example.warehouse.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {
    
    private final InboundRecordRepository inboundRecordRepository;
    private final OutboundRecordRepository outboundRecordRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryCheckRepository inventoryCheckRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    
    public InventoryServiceImpl(InboundRecordRepository inboundRecordRepository,
                                OutboundRecordRepository outboundRecordRepository,
                                InventoryRepository inventoryRepository,
                                InventoryCheckRepository inventoryCheckRepository,
                                ProductRepository productRepository,
                                WarehouseRepository warehouseRepository,
                                UserRepository userRepository) {
        this.inboundRecordRepository = inboundRecordRepository;
        this.outboundRecordRepository = outboundRecordRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryCheckRepository = inventoryCheckRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    @Transactional
    public InboundRecord inbound(InboundRequest request, Long operatorId) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new WarehouseException("商品不存在"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new WarehouseException("仓库不存在"));
        User operator = userRepository.findById(operatorId).orElse(null);
        
        String inboundNo = generateInboundNo();
        
        InboundRecord record = new InboundRecord();
        record.setInboundNo(inboundNo);
        record.setProduct(product);
        record.setWarehouse(warehouse);
        record.setQuantity(request.getQuantity());
        record.setUnitPrice(request.getUnitPrice());
        record.setInboundTime(LocalDateTime.now());
        record.setOperator(operator);
        record.setRemark(request.getRemark());
        record.setConfirmed(false);
        
        return inboundRecordRepository.save(record);
    }
    
    @Override
    @Transactional
    public void confirmInbound(Long id) {
        InboundRecord record = inboundRecordRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("入库记录不存在"));
        
        if (record.getConfirmed()) {
            throw new WarehouseException("该记录已确认");
        }
        
        Optional<Inventory> existingInv = inventoryRepository
            .findByProductIdAndWarehouseId(record.getProduct().getId(), record.getWarehouse().getId());
        
        Inventory inventory;
        if (existingInv.isPresent()) {
            inventory = existingInv.get();
            inventory.setQuantity(inventory.getQuantity() + record.getQuantity());
        } else {
            inventory = new Inventory();
            inventory.setProduct(record.getProduct());
            inventory.setWarehouse(record.getWarehouse());
            inventory.setQuantity(record.getQuantity());
        }
        
        inventoryRepository.save(inventory);
        record.setConfirmed(true);
        inboundRecordRepository.save(record);
    }
    
    @Override
    public List<InboundRecord> getInboundRecords() {
        return inboundRecordRepository.findAll();
    }
    
    @Override
    @Transactional
    public OutboundRecord outbound(OutboundRequest request, Long operatorId) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new WarehouseException("商品不存在"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new WarehouseException("仓库不存在"));
        User operator = userRepository.findById(operatorId).orElse(null);
        
        Optional<Inventory> existingInv = inventoryRepository
            .findByProductIdAndWarehouseId(request.getProductId(), request.getWarehouseId());
        
        if (existingInv.isEmpty()) {
            throw new WarehouseException("库存不存在");
        }
        
        Inventory inventory = existingInv.get();
        if (inventory.getQuantity() < request.getQuantity()) {
            throw new WarehouseException("库存不足");
        }
        
        String outboundNo = generateOutboundNo();
        
        OutboundRecord record = new OutboundRecord();
        record.setOutboundNo(outboundNo);
        record.setProduct(product);
        record.setWarehouse(warehouse);
        record.setQuantity(request.getQuantity());
        record.setOutboundTime(LocalDateTime.now());
        record.setOperator(operator);
        record.setRemark(request.getRemark());
        record.setConfirmed(false);
        
        return outboundRecordRepository.save(record);
    }
    
    @Override
    @Transactional
    public void confirmOutbound(Long id) {
        OutboundRecord record = outboundRecordRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("出库记录不存在"));
        
        if (record.getConfirmed()) {
            throw new WarehouseException("该记录已确认");
        }
        
        Inventory inventory = inventoryRepository
            .findByProductIdAndWarehouseId(record.getProduct().getId(), record.getWarehouse().getId())
            .orElseThrow(() -> new WarehouseException("库存不存在"));
        
        if (inventory.getQuantity() < record.getQuantity()) {
            throw new WarehouseException("库存不足");
        }
        
        inventory.setQuantity(inventory.getQuantity() - record.getQuantity());
        inventoryRepository.save(inventory);
        record.setConfirmed(true);
        outboundRecordRepository.save(record);
    }
    
    @Override
    public List<OutboundRecord> getOutboundRecords() {
        return outboundRecordRepository.findAll();
    }
    
    @Override
    public List<Inventory> getInventory(Long warehouseId) {
        if (warehouseId == null) {
            return inventoryRepository.findAll();
        }
        return inventoryRepository.findByWarehouseId(warehouseId);
    }
    
    @Override
    public Inventory getInventory(Long productId, Long warehouseId) {
        return inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
            .orElseThrow(() -> new WarehouseException("库存不存在"));
    }
    
    @Override
    @Transactional
    public InventoryCheck checkInventory(InventoryCheckRequest request, Long operatorId) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new WarehouseException("商品不存在"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new WarehouseException("仓库不存在"));
        User operator = userRepository.findById(operatorId).orElse(null);
        
        Inventory inventory = inventoryRepository
            .findByProductIdAndWarehouseId(request.getProductId(), request.getWarehouseId())
            .orElse(null);
        
        int systemQuantity = inventory != null ? inventory.getQuantity() : 0;
        int difference = request.getActualQuantity() - systemQuantity;
        
        String checkNo = generateCheckNo();
        
        InventoryCheck check = new InventoryCheck();
        check.setCheckNo(checkNo);
        check.setProduct(product);
        check.setWarehouse(warehouse);
        check.setSystemQuantity(systemQuantity);
        check.setActualQuantity(request.getActualQuantity());
        check.setDifference(difference);
        check.setCheckTime(LocalDateTime.now());
        check.setOperator(operator);
        check.setRemark(request.getRemark());
        check.setConfirmed(false);
        
        return inventoryCheckRepository.save(check);
    }
    
    @Override
    @Transactional
    public void confirmCheck(Long id) {
        InventoryCheck check = inventoryCheckRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("盘点记录不存在"));
        
        if (check.getConfirmed()) {
            throw new WarehouseException("该记录已确认");
        }
        
        Optional<Inventory> existingInv = inventoryRepository
            .findByProductIdAndWarehouseId(check.getProduct().getId(), check.getWarehouse().getId());
        
        Inventory inventory;
        if (existingInv.isPresent()) {
            inventory = existingInv.get();
        } else {
            inventory = new Inventory();
            inventory.setProduct(check.getProduct());
            inventory.setWarehouse(check.getWarehouse());
        }
        
        inventory.setQuantity(check.getActualQuantity());
        inventoryRepository.save(inventory);
        check.setConfirmed(true);
        inventoryCheckRepository.save(check);
    }
    
    @Override
    public List<InventoryCheck> getCheckRecords() {
        return inventoryCheckRepository.findAll();
    }
    
    private String generateInboundNo() {
        return "RK" + System.currentTimeMillis();
    }
    
    private String generateOutboundNo() {
        return "CK" + System.currentTimeMillis();
    }
    
    private String generateCheckNo() {
        return "PD" + System.currentTimeMillis();
    }
}