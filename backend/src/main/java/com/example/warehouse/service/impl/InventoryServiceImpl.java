package com.example.warehouse.service.impl;

import com.example.warehouse.dto.InboundRequest;
import com.example.warehouse.dto.InventoryCheckRequest;
import com.example.warehouse.dto.OutboundRequest;
import com.example.warehouse.entity.*;
import com.example.warehouse.exception.WarehouseException;
import com.example.warehouse.repository.*;
import com.example.warehouse.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 出入库盘点服务实现类
 * 实现商品入库、出库、盘点的全流程业务逻辑
 *
 * 核心设计：
 *   1. 两步确认制：申请时创建记录（confirmed=false），库存不变动；确认时才真正增减库存
 *   2. 事务保证：所有写操作使用 @Transactional 注解，保证数据一致性
 *   3. 单号生成：
 *      - 入库单号：RK + 时间戳（毫秒）
 *      - 出库单号：CK + 时间戳（毫秒）
 *      - 盘点单号：PD + 时间戳（毫秒）
 *
 * 业务规则：
 *   - 出库前校验库存是否充足，不足则抛出异常
 *   - 入库确认时，若库存已存在则累加，否则新建库存记录
 *   - 盘点确认时，以实际盘点数量覆盖系统库存
 *   - 所有确认操作均检查是否已确认（防止重复确认）
 *
 * 输入校验规则：
 *   - 入库数量：必填，必须大于0，不超过Integer.MAX_VALUE
 *   - 出库数量：必填，必须大于0，不超过库存数量
 *   - 单价：必填，必须大于0
 *   - 实际盘点数量：必填，必须大于等于0
 */
@Service
public class InventoryServiceImpl implements InventoryService {
    
    private final InboundRecordRepository inboundRecordRepository;
    private final OutboundRecordRepository outboundRecordRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryCheckRepository inventoryCheckRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    
    public InventoryServiceImpl(InboundRecordRepository inboundRecordRepository,
                                OutboundRecordRepository outboundRecordRepository,
                                InventoryRepository inventoryRepository,
                                InventoryCheckRepository inventoryCheckRepository,
                                ProductRepository productRepository,
                                WarehouseRepository warehouseRepository,
                                UserRepository userRepository) {
        this.inboundRecordRepository = inboundRecordRepository;
        this.outboundRecordRepository = outboundRecordRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryCheckRepository = inventoryCheckRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * 申请入库
     * 校验商品、仓库存在性及输入合法性后创建入库记录，不实际增加库存
     *
     * @param request    入库请求（包含商品ID、仓库ID、数量、单价等）
     * @param operatorId 操作员ID
     * @return 创建的入库记录（confirmed=false）
     * @throws WarehouseException 商品/仓库不存在或输入不合法时抛出
     */
    @Override
    @Transactional
    public InboundRecord inbound(InboundRequest request, Long operatorId) {
        validateInboundRequest(request);
        
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new WarehouseException("商品不存在"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new WarehouseException("仓库不存在"));
        User operator = userRepository.findById(operatorId).orElse(null);
        
        String inboundNo = generateInboundNo();
        
        InboundRecord record = new InboundRecord();
        record.setInboundNo(inboundNo);
        record.setProduct(product);
        record.setWarehouse(warehouse);
        record.setQuantity(request.getQuantity());
        record.setUnitPrice(request.getUnitPrice());
        record.setInboundTime(LocalDateTime.now());
        record.setOperator(operator);
        record.setRemark(request.getRemark());
        record.setConfirmed(false);
        
        return inboundRecordRepository.save(record);
    }
    
    /**
     * 入库请求校验
     * 校验商品ID、仓库ID、数量、单价等字段的合法性
     *
     * @param request 入库请求
     * @throws WarehouseException 校验失败时抛出
     */
    private void validateInboundRequest(InboundRequest request) {
        if (request == null) {
            throw new WarehouseException("入库请求不能为空");
        }
        if (request.getProductId() == null || request.getProductId() <= 0) {
            throw new WarehouseException("请选择商品");
        }
        if (request.getWarehouseId() == null || request.getWarehouseId() <= 0) {
            throw new WarehouseException("请选择仓库");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new WarehouseException("入库数量必须大于0");
        }
        if (request.getQuantity() > 999999) {
            throw new WarehouseException("入库数量不能超过999999");
        }
        if (request.getUnitPrice() == null || request.getUnitPrice() <= 0) {
            throw new WarehouseException("入库单价必须大于0");
        }
        if (request.getUnitPrice() > 999999999.99) {
            throw new WarehouseException("入库单价不能超过999999999.99");
        }
    }
    
    /**
     * 确认入库
     * 确认入库记录并实际增加库存
     * 若库存已存在则累加数量，否则新建库存记录
     *
     * @param id 入库记录ID
     * @throws WarehouseException 记录不存在、已确认时抛出
     */
    @Override
    @Transactional
    public void confirmInbound(Long id) {
        InboundRecord record = inboundRecordRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("入库记录不存在"));
        
        if (record.getConfirmed()) {
            throw new WarehouseException("该记录已确认");
        }
        
        Optional<Inventory> existingInv = inventoryRepository
            .findByProductIdAndWarehouseId(record.getProduct().getId(), record.getWarehouse().getId());
        
        Inventory inventory;
        if (existingInv.isPresent()) {
            inventory = existingInv.get();
            inventory.setQuantity(inventory.getQuantity() + record.getQuantity());
        } else {
            inventory = new Inventory();
            inventory.setProduct(record.getProduct());
            inventory.setWarehouse(record.getWarehouse());
            inventory.setQuantity(record.getQuantity());
        }
        
        inventoryRepository.save(inventory);
        record.setConfirmed(true);
        inboundRecordRepository.save(record);
    }
    
    /**
     * 查询所有入库记录
     *
     * @return 入库记录列表
     */
    @Override
    public List<InboundRecord> getInboundRecords() {
        return inboundRecordRepository.findAll();
    }
    
    /**
     * 申请出库
     * 校验商品、仓库存在性及库存是否充足后创建出库记录，不实际扣减库存
     *
     * @param request    出库请求（包含商品ID、仓库ID、数量等）
     * @param operatorId 操作员ID
     * @return 创建的出库记录（confirmed=false）
     * @throws WarehouseException 商品/仓库不存在、库存不存在或库存不足时抛出
     */
    @Override
    @Transactional
    public OutboundRecord outbound(OutboundRequest request, Long operatorId) {
        validateOutboundRequest(request);
        
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new WarehouseException("商品不存在"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new WarehouseException("仓库不存在"));
        User operator = userRepository.findById(operatorId).orElse(null);
        
        Optional<Inventory> existingInv = inventoryRepository
            .findByProductIdAndWarehouseId(request.getProductId(), request.getWarehouseId());
        
        if (existingInv.isEmpty()) {
            throw new WarehouseException("库存不存在");
        }
        
        Inventory inventory = existingInv.get();
        if (inventory.getQuantity() < request.getQuantity()) {
            throw new WarehouseException("库存不足，当前库存：" + inventory.getQuantity());
        }
        
        String outboundNo = generateOutboundNo();
        
        OutboundRecord record = new OutboundRecord();
        record.setOutboundNo(outboundNo);
        record.setProduct(product);
        record.setWarehouse(warehouse);
        record.setQuantity(request.getQuantity());
        record.setOutboundTime(LocalDateTime.now());
        record.setOperator(operator);
        record.setRemark(request.getRemark());
        record.setConfirmed(false);
        
        return outboundRecordRepository.save(record);
    }
    
    /**
     * 出库请求校验
     * 校验商品ID、仓库ID、数量等字段的合法性
     *
     * @param request 出库请求
     * @throws WarehouseException 校验失败时抛出
     */
    private void validateOutboundRequest(OutboundRequest request) {
        if (request == null) {
            throw new WarehouseException("出库请求不能为空");
        }
        if (request.getProductId() == null || request.getProductId() <= 0) {
            throw new WarehouseException("请选择商品");
        }
        if (request.getWarehouseId() == null || request.getWarehouseId() <= 0) {
            throw new WarehouseException("请选择仓库");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new WarehouseException("出库数量必须大于0");
        }
        if (request.getQuantity() > 999999) {
            throw new WarehouseException("出库数量不能超过999999");
        }
    }
    
    /**
     * 确认出库
     * 确认出库记录并实际扣减库存
     *
     * @param id 出库记录ID
     * @throws WarehouseException 记录不存在、已确认、库存不存在或库存不足时抛出
     */
    @Override
    @Transactional
    public void confirmOutbound(Long id) {
        OutboundRecord record = outboundRecordRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("出库记录不存在"));
        
        if (record.getConfirmed()) {
            throw new WarehouseException("该记录已确认");
        }
        
        Inventory inventory = inventoryRepository
            .findByProductIdAndWarehouseId(record.getProduct().getId(), record.getWarehouse().getId())
            .orElseThrow(() -> new WarehouseException("库存不存在"));
        
        if (inventory.getQuantity() < record.getQuantity()) {
            throw new WarehouseException("库存不足，当前库存：" + inventory.getQuantity());
        }
        
        inventory.setQuantity(inventory.getQuantity() - record.getQuantity());
        inventoryRepository.save(inventory);
        record.setConfirmed(true);
        outboundRecordRepository.save(record);
    }
    
    /**
     * 查询所有出库记录
     *
     * @return 出库记录列表
     */
    @Override
    public List<OutboundRecord> getOutboundRecords() {
        return outboundRecordRepository.findAll();
    }
    
    /**
     * 查询库存列表
     *
     * @param warehouseId 仓库ID（可选，为null时返回所有仓库的库存）
     * @return 库存列表
     */
    @Override
    public List<Inventory> getInventory(Long warehouseId) {
        if (warehouseId != null && warehouseId <= 0) {
            throw new WarehouseException("无效的仓库ID");
        }
        if (warehouseId == null) {
            return inventoryRepository.findAll();
        }
        return inventoryRepository.findByWarehouseId(warehouseId);
    }
    
    /**
     * 查询指定商品在指定仓库的库存
     *
     * @param productId   商品ID
     * @param warehouseId 仓库ID
     * @return 库存记录
     * @throws WarehouseException 库存不存在时抛出
     */
    @Override
    public Inventory getInventory(Long productId, Long warehouseId) {
        if (productId == null || productId <= 0) {
            throw new WarehouseException("无效的商品ID");
        }
        if (warehouseId == null || warehouseId <= 0) {
            throw new WarehouseException("无效的仓库ID");
        }
        return inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
            .orElseThrow(() -> new WarehouseException("库存不存在"));
    }
    
    /**
     * 申请盘点
     * 创建盘点记录，计算系统数量与实际数量的差异
     *
     * @param request    盘点请求（包含商品ID、仓库ID、实际数量等）
     * @param operatorId 操作员ID
     * @return 创建的盘点记录（confirmed=false）
     * @throws WarehouseException 商品/仓库不存在或输入不合法时抛出
     */
    @Override
    @Transactional
    public InventoryCheck checkInventory(InventoryCheckRequest request, Long operatorId) {
        validateCheckRequest(request);
        
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new WarehouseException("商品不存在"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new WarehouseException("仓库不存在"));
        User operator = userRepository.findById(operatorId).orElse(null);
        
        Inventory inventory = inventoryRepository
            .findByProductIdAndWarehouseId(request.getProductId(), request.getWarehouseId())
            .orElse(null);
        
        int systemQuantity = inventory != null ? inventory.getQuantity() : 0;
        int difference = request.getActualQuantity() - systemQuantity;
        
        String checkNo = generateCheckNo();
        
        InventoryCheck check = new InventoryCheck();
        check.setCheckNo(checkNo);
        check.setProduct(product);
        check.setWarehouse(warehouse);
        check.setSystemQuantity(systemQuantity);
        check.setActualQuantity(request.getActualQuantity());
        check.setDifference(difference);
        check.setCheckTime(LocalDateTime.now());
        check.setOperator(operator);
        check.setRemark(request.getRemark());
        check.setConfirmed(false);
        
        return inventoryCheckRepository.save(check);
    }
    
    /**
     * 盘点请求校验
     * 校验商品ID、仓库ID、实际数量等字段的合法性
     *
     * @param request 盘点请求
     * @throws WarehouseException 校验失败时抛出
     */
    private void validateCheckRequest(InventoryCheckRequest request) {
        if (request == null) {
            throw new WarehouseException("盘点请求不能为空");
        }
        if (request.getProductId() == null || request.getProductId() <= 0) {
            throw new WarehouseException("请选择商品");
        }
        if (request.getWarehouseId() == null || request.getWarehouseId() <= 0) {
            throw new WarehouseException("请选择仓库");
        }
        if (request.getActualQuantity() == null || request.getActualQuantity() < 0) {
            throw new WarehouseException("实际盘点数量不能为负数");
        }
        if (request.getActualQuantity() > 999999) {
            throw new WarehouseException("盘点数量不能超过999999");
        }
    }
    
    /**
     * 确认盘点
     * 确认盘点记录并以实际数量覆盖系统库存
     *
     * @param id 盘点记录ID
     * @throws WarehouseException 记录不存在、已确认时抛出
     */
    @Override
    @Transactional
    public void confirmCheck(Long id) {
        InventoryCheck check = inventoryCheckRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("盘点记录不存在"));
        
        if (check.getConfirmed()) {
            throw new WarehouseException("该记录已确认");
        }
        
        Optional<Inventory> existingInv = inventoryRepository
            .findByProductIdAndWarehouseId(check.getProduct().getId(), check.getWarehouse().getId());
        
        Inventory inventory;
        if (existingInv.isPresent()) {
            inventory = existingInv.get();
        } else {
            inventory = new Inventory();
            inventory.setProduct(check.getProduct());
            inventory.setWarehouse(check.getWarehouse());
        }
        
        inventory.setQuantity(check.getActualQuantity());
        inventoryRepository.save(inventory);
        check.setConfirmed(true);
        inventoryCheckRepository.save(check);
    }
    
    /**
     * 查询所有盘点记录
     *
     * @return 盘点记录列表
     */
    @Override
    public List<InventoryCheck> getCheckRecords() {
        return inventoryCheckRepository.findAll();
    }
    
    /**
     * 生成入库单号（RK + 时间戳毫秒）
     *
     * @return 入库单号
     */
    private String generateInboundNo() {
        return "RK" + System.currentTimeMillis();
    }
    
    /**
     * 生成出库单号（CK + 时间戳毫秒）
     *
     * @return 出库单号
     */
    private String generateOutboundNo() {
        return "CK" + System.currentTimeMillis();
    }
    
    /**
     * 生成盘点单号（PD + 时间戳毫秒）
     *
     * @return 盘点单号
     */
    private String generateCheckNo() {
        return "PD" + System.currentTimeMillis();
    }
}
