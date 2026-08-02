package com.example.warehouse.controller;

import com.example.warehouse.dto.ApiResponse;
import com.example.warehouse.entity.Product;
import com.example.warehouse.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 商品管理控制器
 * 提供商品的增删改查及搜索接口
 *
 * @api GET    /api/products          获取所有商品列表
 * @api GET    /api/products/{id}     根据ID获取商品详情
 * @api POST   /api/products          新增商品（需唯一编码）
 * @api PUT    /api/products/{id}     修改商品信息
 * @api DELETE /api/products/{id}     软删除商品（enabled=false）
 * @api GET    /api/products/search   按名称模糊搜索商品
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * 获取所有商品列表
     *
     * @return 启用状态的商品列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAll() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    /**
     * 根据ID获取商品详情
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    /**
     * 新增商品
     *
     * @param product 商品实体（需包含唯一的productCode）
     * @return 创建的商品
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(@RequestBody Product product) {
        Product saved = productService.save(product);
        return ResponseEntity.ok(ApiResponse.success("创建成功", saved));
    }
    
    /**
     * 修改商品信息
     * 仅更新商品名称、分类、单位、价格、描述等字段
     *
     * @param id      商品ID
     * @param product 包含新信息的商品实体
     * @return 更新后的商品
     */
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
    
    /**
     * 软删除商品
     * 将商品的enabled字段置为false
     *
     * @param id 商品ID
     * @return 空数据响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
    
    /**
     * 按商品名称模糊搜索
     *
     * @param name 商品名称关键字
     * @return 匹配的商品列表
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> search(@RequestParam String name) {
        List<Product> products = productService.searchByName(name);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}
