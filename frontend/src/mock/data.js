/**
 * Mock 数据模块
 * 当后端 API 不可用时（如 GitHub Pages 纯静态部署），使用此模块提供模拟数据
 * 用于在线演示，让用户能看到完整的系统功能
 */

// ========== 用户数据 ==========
export const mockUsers = [
  { id: 1, username: 'admin', name: '系统管理员', role: 'ADMIN', enabled: true },
  { id: 2, username: 'operator', name: '张操作', role: 'OPERATOR', enabled: true },
  { id: 3, username: 'employee', name: '李员工', role: 'EMPLOYEE', enabled: true }
]

// ========== 商品数据 ==========
export const mockProducts = [
  { id: 1, productCode: 'SKU001', name: '螺丝钉 M4', category: '五金配件', unit: '个', price: 0.5, minStock: 100, maxStock: 5000, description: '标准不锈钢螺丝钉' },
  { id: 2, productCode: 'SKU002', name: '纸箱 400×300', category: '包装材料', unit: '个', price: 3.0, minStock: 50, maxStock: 1000, description: '标准运输纸箱' },
  { id: 3, productCode: 'SKU003', name: '标签打印机', category: '办公设备', unit: '台', price: 280.0, minStock: 5, maxStock: 50, description: '热敏标签打印机' },
  { id: 4, productCode: 'SKU004', name: '叉车电池', category: '机械设备', unit: '块', price: 1200.0, minStock: 2, maxStock: 20, description: '48V 叉车蓄电池' },
  { id: 5, productCode: 'SKU005', name: '塑料托盘', category: '仓储用品', unit: '个', price: 45.0, minStock: 20, maxStock: 500, description: '1200×1000 标准托盘' },
  { id: 6, productCode: 'SKU006', name: '扫描枪', category: '办公设备', unit: '台', price: 180.0, minStock: 3, maxStock: 30, description: '无线蓝牙扫描枪' },
  { id: 7, productCode: 'SKU007', name: '货架 5层', category: '仓储用品', unit: '组', price: 580.0, minStock: 5, maxStock: 50, description: '1.8m 中型货架' },
  { id: 8, productCode: 'SKU008', name: '叉车', category: '机械设备', unit: '台', price: 8500.0, minStock: 1, maxStock: 10, description: '2.5吨电动叉车' },
  { id: 9, productCode: 'SKU009', name: '胶带', category: '包装材料', unit: '卷', price: 5.0, minStock: 100, maxStock: 2000, description: '48mm 透明胶带' },
  { id: 10, productCode: 'SKU010', name: '手套', category: '劳保用品', unit: '副', price: 8.0, minStock: 50, maxStock: 1000, description: '丁腈防护手套' }
]

// ========== 仓库数据 ==========
export const mockWarehouses = [
  { id: 1, warehouseCode: 'WH001', name: '主仓库', address: '北京市朝阳区建国路88号', manager: '王管理', totalValue: 285600 },
  { id: 2, warehouseCode: 'WH002', name: '备用仓库', address: '北京市海淀区中关村大街15号', manager: '赵管理', totalValue: 128400 },
  { id: 3, warehouseCode: 'WH003', name: '临时仓库', address: '北京市丰台区南三环西路66号', manager: '孙管理', totalValue: 52800 }
]

// ========== 库存数据 ==========
export const mockInventories = [
  { id: 1, productId: 1, warehouseId: 1, quantity: 500, lastUpdated: '2026-08-01 10:30:00' },
  { id: 2, productId: 1, warehouseId: 2, quantity: 200, lastUpdated: '2026-08-01 10:30:00' },
  { id: 3, productId: 2, warehouseId: 1, quantity: 300, lastUpdated: '2026-08-01 10:30:00' },
  { id: 4, productId: 3, warehouseId: 1, quantity: 15, lastUpdated: '2026-08-01 10:30:00' },
  { id: 5, productId: 4, warehouseId: 2, quantity: 8, lastUpdated: '2026-08-01 10:30:00' },
  { id: 6, productId: 5, warehouseId: 1, quantity: 150, lastUpdated: '2026-08-01 10:30:00' },
  { id: 7, productId: 6, warehouseId: 1, quantity: 12, lastUpdated: '2026-08-01 10:30:00' },
  { id: 8, productId: 7, warehouseId: 3, quantity: 25, lastUpdated: '2026-08-01 10:30:00' },
  { id: 9, productId: 8, warehouseId: 1, quantity: 3, lastUpdated: '2026-08-01 10:30:00' },
  { id: 10, productId: 9, warehouseId: 2, quantity: 500, lastUpdated: '2026-08-01 10:30:00' },
  { id: 11, productId: 10, warehouseId: 3, quantity: 80, lastUpdated: '2026-08-01 10:30:00' }
]

// ========== 入库记录 ==========
export const mockInboundRecords = [
  { id: 1, productId: 1, warehouseId: 1, quantity: 100, unitPrice: 0.5, totalPrice: 50, operatorId: 2, remark: '采购入库', createdAt: '2026-07-28 09:15:00' },
  { id: 2, productId: 3, warehouseId: 1, quantity: 5, unitPrice: 280, totalPrice: 1400, operatorId: 2, remark: '新设备采购', createdAt: '2026-07-29 14:30:00' },
  { id: 3, productId: 5, warehouseId: 2, quantity: 30, unitPrice: 45, totalPrice: 1350, operatorId: 2, remark: '补货', createdAt: '2026-07-30 11:00:00' },
  { id: 4, productId: 8, warehouseId: 1, quantity: 1, unitPrice: 8500, totalPrice: 8500, operatorId: 1, remark: '新叉车入库', createdAt: '2026-07-31 16:45:00' }
]

// ========== 出库记录 ==========
export const mockOutboundRecords = [
  { id: 1, productId: 1, warehouseId: 1, quantity: 50, unitPrice: 0.5, totalPrice: 25, operatorId: 2, remark: '生产线领用', createdAt: '2026-07-28 10:00:00' },
  { id: 2, productId: 2, warehouseId: 1, quantity: 20, unitPrice: 3, totalPrice: 60, operatorId: 2, remark: '发货出库', createdAt: '2026-07-29 15:30:00' },
  { id: 3, productId: 4, warehouseId: 2, quantity: 1, unitPrice: 1200, totalPrice: 1200, operatorId: 2, remark: '设备更换', createdAt: '2026-07-30 09:00:00' }
]

// ========== 盘点记录 ==========
export const mockInventoryChecks = [
  { id: 1, warehouseId: 1, operatorId: 2, status: 'COMPLETED', remark: '月度盘点', createdAt: '2026-07-25 09:00:00' },
  { id: 2, warehouseId: 2, operatorId: 1, status: 'COMPLETED', remark: '抽盘', createdAt: '2026-07-26 14:00:00' }
]

// ========== 供应商数据 ==========
export const mockSuppliers = [
  { id: 1, name: '北京五金有限公司', contact: '刘经理', phone: '010-12345678' },
  { id: 2, name: '上海包装材料厂', contact: '陈经理', phone: '021-87654321' },
  { id: 3, name: '深圳办公设备公司', contact: '周经理', phone: '0755-55667788' }
]

// ========== 统计数据 ==========
export const mockStatistics = {
  totalProducts: 10,
  totalWarehouses: 3,
  totalStockValue: 466800,
  lowStockAlerts: 2,
  todayInbound: 3,
  todayOutbound: 2,
  // 最近 7 天出入库趋势
  trendData: [
    { date: '07-27', inbound: 12, outbound: 8 },
    { date: '07-28', inbound: 15, outbound: 10 },
    { date: '07-29', inbound: 8, outbound: 14 },
    { date: '07-30', inbound: 20, outbound: 11 },
    { date: '07-31', inbound: 6, outbound: 9 },
    { date: '08-01', inbound: 18, outbound: 13 },
    { date: '08-02', inbound: 10, outbound: 7 }
  ],
  // 分类统计
  categoryData: [
    { name: '五金配件', value: 150 },
    { name: '包装材料', value: 820 },
    { name: '办公设备', value: 27 },
    { name: '机械设备', value: 11 },
    { name: '仓储用品', value: 175 },
    { name: '劳保用品', value: 80 }
  ]
}

// ========== Mock API 响应封装 ==========
export function mockResponse(data, message = 'success') {
  return {
    code: 200,
    message,
    data
  }
}

// ========== Mock 登录 ==========
export function mockLogin(username, password) {
  const user = mockUsers.find(u => u.username === username)
  if (!user) {
    throw new Error('用户名不存在')
  }
  if (!user.enabled) {
    throw new Error('用户已被禁用')
  }
  // Mock 模式下密码任意，只要用户名存在
  if (password && password.length >= 3) {
    const { id, username: u, name, role } = user
    return { id, username: u, name, role }
  }
  throw new Error('密码错误（至少3位）')
}

// ========== Mock 模式检测 ==========
// 全局标记，用于检测是否已经触发过请求失败
let _mockMode = null

/**
 * 检查是否使用 Mock 模式
 * 触发条件：
 * 1. GitHub Pages 环境（github.io 域名）
 * 2. VITE_USE_MOCK=true 环境变量
 * 3. 首次请求失败后自动降级
 */
export function isMockMode() {
  // 已手动设置或检测到失败
  if (_mockMode === true) {
    return true
  }
  if (_mockMode === false) {
    return false
  }
  
  // 检查是否在 GitHub Pages 上
  if (typeof window !== 'undefined') {
    const hostname = window.location.hostname || ''
    if (hostname.includes('github.io')) {
      _mockMode = true
      return true
    }
    // Vercel 环境
    if (hostname.includes('vercel.app')) {
      _mockMode = true
      return true
    }
  }
  
  // 检查环境变量
  if (typeof import.meta !== 'undefined' && import.meta.env.VITE_USE_MOCK === 'true') {
    _mockMode = true
    return true
  }
  
  return false
}

/**
 * 强制启用 Mock 模式（请求失败时调用）
 */
export function enableMockMode() {
  _mockMode = true
}

/**
 * 重置 Mock 模式（主要用于测试）
 */
export function resetMockMode() {
  _mockMode = null
}
