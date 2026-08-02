package com.example.warehouse.service.impl;

import com.example.warehouse.entity.Product;
import com.example.warehouse.exception.WarehouseException;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.ProductService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 商品服务实现类
 * 实现商品的增删改查业务逻辑，采用软删除（将enabled置为false）
 * 
 * 输入校验规则：
 *   - 商品编码：必填，长度1-50，唯一，不允许特殊字符
 *   - 商品名称：必填，长度1-200
 *   - 分类：可选，长度1-100
 *   - 单位：必填，长度1-20
 *   - 价格：必填，必须大于0，最多两位小数
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * 保存商品（新增或更新）
     * 新增时校验商品编码唯一性和输入合法性
     *
     * @param product 商品实体
     * @return 保存后的商品
     * @throws WarehouseException 商品编码已存在或输入不合法时抛出
     */
    @Override
    public Product save(Product product) {
        validateProduct(product);
        
        // 新增时校验商品编码唯一性（排除自身）
        boolean exists = product.getId() == null 
            ? productRepository.existsByProductCode(product.getProductCode())
            : productRepository.existsByProductCodeAndIdNot(product.getProductCode(), product.getId());
        
        if (exists) {
            throw new WarehouseException("商品编码已存在");
        }
        return productRepository.save(product);
    }
    
    /**
     * 商品输入校验
     * 校验商品编码、名称、单位、价格等字段的合法性
     *
     * @param product 商品实体
     * @throws WarehouseException 校验失败时抛出
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new WarehouseException("商品信息不能为空");
        }
        
        // 商品编码校验
        if (product.getProductCode() == null || product.getProductCode().trim().isEmpty()) {
            throw new WarehouseException("商品编码不能为空");
        }
        if (product.getProductCode().length() > 50) {
            throw new WarehouseException("商品编码长度不能超过50");
        }
        // 编码只允许字母、数字、下划线、连字符
        if (!product.getProductCode().matches("^[a-zA-Z0-9_-]+$")) {
            throw new WarehouseException("商品编码只能包含字母、数字、下划线和连字符");
        }
        
        // 商品名称校验
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new WarehouseException("商品名称不能为空");
        }
        if (product.getProductName().length() > 200) {
            throw new WarehouseException("商品名称长度不能超过200");
        }
        
        // 分类校验（可选）
        if (product.getCategory() != null && product.getCategory().length() > 100) {
            throw new WarehouseException("商品分类长度不能超过100");
        }
        
        // 单位校验
        if (product.getUnit() == null || product.getUnit().trim().isEmpty()) {
            throw new WarehouseException("计量单位不能为空");
        }
        if (product.getUnit().length() > 20) {
            throw new WarehouseException("计量单位长度不能超过20");
        }
        
        // 价格校验
        if (product.getPrice() == null) {
            throw new WarehouseException("商品价格不能为空");
        }
        if (product.getPrice() <= 0) {
            throw new WarehouseException("商品价格必须大于0");
        }
        if (product.getPrice() > 999999999.99) {
            throw new WarehouseException("商品价格不能超过999999999.99");
        }
    }
    
    /**
     * 根据ID查询商品
     *
     * @param id 商品ID
     * @return 商品实体
     * @throws WarehouseException 商品不存在时抛出
     */
    @Override
    public Product findById(Long id) {
        if (id == null || id <= 0) {
            throw new WarehouseException("无效的商品ID");
        }
        return productRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("商品不存在"));
    }
    
    /**
     * 根据商品编码查询商品
     *
     * @param code 商品编码
     * @return 商品实体
     * @throws WarehouseException 商品不存在或编码为空时抛出
     */
    @Override
    public Product findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new WarehouseException("商品编码不能为空");
        }
        return productRepository.findByProductCode(code.trim())
            .orElseThrow(() -> new WarehouseException("商品不存在"));
    }
    
    /**
     * 查询所有启用状态的商品
     *
     * @return 商品列表
     */
    @Override
    public List<Product> findAll() {
        return productRepository.findByEnabledTrue();
    }
    
    /**
     * 根据商品名称模糊搜索
     *
     * @param name 商品名称关键字
     * @return 匹配的商品列表
     */
    @Override
    public List<Product> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        return productRepository.findByProductNameContaining(name.trim());
    }
    
    /**
     * 根据ID软删除商品
     * 将商品的enabled字段置为false
     *
     * @param id 商品ID
     * @throws WarehouseException 商品不存在时抛出
     */
    @Override
    public void deleteById(Long id) {
        Product product = findById(id);
        product.setEnabled(false);
        productRepository.save(product);
    }
}
