package com.example.warehouse.controller;

import com.example.warehouse.dto.ApiResponse;
import com.example.warehouse.entity.Product;
import com.example.warehouse.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAll() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(@RequestBody Product product) {
        Product saved = productService.save(product);
        return ResponseEntity.ok(ApiResponse.success("创建成功", saved));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> update(@PathVariable Long id, @RequestBody Product product) {
        Product existing = productService.findById(id);
        existing.setProductName(product.getProductName());
        existing.setCategory(product.getCategory());
        existing.setUnit(product.getUnit());
        existing.setPrice(product.getPrice());
        existing.setDescription(product.getDescription());
        Product saved = productService.save(existing);
        return ResponseEntity.ok(ApiResponse.success("更新成功", saved));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> search(@RequestParam String name) {
        List<Product> products = productService.searchByName(name);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}