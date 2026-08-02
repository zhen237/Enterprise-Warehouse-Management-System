package com.example.warehouse.repository;

import com.example.warehouse.entity.InboundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 入库记录数据访问接口
 * 基于Spring Data JPA实现入库记录的持久化操作
 */
@Repository
public interface InboundRecordRepository extends JpaRepository<InboundRecord, Long> {
    /**
     * 根据入库单号查询入库记录
     *
     * @param inboundNo 入库单号
     * @return 入库记录（可能为空）
     */
    Optional<InboundRecord> findByInboundNo(String inboundNo);

    /**
     * 根据仓库ID查询入库记录列表
     *
     * @param warehouseId 仓库ID
     * @return 该仓库的所有入库记录
     */
    List<InboundRecord> findByWarehouseId(Long warehouseId);

    /**
     * 根据入库时间区间查询入库记录
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间区间内的入库记录列表
     */
    List<InboundRecord> findByInboundTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据确认状态查询入库记录
     *
     * @param confirmed 是否已确认
     * @return 指定确认状态的入库记录列表
     */
    List<InboundRecord> findByConfirmed(Boolean confirmed);
}
