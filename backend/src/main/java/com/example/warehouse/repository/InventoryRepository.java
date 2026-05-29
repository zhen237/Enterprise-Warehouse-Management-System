package com.example.warehouse.repository;

import com.example.warehouse.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    List<Inventory> findByWarehouseId(Long warehouseId);
    List<Inventory> findByQuantityLessThan(Integer quantity);
    
    @Query("SELECT COALESCE(SUM(i.quantity * p.price), 0) FROM Inventory i " +
           "JOIN i.product p WHERE i.warehouse.id = :warehouseId")
    Double getTotalValueByWarehouseId(@Param("warehouseId") Long warehouseId);
    
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.warehouse.id = :warehouseId")
    Long countByWarehouseId(@Param("warehouseId") Long warehouseId);
}