package com.example.warehouse.service;

import com.example.warehouse.entity.Product;
import java.util.List;

public interface ProductService {
    Product save(Product product);
    Product findById(Long id);
    Product findByCode(String code);
    List<Product> findAll();
    List<Product> searchByName(String name);
    void deleteById(Long id);
}