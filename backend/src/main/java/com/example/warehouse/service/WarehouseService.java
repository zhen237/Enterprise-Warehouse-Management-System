package com.example.warehouse.service;

import com.example.warehouse.dto.WarehouseStatistics;
import com.example.warehouse.entity.Warehouse;
import java.util.List;

/**
 * 仓库服务接口
 * 定义仓库的增删改查及统计业务操作
 */
public interface WarehouseService {
    /**
     * 保存仓库（新增或更新）
     * 新增时校验仓库编码唯一性
     *
     * @param warehouse 仓库实体
     * @return 保存后的仓库
     */
    Warehouse save(Warehouse warehouse);

    /**
     * 根据ID查询仓库
     *
     * @param id 仓库ID
     * @return 仓库实体
     */
    Warehouse findById(Long id);

    /**
     * 根据仓库编码查询仓库
     *
     * @param code 仓库编码
     * @return 仓库实体
     */
    Warehouse findByCode(String code);

    /**
     * 查询所有启用状态的仓库
     *
     * @return 仓库列表
     */
    List<Warehouse> findAll();

    /**
     * 根据ID删除仓库（软删除，将enabled置为false）
     *
     * @param id 仓库ID
     */
    void deleteById(Long id);

    /**
     * 查询所有仓库及其统计信息
     * 统计信息包含商品种类数和库存总价值
     *
     * @return 仓库统计信息列表
     */
    List<WarehouseStatistics> findAllWithStatistics();
}
