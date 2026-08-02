package com.example.warehouse.service;

import com.example.warehouse.entity.Product;
import java.util.List;

/**
 * 商品服务接口
 * 定义商品的增删改查业务操作
 */
public interface ProductService {
    /**
     * 保存商品（新增或更新）
     * 新增时校验商品编码唯一性
     *
     * @param product 商品实体
     * @return 保存后的商品
     */
    Product save(Product product);

    /**
     * 根据ID查询商品
     *
     * @param id 商品ID
     * @return 商品实体
     */
    Product findById(Long id);

    /**
     * 根据商品编码查询商品
     *
     * @param code 商品编码
     * @return 商品实体
     */
    Product findByCode(String code);

    /**
     * 查询所有启用状态的商品
     *
     * @return 商品列表
     */
    List<Product> findAll();

    /**
     * 根据商品名称模糊搜索
     *
     * @param name 商品名称关键字
     * @return 匹配的商品列表
     */
    List<Product> searchByName(String name);

    /**
     * 根据ID删除商品（软删除，将enabled置为false）
     *
     * @param id 商品ID
     */
    void deleteById(Long id);
}
