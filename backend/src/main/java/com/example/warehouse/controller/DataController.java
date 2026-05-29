package com.example.warehouse.controller;

import com.example.warehouse.dto.ApiResponse;
import com.example.warehouse.entity.*;
import com.example.warehouse.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final InboundRecordRepository inboundRecordRepository;
    private final OutboundRecordRepository outboundRecordRepository;
    private final InventoryCheckRepository inventoryCheckRepository;

    public DataController(SupplierRepository supplierRepository,
                         ProductRepository productRepository,
                         WarehouseRepository warehouseRepository,
                         InventoryRepository inventoryRepository,
                         InboundRecordRepository inboundRecordRepository,
                         OutboundRecordRepository outboundRecordRepository,
                         InventoryCheckRepository inventoryCheckRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
        this.inboundRecordRepository = inboundRecordRepository;
        this.outboundRecordRepository = outboundRecordRepository;
        this.inventoryCheckRepository = inventoryCheckRepository;
    }

    @GetMapping("/init")
    public ResponseEntity<ApiResponse<String>> initData() {
        if (supplierRepository.count() > 0) {
            return ResponseEntity.ok(ApiResponse.success("数据已存在，无需重复初始化"));
        }

        List<Supplier> suppliers = createSuppliers();
        supplierRepository.saveAll(suppliers);

        List<Product> products = createProducts(suppliers);
        productRepository.saveAll(products);

        List<Warehouse> warehouses = createWarehouses();
        warehouseRepository.saveAll(warehouses);

        List<Inventory> inventories = createInventories(products, warehouses);
        inventoryRepository.saveAll(inventories);

        List<InboundRecord> inboundRecords = createInboundRecords(products, warehouses);
        inboundRecordRepository.saveAll(inboundRecords);

        List<OutboundRecord> outboundRecords = createOutboundRecords(products, warehouses);
        outboundRecordRepository.saveAll(outboundRecords);

        List<InventoryCheck> inventoryChecks = createInventoryChecks(products, warehouses);
        inventoryCheckRepository.saveAll(inventoryChecks);

        return ResponseEntity.ok(ApiResponse.success("数据初始化完成"));
    }

    @GetMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetData() {
        inventoryCheckRepository.deleteAll();
        outboundRecordRepository.deleteAll();
        inboundRecordRepository.deleteAll();
        inventoryRepository.deleteAll();
        warehouseRepository.deleteAll();
        productRepository.deleteAll();
        supplierRepository.deleteAll();

        return ResponseEntity.ok(ApiResponse.success("数据已清空"));
    }

    private List<Supplier> createSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(createSupplier("S001", "华为技术有限公司", "张三", "13800138001", "深圳市龙岗区坂田华为基地"));
        suppliers.add(createSupplier("S002", "小米科技有限责任公司", "李四", "13800138002", "北京市海淀区小米科技园"));
        suppliers.add(createSupplier("S003", "苹果贸易（上海）有限公司", "王五", "13800138003", "上海市浦东新区世纪大道"));
        suppliers.add(createSupplier("S004", "联想（北京）有限公司", "赵六", "13800138004", "北京市海淀区中关村科技园"));
        suppliers.add(createSupplier("S005", "戴尔（中国）有限公司", "孙七", "13800138005", "上海市闵行区紫竹科技园"));
        return suppliers;
    }

    private Supplier createSupplier(String code, String name, String contact, String phone, String address) {
        Supplier s = new Supplier();
        s.setSupplierCode(code);
        s.setSupplierName(name);
        s.setContact(contact);
        s.setPhone(phone);
        s.setAddress(address);
        s.setEnabled(true);
        return s;
    }

    private List<Product> createProducts(List<Supplier> suppliers) {
        List<Product> products = new ArrayList<>();
        products.add(createProduct("P001", "华为Mate 60 Pro", "手机", "台", 6999.00, "华为旗舰手机，搭载麒麟芯片", suppliers.get(0)));
        products.add(createProduct("P002", "华为MateBook 14", "笔记本", "台", 5999.00, "华为轻薄笔记本电脑", suppliers.get(0)));
        products.add(createProduct("P003", "小米14 Pro", "手机", "台", 4999.00, "小米旗舰手机，徕卡影像", suppliers.get(1)));
        products.add(createProduct("P004", "小米笔记本Pro", "笔记本", "台", 4499.00, "小米高性能笔记本", suppliers.get(1)));
        products.add(createProduct("P005", "iPhone 15 Pro", "手机", "台", 8999.00, "苹果旗舰手机，钛金属设计", suppliers.get(2)));
        products.add(createProduct("P006", "MacBook Pro 14", "笔记本", "台", 14999.00, "苹果专业级笔记本", suppliers.get(2)));
        products.add(createProduct("P007", "联想ThinkPad X1", "笔记本", "台", 9999.00, "联想高端商务笔记本", suppliers.get(3)));
        products.add(createProduct("P008", "联想拯救者Y9000", "游戏本", "台", 8999.00, "联想高性能游戏笔记本", suppliers.get(3)));
        products.add(createProduct("P009", "戴尔XPS 15", "笔记本", "台", 11999.00, "戴尔高端轻薄本", suppliers.get(4)));
        products.add(createProduct("P010", "戴尔Alienware", "游戏本", "台", 15999.00, "戴尔高端游戏电脑", suppliers.get(4)));
        products.add(createProduct("P011", "华为FreeBuds Pro 3", "耳机", "副", 1199.00, "华为旗舰无线耳机", suppliers.get(0)));
        products.add(createProduct("P012", "小米Air 2 Pro", "耳机", "副", 499.00, "小米无线降噪耳机", suppliers.get(1)));
        products.add(createProduct("P013", "AirPods Pro 2", "耳机", "副", 1899.00, "苹果无线降噪耳机", suppliers.get(2)));
        products.add(createProduct("P014", "华为智能手表Watch 4", "手表", "块", 2999.00, "华为智能手表旗舰款", suppliers.get(0)));
        products.add(createProduct("P015", "小米手环8", "手环", "个", 399.00, "小米智能手环", suppliers.get(1)));
        return products;
    }

    private Product createProduct(String code, String name, String category, String unit, double price, String desc, Supplier supplier) {
        Product p = new Product();
        p.setProductCode(code);
        p.setProductName(name);
        p.setCategory(category);
        p.setUnit(unit);
        p.setPrice(price);
        p.setDescription(desc);
        p.setSupplier(supplier);
        p.setEnabled(true);
        return p;
    }

    private List<Warehouse> createWarehouses() {
        List<Warehouse> warehouses = new ArrayList<>();
        warehouses.add(createWarehouse("WH001", "深圳总仓库", "深圳市南山区科技园", "刘强"));
        warehouses.add(createWarehouse("WH002", "北京分仓库", "北京市朝阳区望京", "陈明"));
        warehouses.add(createWarehouse("WH003", "上海分仓库", "上海市浦东新区张江", "王伟"));
        warehouses.add(createWarehouse("WH004", "广州分仓库", "广州市天河区珠江新城", "刘洋"));
        warehouses.add(createWarehouse("WH005", "成都分仓库", "成都市高新区天府软件园", "杨帆"));
        return warehouses;
    }

    private Warehouse createWarehouse(String code, String name, String location, String manager) {
        Warehouse w = new Warehouse();
        w.setWarehouseCode(code);
        w.setWarehouseName(name);
        w.setLocation(location);
        w.setManager(manager);
        w.setEnabled(true);
        return w;
    }

    private List<Inventory> createInventories(List<Product> products, List<Warehouse> warehouses) {
        List<Inventory> inventories = new ArrayList<>();
        inventories.add(createInventory(products.get(0), warehouses.get(0), 150, 20, 200));
        inventories.add(createInventory(products.get(0), warehouses.get(1), 100, 20, 200));
        inventories.add(createInventory(products.get(0), warehouses.get(2), 80, 20, 200));
        inventories.add(createInventory(products.get(1), warehouses.get(0), 60, 10, 100));
        inventories.add(createInventory(products.get(1), warehouses.get(1), 45, 10, 100));
        inventories.add(createInventory(products.get(2), warehouses.get(0), 200, 30, 250));
        inventories.add(createInventory(products.get(2), warehouses.get(2), 120, 30, 250));
        inventories.add(createInventory(products.get(2), warehouses.get(3), 80, 30, 250));
        inventories.add(createInventory(products.get(3), warehouses.get(0), 50, 10, 80));
        inventories.add(createInventory(products.get(3), warehouses.get(4), 30, 10, 80));
        inventories.add(createInventory(products.get(4), warehouses.get(0), 80, 15, 150));
        inventories.add(createInventory(products.get(4), warehouses.get(1), 60, 15, 150));
        inventories.add(createInventory(products.get(4), warehouses.get(2), 70, 15, 150));
        inventories.add(createInventory(products.get(5), warehouses.get(0), 25, 5, 50));
        inventories.add(createInventory(products.get(5), warehouses.get(1), 20, 5, 50));
        inventories.add(createInventory(products.get(6), warehouses.get(1), 35, 8, 60));
        inventories.add(createInventory(products.get(6), warehouses.get(3), 25, 8, 60));
        inventories.add(createInventory(products.get(7), warehouses.get(0), 40, 10, 80));
        inventories.add(createInventory(products.get(7), warehouses.get(4), 30, 10, 80));
        inventories.add(createInventory(products.get(8), warehouses.get(2), 20, 5, 40));
        inventories.add(createInventory(products.get(9), warehouses.get(0), 15, 3, 30));
        inventories.add(createInventory(products.get(10), warehouses.get(0), 200, 30, 300));
        inventories.add(createInventory(products.get(10), warehouses.get(1), 150, 30, 300));
        inventories.add(createInventory(products.get(11), warehouses.get(0), 300, 50, 400));
        inventories.add(createInventory(products.get(11), warehouses.get(2), 200, 50, 400));
        inventories.add(createInventory(products.get(12), warehouses.get(0), 100, 20, 150));
        inventories.add(createInventory(products.get(12), warehouses.get(1), 80, 20, 150));
        inventories.add(createInventory(products.get(13), warehouses.get(0), 60, 10, 100));
        inventories.add(createInventory(products.get(13), warehouses.get(3), 40, 10, 100));
        inventories.add(createInventory(products.get(14), warehouses.get(0), 500, 100, 600));
        inventories.add(createInventory(products.get(14), warehouses.get(4), 300, 100, 600));
        return inventories;
    }

    private Inventory createInventory(Product product, Warehouse warehouse, int quantity, int minStock, int maxStock) {
        Inventory i = new Inventory();
        i.setProduct(product);
        i.setWarehouse(warehouse);
        i.setQuantity(quantity);
        i.setMinStock(minStock);
        i.setMaxStock(maxStock);
        return i;
    }

    private List<InboundRecord> createInboundRecords(List<Product> products, List<Warehouse> warehouses) {
        List<InboundRecord> records = new ArrayList<>();
        records.add(createInboundRecord("IN202401001", products.get(0), warehouses.get(0), 50, 6500.00, LocalDateTime.of(2024, 1, 10, 9, 30), "批量采购入库"));
        records.add(createInboundRecord("IN202401002", products.get(2), warehouses.get(0), 80, 4500.00, LocalDateTime.of(2024, 1, 12, 14, 0), "新品入库"));
        records.add(createInboundRecord("IN202401003", products.get(4), warehouses.get(1), 40, 8500.00, LocalDateTime.of(2024, 1, 15, 10, 0), "北京仓库补货"));
        records.add(createInboundRecord("IN202401004", products.get(10), warehouses.get(0), 100, 1000.00, LocalDateTime.of(2024, 1, 18, 15, 30), "耳机批量到货"));
        records.add(createInboundRecord("IN202401005", products.get(6), warehouses.get(3), 20, 9500.00, LocalDateTime.of(2024, 1, 20, 11, 0), "广州仓库新品"));
        records.add(createInboundRecord("IN202402001", products.get(1), warehouses.get(0), 30, 5500.00, LocalDateTime.of(2024, 2, 5, 9, 0), "笔记本补货"));
        records.add(createInboundRecord("IN202402002", products.get(7), warehouses.get(4), 25, 8500.00, LocalDateTime.of(2024, 2, 8, 14, 30), "成都游戏本入库"));
        records.add(createInboundRecord("IN202402003", products.get(13), warehouses.get(0), 30, 2800.00, LocalDateTime.of(2024, 2, 12, 10, 0), "智能手表到货"));
        records.add(createInboundRecord("IN202402004", products.get(14), warehouses.get(0), 200, 350.00, LocalDateTime.of(2024, 2, 15, 16, 0), "手环大批量入库"));
        records.add(createInboundRecord("IN202402005", products.get(5), warehouses.get(2), 15, 14000.00, LocalDateTime.of(2024, 2, 18, 9, 30), "上海MacBook到货"));
        return records;
    }

    private InboundRecord createInboundRecord(String no, Product product, Warehouse warehouse, int quantity, double price, LocalDateTime time, String remark) {
        InboundRecord r = new InboundRecord();
        r.setInboundNo(no);
        r.setProduct(product);
        r.setWarehouse(warehouse);
        r.setQuantity(quantity);
        r.setUnitPrice(price);
        r.setInboundTime(time);
        r.setRemark(remark);
        r.setConfirmed(true);
        return r;
    }

    private List<OutboundRecord> createOutboundRecords(List<Product> products, List<Warehouse> warehouses) {
        List<OutboundRecord> records = new ArrayList<>();
        records.add(createOutboundRecord("OUT202401001", products.get(0), warehouses.get(0), 20, LocalDateTime.of(2024, 1, 11, 10, 0), "电商订单发货"));
        records.add(createOutboundRecord("OUT202401002", products.get(2), warehouses.get(0), 30, LocalDateTime.of(2024, 1, 13, 15, 0), "经销商提货"));
        records.add(createOutboundRecord("OUT202401003", products.get(4), warehouses.get(1), 15, LocalDateTime.of(2024, 1, 16, 9, 30), "北京直营店调货"));
        records.add(createOutboundRecord("OUT202401004", products.get(10), warehouses.get(0), 50, LocalDateTime.of(2024, 1, 19, 14, 0), "京东自营发货"));
        records.add(createOutboundRecord("OUT202401005", products.get(6), warehouses.get(3), 10, LocalDateTime.of(2024, 1, 21, 11, 30), "广州经销商提货"));
        records.add(createOutboundRecord("OUT202402001", products.get(1), warehouses.get(0), 15, LocalDateTime.of(2024, 2, 6, 10, 0), "企业采购订单"));
        records.add(createOutboundRecord("OUT202402002", products.get(7), warehouses.get(4), 12, LocalDateTime.of(2024, 2, 9, 15, 0), "成都游戏店调货"));
        records.add(createOutboundRecord("OUT202402003", products.get(13), warehouses.get(0), 20, LocalDateTime.of(2024, 2, 13, 9, 0), "线下门店补货"));
        records.add(createOutboundRecord("OUT202402004", products.get(14), warehouses.get(0), 100, LocalDateTime.of(2024, 2, 16, 14, 30), "拼多多团购发货"));
        records.add(createOutboundRecord("OUT202402005", products.get(5), warehouses.get(2), 8, LocalDateTime.of(2024, 2, 19, 10, 30), "上海旗舰店调货"));
        return records;
    }

    private OutboundRecord createOutboundRecord(String no, Product product, Warehouse warehouse, int quantity, LocalDateTime time, String remark) {
        OutboundRecord r = new OutboundRecord();
        r.setOutboundNo(no);
        r.setProduct(product);
        r.setWarehouse(warehouse);
        r.setQuantity(quantity);
        r.setOutboundTime(time);
        r.setRemark(remark);
        r.setConfirmed(true);
        return r;
    }

    private List<InventoryCheck> createInventoryChecks(List<Product> products, List<Warehouse> warehouses) {
        List<InventoryCheck> checks = new ArrayList<>();
        checks.add(createInventoryCheck("CK202401001", products.get(0), warehouses.get(0), 130, 128, -2, LocalDateTime.of(2024, 1, 25, 9, 0), "月度盘点"));
        checks.add(createInventoryCheck("CK202401002", products.get(2), warehouses.get(0), 150, 150, 0, LocalDateTime.of(2024, 1, 25, 10, 0), "月度盘点"));
        checks.add(createInventoryCheck("CK202401003", products.get(4), warehouses.get(1), 45, 46, 1, LocalDateTime.of(2024, 1, 26, 9, 0), "月度盘点"));
        checks.add(createInventoryCheck("CK202402001", products.get(10), warehouses.get(0), 150, 148, -2, LocalDateTime.of(2024, 2, 28, 9, 0), "月度盘点"));
        checks.add(createInventoryCheck("CK202402002", products.get(14), warehouses.get(0), 400, 405, 5, LocalDateTime.of(2024, 2, 28, 10, 30), "月度盘点"));
        return checks;
    }

    private InventoryCheck createInventoryCheck(String no, Product product, Warehouse warehouse, int systemQty, int actualQty, int diff, LocalDateTime time, String remark) {
        InventoryCheck c = new InventoryCheck();
        c.setCheckNo(no);
        c.setProduct(product);
        c.setWarehouse(warehouse);
        c.setSystemQuantity(systemQty);
        c.setActualQuantity(actualQty);
        c.setDifference(diff);
        c.setCheckTime(time);
        c.setRemark(remark);
        c.setConfirmed(true);
        return c;
    }
}