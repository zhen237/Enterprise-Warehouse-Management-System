package com.example.warehouse.service;

import com.example.warehouse.dto.WarehouseStatistics;
import com.example.warehouse.entity.Warehouse;
import java.util.List;

public interface WarehouseService {
    Warehouse save(Warehouse warehouse);
    Warehouse findById(Long id);
    Warehouse findByCode(String code);
    List<Warehouse> findAll();
    void deleteById(Long id);
    List<WarehouseStatistics> findAllWithStatistics();
}