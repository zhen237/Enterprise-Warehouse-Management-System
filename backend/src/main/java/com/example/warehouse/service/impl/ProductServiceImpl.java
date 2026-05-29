package com.example.warehouse.service.impl;

import com.example.warehouse.entity.Product;
import com.example.warehouse.exception.WarehouseException;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.ProductService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public Product save(Product product) {
        if (productRepository.existsByProductCode(product.getProductCode())) {
            throw new WarehouseException("商品编码已存在");
        }
        return productRepository.save(product);
    }
    
    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("商品不存在"));
    }
    
    @Override
    public Product findByCode(String code) {
        return productRepository.findByProductCode(code)
            .orElseThrow(() -> new WarehouseException("商品不存在"));
    }
    
    @Override
    public List<Product> findAll() {
        return productRepository.findByEnabledTrue();
    }
    
    @Override
    public List<Product> searchByName(String name) {
        return productRepository.findByProductNameContaining(name);
    }
    
    @Override
    public void deleteById(Long id) {
        Product product = findById(id);
        product.setEnabled(false);
        productRepository.save(product);
    }
}