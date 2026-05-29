package com.example.warehouse.service.impl;

import com.example.warehouse.dto.WarehouseStatistics;
import com.example.warehouse.entity.Warehouse;
import com.example.warehouse.exception.WarehouseException;
import com.example.warehouse.repository.InventoryRepository;
import com.example.warehouse.repository.WarehouseRepository;
import com.example.warehouse.service.WarehouseService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    
    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, InventoryRepository inventoryRepository) {
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
    }
    
    @Override
    public Warehouse save(Warehouse warehouse) {
        if (warehouseRepository.existsByWarehouseCode(warehouse.getWarehouseCode())) {
            throw new WarehouseException("仓库编码已存在");
        }
        return warehouseRepository.save(warehouse);
    }
    
    @Override
    public Warehouse findById(Long id) {
        return warehouseRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("仓库不存在"));
    }
    
    @Override
    public Warehouse findByCode(String code) {
        return warehouseRepository.findByWarehouseCode(code)
            .orElseThrow(() -> new WarehouseException("仓库不存在"));
    }
    
    @Override
    public List<Warehouse> findAll() {
        return warehouseRepository.findByEnabledTrue();
    }
    
    @Override
    public void deleteById(Long id) {
        Warehouse warehouse = findById(id);
        warehouse.setEnabled(false);
        warehouseRepository.save(warehouse);
    }
    
    @Override
    public List<WarehouseStatistics> findAllWithStatistics() {
        List<Warehouse> warehouses = warehouseRepository.findByEnabledTrue();
        List<WarehouseStatistics> statistics = new ArrayList<>();
        
        for (Warehouse warehouse : warehouses) {
            Long itemCount = inventoryRepository.countByWarehouseId(warehouse.getId());
            Double totalValue = inventoryRepository.getTotalValueByWarehouseId(warehouse.getId());
            
            statistics.add(new WarehouseStatistics(
                warehouse.getId(),
                warehouse.getWarehouseName(),
                warehouse.getWarehouseCode(),
                warehouse.getLocation(),
                warehouse.getManager(),
                warehouse.getEnabled(),
                itemCount,
                totalValue
            ));
        }
        
        return statistics;
    }
}