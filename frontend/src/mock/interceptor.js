/**
 * Mock API 拦截器
 * 
 * 通过覆盖 axios 适配器实现 Mock 模式
 * 当后端 API 不可用时（如 GitHub Pages 纯静态部署），直接返回 Mock 数据
 */
import {
  mockUsers,
  mockProducts,
  mockWarehouses,
  mockInventories,
  mockInboundRecords,
  mockOutboundRecords,
  mockInventoryChecks,
  mockSuppliers,
  mockStatistics,
  mockResponse,
  mockLogin,
  isMockMode
} from './data.js'

// ========== 数据转换函数 ==========

function transformProduct(p) {
  return {
    id: p.id,
    productCode: p.productCode,
    productName: p.name || p.productName,
    category: p.category,
    unit: p.unit,
    price: p.price,
    minStock: p.minStock,
    maxStock: p.maxStock,
    description: p.description,
    supplier: p.supplier || { supplierName: '默认供应商' }
  }
}

function transformWarehouse(w) {
  const totalItems = [...new Set(mockInventories.filter(i => i.warehouseId === w.id).map(i => i.productId))].length
  return {
    id: w.id,
    warehouseCode: w.warehouseCode,
    warehouseName: w.name || w.warehouseName,
    location: w.address || w.location,
    manager: w.manager,
    totalValue: w.totalValue || 0,
    totalItems: totalItems
  }
}

function transformInventory(inv) {
  const product = mockProducts.find(p => p.id === inv.productId)
  const warehouse = mockWarehouses.find(w => w.id === inv.warehouseId)
  return {
    id: inv.id,
    productId: inv.productId,
    warehouseId: inv.warehouseId,
    quantity: inv.quantity,
    lastUpdated: inv.lastUpdated,
    product: {
      id: product?.id,
      productCode: product?.productCode || '',
      productName: product?.name || '未知商品',
      category: product?.category || '',
      unit: product?.unit || '',
      price: product?.price || 0
    },
    warehouse: {
      id: warehouse?.id,
      warehouseCode: warehouse?.warehouseCode || '',
      warehouseName: warehouse?.name || '未知仓库'
    },
    minStock: product?.minStock || 0,
    maxStock: product?.maxStock || 9999,
    totalValue: (product?.price || 0) * inv.quantity
  }
}

function transformInbound(record) {
  const product = mockProducts.find(p => p.id === record.productId)
  const warehouse = mockWarehouses.find(w => w.id === record.warehouseId)
  const operator = mockUsers.find(u => u.id === record.operatorId)
  return {
    id: record.id,
    inboundNo: record.inboundNo || `IN${String(record.id).padStart(6, '0')}`,
    productId: record.productId,
    warehouseId: record.warehouseId,
    quantity: record.quantity,
    unitPrice: record.unitPrice,
    totalPrice: record.totalPrice || record.quantity * record.unitPrice,
    remark: record.remark || '',
    confirmed: record.confirmed !== false,
    inboundTime: record.inboundTime || record.createdAt,
    product: {
      id: product?.id,
      productCode: product?.productCode || '',
      productName: product?.name || '未知商品'
    },
    warehouse: {
      id: warehouse?.id,
      warehouseCode: warehouse?.warehouseCode || '',
      warehouseName: warehouse?.name || '未知仓库'
    },
    operator: operator ? { id: operator.id, name: operator.name } : null
  }
}

function transformOutbound(record) {
  const product = mockProducts.find(p => p.id === record.productId)
  const warehouse = mockWarehouses.find(w => w.id === record.warehouseId)
  const operator = mockUsers.find(u => u.id === record.operatorId)
  return {
    id: record.id,
    outboundNo: record.outboundNo || `OUT${String(record.id).padStart(6, '0')}`,
    productId: record.productId,
    warehouseId: record.warehouseId,
    quantity: record.quantity,
    unitPrice: record.unitPrice,
    totalPrice: record.totalPrice || record.quantity * record.unitPrice,
    remark: record.remark || '',
    confirmed: record.confirmed !== false,
    outboundTime: record.outboundTime || record.createdAt,
    product: {
      id: product?.id,
      productCode: product?.productCode || '',
      productName: product?.name || '未知商品'
    },
    warehouse: {
      id: warehouse?.id,
      warehouseCode: warehouse?.warehouseCode || '',
      warehouseName: warehouse?.name || '未知仓库'
    },
    operator: operator ? { id: operator.id, name: operator.name } : null
  }
}

function transformCheck(record) {
  const warehouse = mockWarehouses.find(w => w.id === record.warehouseId)
  const operator = mockUsers.find(u => u.id === record.operatorId)
  return {
    id: record.id,
    warehouseId: record.warehouseId,
    status: record.status || 'COMPLETED',
    remark: record.remark || '',
    checkTime: record.checkTime || record.createdAt,
    warehouse: {
      id: warehouse?.id,
      warehouseName: warehouse?.name || '未知仓库'
    },
    operator: operator ? { id: operator.id, name: operator.name } : null,
    items: record.items || []
  }
}

// ========== 主响应函数 ==========

export function getMockResponse(url, method, data) {
  const m = method.toLowerCase()

  // 登录接口
  if (url.includes('/auth/login') && m === 'post') {
    const user = mockLogin(data.username, data.password)
    return mockResponse(user)
  }

  // 商品接口
  if (url.includes('/products') && !url.includes('/warehouse')) {
    if (m === 'get') {
      if (url.includes('/low-stock')) {
        const lowStock = mockProducts.filter(p => {
          const inv = mockInventories.find(i => i.productId === p.id)
          return inv && inv.quantity < p.minStock
        })
        return mockResponse(lowStock.map(transformProduct))
      }
      if (url.includes('/search')) {
        const keyword = url.split('name=')[1] || ''
        const filtered = mockProducts.filter(p => p.name.includes(keyword))
        return mockResponse(filtered.map(transformProduct))
      }
      return mockResponse(mockProducts.map(transformProduct))
    }
    if (m === 'post') {
      const newProduct = { ...data, id: mockProducts.length + 1 }
      mockProducts.push(newProduct)
      return mockResponse(transformProduct(newProduct))
    }
    if (m === 'put') {
      const id = parseInt(url.split('/').pop())
      const idx = mockProducts.findIndex(p => p.id === id)
      if (idx !== -1) {
        mockProducts[idx] = { ...mockProducts[idx], ...data }
        return mockResponse(transformProduct(mockProducts[idx]))
      }
    }
    if (m === 'delete') {
      const id = parseInt(url.split('/').pop())
      const idx = mockProducts.findIndex(p => p.id === id)
      if (idx !== -1) {
        mockProducts.splice(idx, 1)
        return mockResponse(null)
      }
    }
  }

  // 仓库接口
  if (url.includes('/warehouses')) {
    if (m === 'get') {
      if (url.includes('/statistics')) {
        const stats = mockWarehouses.map(w => {
          const totalStock = mockInventories.filter(i => i.warehouseId === w.id).reduce((sum, i) => sum + i.quantity, 0)
          return { id: w.id, warehouseName: w.name, totalStock, totalValue: w.totalValue }
        })
        return mockResponse(stats)
      }
      return mockResponse(mockWarehouses.map(transformWarehouse))
    }
    if (m === 'post') {
      const newWH = { ...data, id: mockWarehouses.length + 1 }
      mockWarehouses.push(newWH)
      return mockResponse(transformWarehouse(newWH))
    }
    if (m === 'put') {
      const id = parseInt(url.split('/').pop())
      const idx = mockWarehouses.findIndex(w => w.id === id)
      if (idx !== -1) {
        mockWarehouses[idx] = { ...mockWarehouses[idx], ...data }
        return mockResponse(transformWarehouse(mockWarehouses[idx]))
      }
    }
    if (m === 'delete') {
      const id = parseInt(url.split('/').pop())
      const idx = mockWarehouses.findIndex(w => w.id === id)
      if (idx !== -1) {
        mockWarehouses.splice(idx, 1)
        return mockResponse(null)
      }
    }
  }

  // 库存查询（GET /inventory，排除 inbound/outbound）
  if ((url === '/inventory' || url.includes('/inventory')) && !url.includes('inbound') && !url.includes('outbound') && !url.includes('check')) {
    if (m === 'get') {
      return mockResponse(mockInventories.map(transformInventory))
    }
  }

  // 入库接口
  if (url.includes('inbound') && !url.includes('outbound')) {
    if (m === 'get') {
      return mockResponse(mockInboundRecords.map(transformInbound))
    }
    if (m === 'post') {
      if (url.includes('/confirm')) {
        const parts = url.split('/')
        const id = parseInt(parts[parts.length - 2])
        const record = mockInboundRecords.find(r => r.id === id)
        if (record) {
          record.confirmed = true
          const inv = mockInventories.find(i => i.productId === record.productId && i.warehouseId === record.warehouseId)
          const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
          if (inv) {
            inv.quantity += record.quantity
            inv.lastUpdated = now
          } else {
            mockInventories.push({ id: mockInventories.length + 1, productId: record.productId, warehouseId: record.warehouseId, quantity: record.quantity, lastUpdated: now })
          }
        }
        return mockResponse({ success: true })
      }
      const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
      const newRecord = {
        ...data,
        id: mockInboundRecords.length + 1,
        inboundNo: `IN${String(mockInboundRecords.length + 1).padStart(6, '0')}`,
        totalPrice: (data.quantity || 0) * (data.unitPrice || 0),
        confirmed: false,
        inboundTime: now,
        createdAt: now
      }
      mockInboundRecords.push(newRecord)
      return mockResponse(transformInbound(newRecord))
    }
  }

  // 出库接口
  if (url.includes('outbound')) {
    if (m === 'get') {
      return mockResponse(mockOutboundRecords.map(transformOutbound))
    }
    if (m === 'post') {
      if (url.includes('/confirm')) {
        const parts = url.split('/')
        const id = parseInt(parts[parts.length - 2])
        const record = mockOutboundRecords.find(r => r.id === id)
        if (record) record.confirmed = true
        return mockResponse({ success: true })
      }
      const inv = mockInventories.find(i => i.productId === data.productId && i.warehouseId === data.warehouseId)
      if (!inv || inv.quantity < data.quantity) {
        throw new Error('库存不足')
      }
      const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
      const newRecord = {
        ...data,
        id: mockOutboundRecords.length + 1,
        outboundNo: `OUT${String(mockOutboundRecords.length + 1).padStart(6, '0')}`,
        totalPrice: (data.quantity || 0) * (data.unitPrice || 0),
        confirmed: false,
        outboundTime: now,
        createdAt: now
      }
      mockOutboundRecords.push(newRecord)
      inv.quantity -= data.quantity
      inv.lastUpdated = now
      return mockResponse(transformOutbound(newRecord))
    }
  }

  // 盘点接口
  if (url.includes('check')) {
    if (m === 'get') {
      return mockResponse(mockInventoryChecks.map(transformCheck))
    }
    if (m === 'post') {
      const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
      const newRecord = {
        ...data,
        id: mockInventoryChecks.length + 1,
        status: 'COMPLETED',
        checkTime: now,
        createdAt: now
      }
      mockInventoryChecks.push(newRecord)
      data.items?.forEach(item => {
        const inv = mockInventories.find(i => i.productId === item.productId && i.warehouseId === data.warehouseId)
        if (inv) {
          inv.quantity = item.actualQuantity
          inv.lastUpdated = now
        }
      })
      return mockResponse(transformCheck(newRecord))
    }
  }

  // 用户接口
  if (url.includes('/users')) {
    if (m === 'get') {
      return mockResponse(mockUsers.map(u => ({
        id: u.id,
        username: u.username,
        name: u.name,
        role: u.role,
        enabled: u.enabled
      })))
    }
    if (m === 'post') {
      const newUser = { ...data, id: mockUsers.length + 1, enabled: true }
      mockUsers.push(newUser)
      return mockResponse({ id: newUser.id, username: newUser.username, name: newUser.name, role: newUser.role, enabled: true })
    }
    if (m === 'put') {
      const id = parseInt(url.split('/').pop())
      const idx = mockUsers.findIndex(u => u.id === id)
      if (idx !== -1) {
        mockUsers[idx] = { ...mockUsers[idx], ...data }
        return mockResponse({ ...mockUsers[idx], password: undefined })
      }
    }
    if (m === 'delete') {
      const id = parseInt(url.split('/').pop())
      const idx = mockUsers.findIndex(u => u.id === id)
      if (idx !== -1) {
        mockUsers.splice(idx, 1)
        return mockResponse(null)
      }
    }
  }

  // 报表统计接口
  if (url.includes('/reports') || url.includes('/statistics') || url === '/data') {
    if (url.includes('/trend')) {
      return mockResponse(mockStatistics.trendData)
    }
    if (url.includes('/category')) {
      return mockResponse(mockStatistics.categoryData)
    }
    return mockResponse({
      totalProducts: mockProducts.length,
      totalWarehouses: mockWarehouses.length,
      totalStockValue: mockInventories.reduce((sum, i) => {
        const p = mockProducts.find(pr => pr.id === i.productId)
        return sum + (p?.price || 0) * i.quantity
      }, 0),
      lowStockAlerts: mockProducts.filter(p => {
        const inv = mockInventories.find(i => i.productId === p.id)
        return inv && inv.quantity < p.minStock
      }).length,
      todayInbound: mockInboundRecords.length,
      todayOutbound: mockOutboundRecords.length,
      trendData: mockStatistics.trendData,
      categoryData: mockStatistics.categoryData
    })
  }

  // 供应商接口
  if (url.includes('/suppliers')) {
    return mockResponse(mockSuppliers)
  }

  // 默认返回空数据
  return mockResponse([])
}

/**
 * 安装 Mock 适配器
 * 通过覆盖 axios 适配器，直接返回 Mock 数据，不发送真实请求
 */
export function installMockInterceptor(api) {
  // 保存原始适配器
  const originalAdapter = api.defaults.adapter

  // 替换为 Mock 适配器
  api.defaults.adapter = function (config) {
    if (!isMockMode()) {
      return originalAdapter(config)
    }

    const url = config.url || ''
    const method = (config.method || 'get').toLowerCase()
    const data = config.data ? (typeof config.data === 'string' ? JSON.parse(config.data) : config.data) : {}

    return new Promise((resolve, reject) => {
      setTimeout(() => {
        try {
          const mockData = getMockResponse(url, method, data)
          resolve({
            data: mockData,
            status: 200,
            statusText: 'OK',
            headers: { 'content-type': 'application/json' },
            config
          })
        } catch (error) {
          reject(new Error(error.message || '请求失败'))
        }
      }, 100)
    })
  }
}

export { isMockMode }
