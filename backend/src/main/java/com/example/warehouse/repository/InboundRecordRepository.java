package com.example.warehouse.repository;

import com.example.warehouse.entity.InboundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InboundRecordRepository extends JpaRepository<InboundRecord, Long> {
    Optional<InboundRecord> findByInboundNo(String inboundNo);
    List<InboundRecord> findByWarehouseId(Long warehouseId);
    List<InboundRecord> findByInboundTimeBetween(LocalDateTime start, LocalDateTime end);
    List<InboundRecord> findByConfirmed(Boolean confirmed);
}