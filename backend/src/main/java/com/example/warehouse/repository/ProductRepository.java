package com.example.warehouse.repository;

import com.example.warehouse.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 商品数据访问接口
 * 基于Spring Data JPA实现商品的持久化操作
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * 根据商品编码查询商品
     *
     * @param productCode 商品编码
     * @return 商品（可能为空）
     */
    Optional<Product> findByProductCode(String productCode);

    /**
     * 判断指定商品编码是否已存在
     *
     * @param productCode 商品编码
     * @return 是否存在
     */
    boolean existsByProductCode(String productCode);

    /**
     * 判断指定商品编码是否已存在（排除指定ID的商品）
     * 用于更新商品时校验编码唯一性
     *
     * @param productCode 商品编码
     * @param id          商品ID（排除自身）
     * @return 是否存在
     */
    boolean existsByProductCodeAndIdNot(String productCode, Long id);

    /**
     * 查询所有启用状态的商品
     *
     * @return 启用状态的商品列表
     */
    List<Product> findByEnabledTrue();

    /**
     * 根据商品名称模糊匹配查询
     *
     * @param productName 商品名称关键字
     * @return 匹配的商品列表
     */
    List<Product> findByProductNameContaining(String productName);
}
