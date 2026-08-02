package com.example.warehouse.controller;

import com.example.warehouse.dto.ApiResponse;
import com.example.warehouse.dto.WarehouseStatistics;
import com.example.warehouse.entity.Warehouse;
import com.example.warehouse.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 仓库管理控制器
 * 提供仓库的增删改查及统计接口
 *
 * @api GET    /api/warehouses               获取所有仓库列表
 * @api GET    /api/warehouses/statistics    获取仓库统计信息（品种数、总价值）
 * @api GET    /api/warehouses/{id}          获取仓库详情
 * @api POST   /api/warehouses               新增仓库
 * @api PUT    /api/warehouses/{id}          修改仓库信息
 * @api DELETE /api/warehouses/{id}          删除仓库
 */
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    
    private final WarehouseService warehouseService;
    
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }
    
    /**
     * 获取所有仓库列表
     *
     * @return 启用状态的仓库列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Warehouse>>> getAll() {
        List<Warehouse> warehouses = warehouseService.findAll();
        return ResponseEntity.ok(ApiResponse.success(warehouses));
    }
    
    /**
     * 获取所有仓库的统计信息
     * 统计包含每个仓库的库存商品种类数和总价值
     *
     * @return 仓库统计信息列表
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<List<WarehouseStatistics>>> getAllWithStatistics() {
        List<WarehouseStatistics> statistics = warehouseService.findAllWithStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    /**
     * 根据ID获取仓库详情
     *
     * @param id 仓库ID
     * @return 仓库详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> getById(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(warehouse));
    }
    
    /**
     * 新增仓库
     *
     * @param warehouse 仓库实体（需包含唯一的warehouseCode）
     * @return 创建的仓库
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Warehouse>> create(@RequestBody Warehouse warehouse) {
        Warehouse saved = warehouseService.save(warehouse);
        return ResponseEntity.ok(ApiResponse.success("创建成功", saved));
    }
    
    /**
     * 修改仓库信息
     * 仅更新仓库名称、地址、负责人等字段
     *
     * @param id        仓库ID
     * @param warehouse 包含新信息的仓库实体
     * @return 更新后的仓库
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> update(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        Warehouse existing = warehouseService.findById(id);
        existing.setWarehouseName(warehouse.getWarehouseName());
        existing.setLocation(warehouse.getLocation());
        existing.setManager(warehouse.getManager());
        Warehouse saved = warehouseService.save(existing);
        return ResponseEntity.ok(ApiResponse.success("更新成功", saved));
    }
    
    /**
     * 软删除仓库
     * 将仓库的enabled字段置为false
     *
     * @param id 仓库ID
     * @return 空数据响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        warehouseService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}
