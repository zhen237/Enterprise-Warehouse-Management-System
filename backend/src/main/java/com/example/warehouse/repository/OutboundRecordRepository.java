package com.example.warehouse.repository;

import com.example.warehouse.entity.OutboundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OutboundRecordRepository extends JpaRepository<OutboundRecord, Long> {
    Optional<OutboundRecord> findByOutboundNo(String outboundNo);
    List<OutboundRecord> findByWarehouseId(Long warehouseId);
    List<OutboundRecord> findByOutboundTimeBetween(LocalDateTime start, LocalDateTime end);
    List<OutboundRecord> findByConfirmed(Boolean confirmed);
}