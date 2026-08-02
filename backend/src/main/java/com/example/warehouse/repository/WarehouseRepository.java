package com.example.warehouse.repository;

import com.example.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 仓库数据访问接口
 * 基于Spring Data JPA实现仓库的持久化操作
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    /**
     * 根据仓库编码查询仓库
     *
     * @param warehouseCode 仓库编码
     * @return 仓库（可能为空）
     */
    Optional<Warehouse> findByWarehouseCode(String warehouseCode);

    /**
     * 判断指定仓库编码是否已存在
     *
     * @param warehouseCode 仓库编码
     * @return 是否存在
     */
    boolean existsByWarehouseCode(String warehouseCode);

    /**
     * 判断指定仓库编码是否已存在（排除指定ID的仓库）
     * 用于更新仓库时校验编码唯一性
     *
     * @param warehouseCode 仓库编码
     * @param id            仓库ID（排除自身）
     * @return 是否存在
     */
    boolean existsByWarehouseCodeAndIdNot(String warehouseCode, Long id);

    /**
     * 查询所有启用状态的仓库
     *
     * @return 启用状态的仓库列表
     */
    List<Warehouse> findByEnabledTrue();
}
