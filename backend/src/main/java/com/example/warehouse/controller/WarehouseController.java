package com.example.warehouse.controller;

import com.example.warehouse.dto.ApiResponse;
import com.example.warehouse.dto.WarehouseStatistics;
import com.example.warehouse.entity.Warehouse;
import com.example.warehouse.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    
    private final WarehouseService warehouseService;
    
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Warehouse>>> getAll() {
        List<Warehouse> warehouses = warehouseService.findAll();
        return ResponseEntity.ok(ApiResponse.success(warehouses));
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<List<WarehouseStatistics>>> getAllWithStatistics() {
        List<WarehouseStatistics> statistics = warehouseService.findAllWithStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> getById(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(warehouse));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Warehouse>> create(@RequestBody Warehouse warehouse) {
        Warehouse saved = warehouseService.save(warehouse);
        return ResponseEntity.ok(ApiResponse.success("创建成功", saved));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> update(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        Warehouse existing = warehouseService.findById(id);
        existing.setWarehouseName(warehouse.getWarehouseName());
        existing.setLocation(warehouse.getLocation());
        existing.setManager(warehouse.getManager());
        Warehouse saved = warehouseService.save(existing);
        return ResponseEntity.ok(ApiResponse.success("更新成功", saved));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        warehouseService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}