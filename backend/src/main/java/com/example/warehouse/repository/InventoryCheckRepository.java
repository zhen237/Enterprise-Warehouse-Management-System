package com.example.warehouse.repository;

import com.example.warehouse.entity.InventoryCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 盘点记录数据访问接口
 * 基于Spring Data JPA实现盘点记录的持久化操作
 */
@Repository
public interface InventoryCheckRepository extends JpaRepository<InventoryCheck, Long> {
    /**
     * 根据盘点单号查询盘点记录
     *
     * @param checkNo 盘点单号
     * @return 盘点记录（可能为空）
     */
    Optional<InventoryCheck> findByCheckNo(String checkNo);

    /**
     * 根据仓库ID查询盘点记录列表
     *
     * @param warehouseId 仓库ID
     * @return 该仓库的所有盘点记录
     */
    List<InventoryCheck> findByWarehouseId(Long warehouseId);

    /**
     * 根据盘点时间区间查询盘点记录
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间区间内的盘点记录列表
     */
    List<InventoryCheck> findByCheckTimeBetween(LocalDateTime start, LocalDateTime end);
}
