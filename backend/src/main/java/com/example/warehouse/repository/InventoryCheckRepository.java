package com.example.warehouse.repository;

import com.example.warehouse.entity.InventoryCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryCheckRepository extends JpaRepository<InventoryCheck, Long> {
    Optional<InventoryCheck> findByCheckNo(String checkNo);
    List<InventoryCheck> findByWarehouseId(Long warehouseId);
    List<InventoryCheck> findByCheckTimeBetween(LocalDateTime start, LocalDateTime end);
}