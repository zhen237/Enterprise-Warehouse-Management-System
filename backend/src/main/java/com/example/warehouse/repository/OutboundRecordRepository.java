package com.example.warehouse.repository;

import com.example.warehouse.entity.OutboundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 出库记录数据访问接口
 * 基于Spring Data JPA实现出库记录的持久化操作
 */
@Repository
public interface OutboundRecordRepository extends JpaRepository<OutboundRecord, Long> {
    /**
     * 根据出库单号查询出库记录
     *
     * @param outboundNo 出库单号
     * @return 出库记录（可能为空）
     */
    Optional<OutboundRecord> findByOutboundNo(String outboundNo);

    /**
     * 根据仓库ID查询出库记录列表
     *
     * @param warehouseId 仓库ID
     * @return 该仓库的所有出库记录
     */
    List<OutboundRecord> findByWarehouseId(Long warehouseId);

    /**
     * 根据出库时间区间查询出库记录
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间区间内的出库记录列表
     */
    List<OutboundRecord> findByOutboundTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据确认状态查询出库记录
     *
     * @param confirmed 是否已确认
     * @return 指定确认状态的出库记录列表
     */
    List<OutboundRecord> findByConfirmed(Boolean confirmed);
}
