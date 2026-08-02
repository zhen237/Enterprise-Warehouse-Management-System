package com.example.warehouse.repository;

import com.example.warehouse.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 供应商数据访问接口
 * 基于Spring Data JPA实现供应商的持久化操作
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    /**
     * 根据供应商编码查询供应商
     *
     * @param supplierCode 供应商编码
     * @return 供应商（可能为空）
     */
    Optional<Supplier> findBySupplierCode(String supplierCode);

    /**
     * 判断指定供应商编码是否已存在
     *
     * @param supplierCode 供应商编码
     * @return 是否存在
     */
    boolean existsBySupplierCode(String supplierCode);

    /**
     * 查询所有启用状态的供应商
     *
     * @return 启用状态的供应商列表
     */
    List<Supplier> findByEnabledTrue();
}
