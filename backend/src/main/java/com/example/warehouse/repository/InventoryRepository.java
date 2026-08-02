package com.example.warehouse.repository;

import com.example.warehouse.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 库存数据访问接口
 * 基于Spring Data JPA实现库存的持久化操作，同时提供自定义统计查询
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    /**
     * 根据商品ID和仓库ID查询库存
     *
     * @param productId   商品ID
     * @param warehouseId 仓库ID
     * @return 库存记录（可能为空）
     */
    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    /**
     * 根据仓库ID查询该仓库的所有库存
     *
     * @param warehouseId 仓库ID
     * @return 该仓库的库存列表
     */
    List<Inventory> findByWarehouseId(Long warehouseId);

    /**
     * 查询库存数量低于指定值的记录
     *
     * @param quantity 库存数量阈值
     * @return 低于阈值的库存列表
     */
    List<Inventory> findByQuantityLessThan(Integer quantity);
    
    /**
     * 计算指定仓库的库存总价值（库存数量 × 商品单价 之和）
     *
     * @param warehouseId 仓库ID
     * @return 库存总价值，若无记录返回0
     */
    @Query("SELECT COALESCE(SUM(i.quantity * p.price), 0) FROM Inventory i " +
           "JOIN i.product p WHERE i.warehouse.id = :warehouseId")
    Double getTotalValueByWarehouseId(@Param("warehouseId") Long warehouseId);
    
    /**
     * 统计指定仓库的库存记录数（即商品种类数）
     *
     * @param warehouseId 仓库ID
     * @return 库存记录数
     */
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.warehouse.id = :warehouseId")
    Long countByWarehouseId(@Param("warehouseId") Long warehouseId);
}
