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

/**
 * 仓库服务实现类
 * 实现仓库的增删改查及统计业务逻辑，采用软删除（将enabled置为false）
 * 
 * 输入校验规则：
 *   - 仓库编码：必填，长度1-50，唯一，不允许特殊字符
 *   - 仓库名称：必填，长度1-200
 *   - 地址：可选，长度1-500
 *   - 负责人：可选，长度1-100
 */
@Service
public class WarehouseServiceImpl implements WarehouseService {
    
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    
    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, InventoryRepository inventoryRepository) {
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
    }
    
    /**
     * 保存仓库（新增或更新）
     * 新增时校验仓库编码唯一性和输入合法性
     *
     * @param warehouse 仓库实体
     * @return 保存后的仓库
     * @throws WarehouseException 仓库编码已存在或输入不合法时抛出
     */
    @Override
    public Warehouse save(Warehouse warehouse) {
        validateWarehouse(warehouse);
        
        // 新增时校验仓库编码唯一性（排除自身）
        boolean exists = warehouse.getId() == null 
            ? warehouseRepository.existsByWarehouseCode(warehouse.getWarehouseCode())
            : warehouseRepository.existsByWarehouseCodeAndIdNot(warehouse.getWarehouseCode(), warehouse.getId());
        
        if (exists) {
            throw new WarehouseException("仓库编码已存在");
        }
        return warehouseRepository.save(warehouse);
    }
    
    /**
     * 仓库输入校验
     * 校验仓库编码、名称、地址等字段的合法性
     *
     * @param warehouse 仓库实体
     * @throws WarehouseException 校验失败时抛出
     */
    private void validateWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new WarehouseException("仓库信息不能为空");
        }
        
        // 仓库编码校验
        if (warehouse.getWarehouseCode() == null || warehouse.getWarehouseCode().trim().isEmpty()) {
            throw new WarehouseException("仓库编码不能为空");
        }
        if (warehouse.getWarehouseCode().length() > 50) {
            throw new WarehouseException("仓库编码长度不能超过50");
        }
        // 编码只允许字母、数字、下划线、连字符
        if (!warehouse.getWarehouseCode().matches("^[a-zA-Z0-9_-]+$")) {
            throw new WarehouseException("仓库编码只能包含字母、数字、下划线和连字符");
        }
        
        // 仓库名称校验
        if (warehouse.getWarehouseName() == null || warehouse.getWarehouseName().trim().isEmpty()) {
            throw new WarehouseException("仓库名称不能为空");
        }
        if (warehouse.getWarehouseName().length() > 200) {
            throw new WarehouseException("仓库名称长度不能超过200");
        }
        
        // 地址校验（可选）
        if (warehouse.getLocation() != null && warehouse.getLocation().length() > 500) {
            throw new WarehouseException("仓库地址长度不能超过500");
        }
        
        // 负责人校验（可选）
        if (warehouse.getManager() != null && warehouse.getManager().length() > 100) {
            throw new WarehouseException("负责人姓名长度不能超过100");
        }
    }
    
    /**
     * 根据ID查询仓库
     *
     * @param id 仓库ID
     * @return 仓库实体
     * @throws WarehouseException 仓库不存在时抛出
     */
    @Override
    public Warehouse findById(Long id) {
        if (id == null || id <= 0) {
            throw new WarehouseException("无效的仓库ID");
        }
        return warehouseRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("仓库不存在"));
    }
    
    /**
     * 根据仓库编码查询仓库
     *
     * @param code 仓库编码
     * @return 仓库实体
     * @throws WarehouseException 仓库不存在或编码为空时抛出
     */
    @Override
    public Warehouse findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new WarehouseException("仓库编码不能为空");
        }
        return warehouseRepository.findByWarehouseCode(code.trim())
            .orElseThrow(() -> new WarehouseException("仓库不存在"));
    }
    
    /**
     * 查询所有启用状态的仓库
     *
     * @return 仓库列表
     */
    @Override
    public List<Warehouse> findAll() {
        return warehouseRepository.findByEnabledTrue();
    }
    
    /**
     * 根据ID软删除仓库
     * 将仓库的enabled字段置为false
     *
     * @param id 仓库ID
     * @throws WarehouseException 仓库不存在时抛出
     */
    @Override
    public void deleteById(Long id) {
        Warehouse warehouse = findById(id);
        warehouse.setEnabled(false);
        warehouseRepository.save(warehouse);
    }
    
    /**
     * 查询所有仓库及其统计信息
     * 遍历每个启用仓库，查询该仓库的库存商品种类数和库存总价值
     *
     * @return 仓库统计信息列表
     */
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
