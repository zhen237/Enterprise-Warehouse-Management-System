-- ============================================
-- 企业仓库管理系统 - 数据库初始化脚本
-- Enterprise Warehouse Management System
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS warehouse_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE warehouse_db;

-- ============================================
-- 用户表 (users)
-- ============================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    role VARCHAR(20) NOT NULL COMMENT '角色：ADMIN/OPERATOR',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '电话',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入默认用户（密码都是 BCrypt 加密后的）
INSERT INTO users (username, password, role, name, phone, enabled) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'ADMIN', '系统管理员', '13800138000', 1),
('operator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'OPERATOR', '操作员', '13900139000', 1);

-- ============================================
-- 供应商表 (suppliers)
-- ============================================
DROP TABLE IF EXISTS suppliers;
CREATE TABLE suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_code VARCHAR(50) NOT NULL UNIQUE COMMENT '供应商编码',
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '电话',
    address VARCHAR(200) COMMENT '地址',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='供应商表';

-- ============================================
-- 商品表 (products)
-- ============================================
DROP TABLE IF EXISTS products;
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE COMMENT '商品编码',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    category VARCHAR(50) COMMENT '分类',
    unit VARCHAR(20) COMMENT '单位',
    price DECIMAL(10,2) DEFAULT 0 COMMENT '单价',
    description TEXT COMMENT '描述',
    supplier_id BIGINT COMMENT '供应商ID',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ============================================
-- 仓库表 (warehouses)
-- ============================================
DROP TABLE IF EXISTS warehouses;
CREATE TABLE warehouses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_code VARCHAR(50) NOT NULL UNIQUE COMMENT '仓库编码',
    warehouse_name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    location VARCHAR(200) COMMENT '位置',
    manager VARCHAR(50) COMMENT '负责人',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓库表';

-- ============================================
-- 库存表 (inventory)
-- ============================================
DROP TABLE IF EXISTS inventory;
CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    quantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    min_stock INT DEFAULT 10 COMMENT '最低库存（安全库存）',
    max_stock INT DEFAULT 100 COMMENT '最高库存（库存上限）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    UNIQUE KEY uk_product_warehouse (product_id, warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存表';

-- ============================================
-- 入库记录表 (inbound_records)
-- ============================================
DROP TABLE IF EXISTS inbound_records;
CREATE TABLE inbound_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inbound_no VARCHAR(50) NOT NULL UNIQUE COMMENT '入库单号',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    quantity INT NOT NULL COMMENT '入库数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    inbound_time DATETIME NOT NULL COMMENT '入库时间',
    operator_id BIGINT COMMENT '操作员ID',
    remark TEXT COMMENT '备注',
    confirmed TINYINT(1) DEFAULT 0 COMMENT '是否确认：0-未确认，1-已确认',
    confirmed_time DATETIME COMMENT '确认时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (operator_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入库记录表';

-- ============================================
-- 出库记录表 (outbound_records)
-- ============================================
DROP TABLE IF EXISTS outbound_records;
CREATE TABLE outbound_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    outbound_no VARCHAR(50) NOT NULL UNIQUE COMMENT '出库单号',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    quantity INT NOT NULL COMMENT '出库数量',
    outbound_time DATETIME NOT NULL COMMENT '出库时间',
    operator_id BIGINT COMMENT '操作员ID',
    remark TEXT COMMENT '备注',
    confirmed TINYINT(1) DEFAULT 0 COMMENT '是否确认：0-未确认，1-已确认',
    confirmed_time DATETIME COMMENT '确认时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (operator_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出库记录表';

-- ============================================
-- 库存盘点表 (inventory_check)
-- ============================================
DROP TABLE IF EXISTS inventory_check;
CREATE TABLE inventory_check (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    check_no VARCHAR(50) NOT NULL UNIQUE COMMENT '盘点单号',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    system_quantity INT NOT NULL COMMENT '系统数量',
    actual_quantity INT NOT NULL COMMENT '实际数量',
    difference INT COMMENT '差异（实际-系统）',
    check_time DATETIME NOT NULL COMMENT '盘点时间',
    operator_id BIGINT COMMENT '操作员ID',
    remark TEXT COMMENT '备注',
    confirmed TINYINT(1) DEFAULT 0 COMMENT '是否确认：0-未确认，1-已确认',
    confirmed_time DATETIME COMMENT '确认时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (operator_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存盘点表';

-- ============================================
-- 测试数据
-- ============================================

-- 插入供应商数据
INSERT INTO suppliers (supplier_code, supplier_name, contact, phone, address) VALUES
('SUP001', '华为技术有限公司', '张经理', '0755-12345678', '深圳市龙岗区坂田华为总部'),
('SUP002', '小米科技有限公司', '李经理', '010-87654321', '北京市海淀区小米科技园'),
('SUP003', '苹果（中国）有限公司', '王经理', '021-98765432', '上海市浦东新区苹果亚洲采购中心'),
('SUP004', '联想（北京）有限公司', '刘经理', '010-12349876', '北京市海淀区联想总部'),
('SUP005', '戴尔（中国）有限公司', '陈经理', '021-56789012', '厦门市思明区戴尔中国总部');

-- 插入商品数据
INSERT INTO products (product_code, product_name, category, unit, price, description, supplier_id) VALUES
-- 手机类
('PRO001', '华为 Mate 60 Pro', '手机', '台', 6999.00, '华为旗舰手机，麒麟9000S芯片', 1),
('PRO002', '小米 14 Pro', '手机', '台', 4999.00, '小米旗舰手机骁龙8Gen3', 2),
('PRO003', '苹果 iPhone 15 Pro', '手机', '台', 8999.00, '苹果旗舰手机A17Pro芯片', 3),
-- 笔记本类
('PRO004', '华为 MateBook 14', '笔记本', '台', 5699.00, '华为轻薄本，2K触控屏', 1),
('PRO005', '小米笔记本 Pro 14', '笔记本', '台', 6499.00, '小米Pro系列，OLED屏幕', 2),
('PRO006', 'MacBook Pro 14', '笔记本', '台', 14999.00, '苹果专业笔记本M3Pro芯片', 3),
('PRO007', 'ThinkPad X1 Carbon', '笔记本', '台', 12999.00, '联想商务旗舰，轻薄便携', 4),
('PRO008', '联想拯救者 Y9000X', '笔记本', '台', 9999.00, '联想游戏本，高性能', 4),
('PRO009', '戴尔 XPS 15', '笔记本', '台', 11999.00, '戴尔高端本，4K触控屏', 5),
('PRO010', 'Alienware M18', '笔记本', '台', 18999.00, '戴尔游戏本旗舰，顶级配置', 5),
-- 耳机类
('PRO011', '华为 FreeBuds Pro 3', '耳机', '副', 1499.00, '华为旗舰降噪耳机', 1),
('PRO012', '小米 Air 2 Pro', '耳机', '副', 699.00, '小米主动降噪耳机', 2),
('PRO013', '苹果 AirPods Pro 2', '耳机', '副', 1899.00, '苹果旗舰降噪耳机', 3),
-- 智能穿戴类
('PRO014', '华为 Watch 4', '手表', '块', 2699.00, '华为智能手表，健康监测', 1),
('PRO015', '小米手环 8', '手环', '个', 299.00, '小米智能手环，运动监测', 2);

-- 插入仓库数据
INSERT INTO warehouses (warehouse_code, warehouse_name, location, manager) VALUES
('WH001', '深圳总仓库', '深圳市宝安区仓库中心A区', '王建国'),
('WH002', '北京分仓库', '北京市朝阳区物流园B区', '李明'),
('WH003', '上海分仓库', '上海市浦东新区仓储中心C区', '张伟'),
('WH004', '广州分仓库', '广州市白云区物流仓库D区', '刘芳'),
('WH005', '成都分仓库', '成都市双流区配送中心E区', '陈刚');

-- 插入库存数据
INSERT INTO inventory (product_id, warehouse_id, quantity, min_stock, max_stock) VALUES
-- 深圳总仓库库存
(1, 1, 150, 20, 200),  -- 华为 Mate 60 Pro
(2, 1, 120, 20, 200),  -- 小米 14 Pro
(3, 1, 80, 10, 150),   -- iPhone 15 Pro
(4, 1, 100, 15, 180),  -- MateBook 14
(5, 1, 60, 10, 120),   -- 小米笔记本 Pro 14
(6, 1, 40, 5, 80),     -- MacBook Pro 14
(11, 1, 200, 30, 300), -- FreeBuds Pro 3
(12, 1, 180, 30, 250), -- 小米 Air 2 Pro
(13, 1, 150, 20, 200), -- AirPods Pro 2
(14, 1, 90, 15, 150),  -- 华为 Watch 4
(15, 1, 300, 50, 500), -- 小米手环 8
-- 北京分仓库库存
(1, 2, 80, 10, 150),   -- 华为 Mate 60 Pro
(4, 2, 60, 10, 100),   -- MateBook 14
(7, 2, 30, 5, 60),     -- ThinkPad X1 Carbon
(8, 2, 25, 5, 50),     -- 拯救者 Y9000X
(14, 2, 50, 10, 80),   -- 华为 Watch 4
-- 上海分仓库库存
(2, 3, 100, 15, 180),  -- 小米 14 Pro
(3, 3, 60, 8, 100),    -- iPhone 15 Pro
(6, 3, 20, 3, 50),     -- MacBook Pro 14
(9, 3, 25, 5, 50),     -- 戴尔 XPS 15
(10, 3, 15, 3, 30),    -- Alienware M18
(13, 3, 80, 10, 120),  -- AirPods Pro 2
-- 广州分仓库库存
(1, 4, 70, 10, 120),   -- 华为 Mate 60 Pro
(4, 4, 50, 8, 80),     -- MateBook 14
(11, 4, 100, 15, 150), -- FreeBuds Pro 3
(12, 4, 120, 20, 180), -- 小米 Air 2 Pro
(15, 4, 200, 30, 300), -- 小米手环 8
-- 成都分仓库库存
(2, 5, 60, 8, 100),    -- 小米 14 Pro
(5, 5, 40, 5, 80),     -- 小米笔记本 Pro 14
(7, 5, 20, 3, 40),     -- ThinkPad X1 Carbon
(14, 5, 40, 8, 70),    -- 华为 Watch 4;

-- 插入入库记录（历史数据）
INSERT INTO inbound_records (inbound_no, product_id, warehouse_id, quantity, unit_price, inbound_time, operator_id, confirmed, confirmed_time) VALUES
('RK20240101001', 1, 1, 100, 6500.00, '2024-01-05 10:00:00', 1, 1, '2024-01-05 14:30:00'),
('RK20240101002', 2, 1, 80, 4500.00, '2024-01-08 09:30:00', 1, 1, '2024-01-08 11:00:00'),
('RK20240101003', 3, 1, 50, 8500.00, '2024-01-10 14:00:00', 2, 1, '2024-01-10 16:00:00'),
('RK20240101004', 4, 2, 60, 5300.00, '2024-01-12 10:30:00', 1, 1, '2024-01-12 15:00:00'),
('RK20240101005', 11, 3, 100, 1300.00, '2024-01-15 11:00:00', 2, 1, '2024-01-15 13:30:00'),
('RK20240101006', 12, 4, 80, 600.00, '2024-01-18 09:00:00', 1, 1, '2024-01-18 10:30:00'),
('RK20240101007', 14, 5, 40, 2400.00, '2024-01-20 14:30:00', 2, 1, '2024-01-20 16:00:00'),
('RK20240101008', 7, 2, 30, 12000.00, '2024-02-01 10:00:00', 1, 1, '2024-02-01 11:30:00'),
('RK20240101009', 15, 1, 200, 250.00, '2024-02-05 09:00:00', 1, 1, '2024-02-05 10:00:00'),
('RK20240101010', 5, 3, 40, 6000.00, '2024-02-08 11:00:00', 2, 1, '2024-02-08 14:00:00');

-- 插入最近30天入库演示数据（用于仪表盘趋势图）
INSERT INTO inbound_records (inbound_no, product_id, warehouse_id, quantity, unit_price, inbound_time, operator_id, confirmed, confirmed_time) VALUES
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 29 DAY), '001'), 1, 1, 20, 6800.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 29 DAY), ' 09:30:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 29 DAY), ' 10:00:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 28 DAY), '001'), 2, 1, 15, 4800.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 28 DAY), ' 10:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 28 DAY), ' 10:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 27 DAY), '001'), 4, 2, 25, 5500.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 27 DAY), ' 09:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 27 DAY), ' 09:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 26 DAY), '001'), 11, 3, 40, 1400.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 26 DAY), ' 11:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 26 DAY), ' 11:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 25 DAY), '001'), 3, 1, 10, 8500.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 25 DAY), ' 14:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 25 DAY), ' 14:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 24 DAY), '001'), 6, 3, 8, 14500.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 24 DAY), ' 10:30:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 24 DAY), ' 11:00:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 23 DAY), '001'), 12, 4, 30, 700.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 23 DAY), ' 09:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 23 DAY), ' 09:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 22 DAY), '001'), 14, 5, 12, 2700.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 22 DAY), ' 13:30:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 22 DAY), ' 14:00:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 21 DAY), '001'), 7, 2, 10, 13000.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 21 DAY), ' 10:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 21 DAY), ' 10:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 20 DAY), '001'), 15, 1, 80, 300.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 20 DAY), ' 09:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 20 DAY), ' 09:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 19 DAY), '001'), 1, 2, 18, 6900.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 19 DAY), ' 10:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 19 DAY), ' 11:00:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 18 DAY), '001'), 5, 3, 12, 6500.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 18 DAY), ' 11:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 18 DAY), ' 11:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 17 DAY), '001'), 8, 2, 6, 10500.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 17 DAY), ' 14:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 17 DAY), ' 14:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 16 DAY), '001'), 13, 1, 25, 2000.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 16 DAY), ' 15:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 16 DAY), ' 15:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 15 DAY), '001'), 9, 3, 8, 12500.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 15 DAY), ' 09:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 15 DAY), ' 10:00:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 14 DAY), '001'), 10, 4, 4, 19500.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 14 DAY), ' 10:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 14 DAY), ' 10:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 13 DAY), '001'), 2, 1, 20, 4900.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 13 DAY), ' 11:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 13 DAY), ' 11:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 12 DAY), '001'), 4, 5, 15, 5800.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 12 DAY), ' 09:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 12 DAY), ' 09:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 11 DAY), '001'), 11, 2, 35, 1500.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 11 DAY), ' 14:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 11 DAY), ' 14:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 10 DAY), '001'), 3, 3, 14, 8800.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 10 DAY), ' 10:30:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 10 DAY), ' 11:00:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 9 DAY), '001'), 6, 1, 10, 14800.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 9 DAY), ' 09:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 9 DAY), ' 09:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 8 DAY), '001'), 12, 4, 28, 720.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 8 DAY), ' 13:30:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 8 DAY), ' 14:00:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 7 DAY), '001'), 14, 5, 15, 2800.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 10:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 10:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 6 DAY), '001'), 1, 1, 22, 7000.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 11:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 11:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '001'), 7, 2, 8, 12800.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 09:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 10:00:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 4 DAY), '001'), 15, 3, 55, 320.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 10:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 10:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 3 DAY), '001'), 2, 4, 18, 5000.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 14:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 15:00:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 2 DAY), '001'), 11, 5, 42, 1600.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 09:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 09:30:00')),
(CONCAT('RK', DATE_SUB(CURDATE(), INTERVAL 1 DAY), '001'), 4, 1, 20, 5900.00, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 10:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 11:00:00')),
(CONCAT('RK', CURDATE(), '001'), 3, 2, 10, 9000.00, CONCAT(CURDATE(), ' 09:00:00'), 1, 1, CONCAT(CURDATE(), ' 09:30:00')),
(CONCAT('RK', CURDATE(), '002'), 13, 3, 30, 1900.00, CONCAT(CURDATE(), ' 10:30:00'), 2, 1, CONCAT(CURDATE(), ' 11:00:00')),
(CONCAT('RK', CURDATE(), '003'), 8, 4, 5, 11000.00, CONCAT(CURDATE(), ' 14:00:00'), 1, 1, CONCAT(CURDATE(), ' 14:30:00'));

-- 插入出库记录（历史数据）
INSERT INTO outbound_records (outbound_no, product_id, warehouse_id, quantity, outbound_time, operator_id, confirmed, confirmed_time) VALUES
('CK20240102001', 1, 1, 50, '2024-01-06 14:00:00', 2, 1, '2024-01-06 15:00:00'),
('CK20240102002', 2, 1, 40, '2024-01-09 10:30:00', 2, 1, '2024-01-09 11:30:00'),
('CK20240102003', 11, 2, 30, '2024-01-11 15:00:00', 2, 1, '2024-01-11 16:00:00'),
('CK20240102004', 3, 3, 20, '2024-01-14 11:00:00', 2, 1, '2024-01-14 12:00:00'),
('CK20240102005', 12, 4, 40, '2024-01-16 09:30:00', 2, 1, '2024-01-16 10:30:00'),
('CK20240102006', 14, 1, 25, '2024-01-19 14:00:00', 2, 1, '2024-01-19 15:30:00'),
('CK20240102007', 4, 2, 20, '2024-01-22 10:00:00', 2, 1, '2024-01-22 11:00:00'),
('CK20240102008', 15, 3, 50, '2024-02-03 09:00:00', 2, 1, '2024-02-03 10:00:00'),
('CK20240102009', 7, 4, 15, '2024-02-06 14:30:00', 2, 1, '2024-02-06 15:30:00'),
('CK20240102010', 5, 5, 10, '2024-02-10 11:00:00', 2, 1, '2024-02-10 12:00:00');

-- 插入最近30天出库演示数据（用于仪表盘趋势图）
INSERT INTO outbound_records (outbound_no, product_id, warehouse_id, quantity, outbound_time, operator_id, confirmed, confirmed_time) VALUES
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 29 DAY), '001'), 1, 1, 10, CONCAT(DATE_SUB(CURDATE(), INTERVAL 29 DAY), ' 14:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 29 DAY), ' 14:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 28 DAY), '001'), 2, 1, 8, CONCAT(DATE_SUB(CURDATE(), INTERVAL 28 DAY), ' 10:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 28 DAY), ' 10:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 27 DAY), '001'), 4, 2, 12, CONCAT(DATE_SUB(CURDATE(), INTERVAL 27 DAY), ' 15:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 27 DAY), ' 15:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 26 DAY), '001'), 11, 3, 20, CONCAT(DATE_SUB(CURDATE(), INTERVAL 26 DAY), ' 11:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 26 DAY), ' 11:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 25 DAY), '001'), 3, 1, 5, CONCAT(DATE_SUB(CURDATE(), INTERVAL 25 DAY), ' 09:30:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 25 DAY), ' 10:00:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 24 DAY), '001'), 6, 3, 3, CONCAT(DATE_SUB(CURDATE(), INTERVAL 24 DAY), ' 14:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 24 DAY), ' 15:00:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 23 DAY), '001'), 12, 4, 18, CONCAT(DATE_SUB(CURDATE(), INTERVAL 23 DAY), ' 16:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 23 DAY), ' 16:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 22 DAY), '001'), 14, 5, 6, CONCAT(DATE_SUB(CURDATE(), INTERVAL 22 DAY), ' 10:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 22 DAY), ' 10:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 21 DAY), '001'), 7, 2, 4, CONCAT(DATE_SUB(CURDATE(), INTERVAL 21 DAY), ' 11:30:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 21 DAY), ' 12:00:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 20 DAY), '001'), 15, 1, 40, CONCAT(DATE_SUB(CURDATE(), INTERVAL 20 DAY), ' 09:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 20 DAY), ' 10:00:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 19 DAY), '001'), 1, 2, 9, CONCAT(DATE_SUB(CURDATE(), INTERVAL 19 DAY), ' 14:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 19 DAY), ' 14:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 18 DAY), '001'), 5, 3, 6, CONCAT(DATE_SUB(CURDATE(), INTERVAL 18 DAY), ' 10:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 18 DAY), ' 11:00:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 17 DAY), '001'), 8, 2, 3, CONCAT(DATE_SUB(CURDATE(), INTERVAL 17 DAY), ' 15:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 17 DAY), ' 15:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 16 DAY), '001'), 13, 1, 14, CONCAT(DATE_SUB(CURDATE(), INTERVAL 16 DAY), ' 09:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 16 DAY), ' 09:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 15 DAY), '001'), 9, 3, 4, CONCAT(DATE_SUB(CURDATE(), INTERVAL 15 DAY), ' 11:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 15 DAY), ' 11:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 14 DAY), '001'), 10, 4, 2, CONCAT(DATE_SUB(CURDATE(), INTERVAL 14 DAY), ' 14:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 14 DAY), ' 15:00:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 13 DAY), '001'), 2, 1, 10, CONCAT(DATE_SUB(CURDATE(), INTERVAL 13 DAY), ' 10:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 13 DAY), ' 10:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 12 DAY), '001'), 4, 5, 7, CONCAT(DATE_SUB(CURDATE(), INTERVAL 12 DAY), ' 15:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 12 DAY), ' 15:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 11 DAY), '001'), 11, 2, 18, CONCAT(DATE_SUB(CURDATE(), INTERVAL 11 DAY), ' 09:30:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 11 DAY), ' 10:00:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 10 DAY), '001'), 3, 3, 7, CONCAT(DATE_SUB(CURDATE(), INTERVAL 10 DAY), ' 11:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 10 DAY), ' 11:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 9 DAY), '001'), 6, 1, 5, CONCAT(DATE_SUB(CURDATE(), INTERVAL 9 DAY), ' 14:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 9 DAY), ' 14:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 8 DAY), '001'), 12, 4, 16, CONCAT(DATE_SUB(CURDATE(), INTERVAL 8 DAY), ' 10:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 8 DAY), ' 11:00:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 7 DAY), '001'), 14, 5, 8, CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 15:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 15:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 6 DAY), '001'), 1, 1, 11, CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 09:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 10:00:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '001'), 7, 2, 3, CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 14:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 14:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 4 DAY), '001'), 15, 3, 30, CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 10:30:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 11:00:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 3 DAY), '001'), 2, 4, 9, CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 15:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 15:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 2 DAY), '001'), 11, 5, 22, CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 09:00:00'), 2, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 09:30:00')),
(CONCAT('CK', DATE_SUB(CURDATE(), INTERVAL 1 DAY), '001'), 4, 1, 10, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 14:00:00'), 1, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 14:30:00')),
(CONCAT('CK', CURDATE(), '001'), 3, 2, 5, CONCAT(CURDATE(), ' 10:00:00'), 2, 1, CONCAT(CURDATE(), ' 10:30:00')),
(CONCAT('CK', CURDATE(), '002'), 13, 3, 16, CONCAT(CURDATE(), ' 14:30:00'), 1, 1, CONCAT(CURDATE(), ' 15:00:00')),
(CONCAT('CK', CURDATE(), '003'), 8, 4, 2, CONCAT(CURDATE(), ' 16:00:00'), 2, 1, CONCAT(CURDATE(), ' 16:30:00'));

-- 插入盘点记录
INSERT INTO inventory_check (check_no, product_id, warehouse_id, system_quantity, actual_quantity, difference, check_time, operator_id, confirmed, confirmed_time) VALUES
('PD20240103001', 1, 1, 150, 148, -2, '2024-01-25 10:00:00', 1, 1, '2024-01-25 14:00:00'),
('PD20240103002', 2, 1, 120, 122, 2, '2024-01-26 09:00:00', 1, 1, '2024-01-26 11:00:00'),
('PD20240103003', 11, 2, 200, 198, -2, '2024-01-27 14:00:00', 1, 1, '2024-01-27 16:00:00'),
('PD20240103004', 12, 3, 180, 182, 2, '2024-01-28 10:30:00', 1, 1, '2024-01-28 12:00:00'),
('PD20240103005', 15, 4, 300, 295, -5, '2024-01-29 15:00:00', 1, 1, '2024-01-29 17:00:00');

-- ============================================
-- 初始化完成
-- ============================================
COMMIT;

-- 显示统计信息
SELECT '数据库初始化完成！' AS status;
SELECT COUNT(*) AS '用户数' FROM users;
SELECT COUNT(*) AS '供应商数' FROM suppliers;
SELECT COUNT(*) AS '商品数' FROM products;
SELECT COUNT(*) AS '仓库数' FROM warehouses;
SELECT COUNT(*) AS '库存记录数' FROM inventory;
SELECT COUNT(*) AS '入库记录数' FROM inbound_records;
SELECT COUNT(*) AS '出库记录数' FROM outbound_records;
SELECT COUNT(*) AS '盘点记录数' FROM inventory_check;
