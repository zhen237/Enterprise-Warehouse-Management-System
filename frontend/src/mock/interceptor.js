/**
 * Mock API 拦截器
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

/**
 * 将 Mock 商品数据转换为后端 API 格式
 */
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

/**
 * 将 Mock 仓库数据转换为后端 API 格式
 */
function transformWarehouse(w) {
  const totalItems = mockInventories
    .filter(i => i.warehouseId === w.id)
    .map(i => i.productId)
    .filter((v, i, a) => a.indexOf(v) === i)
    .length
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

/**
 * 根据请求路径和方法返回对应的 Mock 数据
 */
export function getMockResponse(url, method, data) {
  // 登录接口
  if (url.includes('/auth/login') && method === 'post') {
    const user = mockLogin(data.username, data.password)
    return mockResponse(user)
  }

  // 商品接口
  if (url.includes('/products')) {
    if (method === 'get') {
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
    if (method === 'post') {
      const newProduct = { ...data, id: mockProducts.length + 1 }
      mockProducts.push(newProduct)
      return mockResponse(transformProduct(newProduct))
    }
    if (method === 'put') {
      const id = parseInt(url.split('/').pop())
      const idx = mockProducts.findIndex(p => p.id === id)
      if (idx !== -1) {
        mockProducts[idx] = { ...mockProducts[idx], ...data }
        return mockResponse(transformProduct(mockProducts[idx]))
      }
    }
    if (method === 'delete') {
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
    if (method === 'get') {
      if (url.includes('/statistics')) {
        const stats = mockWarehouses.map(w => {
          const totalStock = mockInventories
            .filter(i => i.warehouseId === w.id)
            .reduce((sum, i) => sum + i.quantity, 0)
          return {
            id: w.id,
            warehouseName: w.name,
            totalStock,
            totalValue: w.totalValue
          }
        })
        return mockResponse(stats)
      }
      return mockResponse(mockWarehouses.map(transformWarehouse))
    }
    if (method === 'post') {
      const newWH = { ...data, id: mockWarehouses.length + 1 }
      mockWarehouses.push(newWH)
      return mockResponse(transformWarehouse(newWH))
    }
    if (method === 'put') {
      const id = parseInt(url.split('/').pop())
      const idx = mockWarehouses.findIndex(w => w.id === id)
      if (idx !== -1) {
        mockWarehouses[idx] = { ...mockWarehouses[idx], ...data }
        return mockResponse(transformWarehouse(mockWarehouses[idx]))
      }
    }
    if (method === 'delete') {
      const id = parseInt(url.split('/').pop())
      const idx = mockWarehouses.findIndex(w => w.id === id)
      if (idx !== -1) {
        mockWarehouses.splice(idx, 1)
        return mockResponse(null)
      }
    }
  }

  // 库存接口
  if (url.includes('/inventory')) {
    if (method === 'get') {
      const inventory = mockInventories.map(inv => {
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
      })
      return mockResponse(inventory)
    }
  }

  // 入库接口
  if (url.includes('/inbound')) {
    if (method === 'get') {
      return mockResponse(mockInboundRecords)
    }
    if (method === 'post') {
      const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
      const newRecord = {
        ...data,
        id: mockInboundRecords.length + 1,
        totalPrice: data.quantity * data.unitPrice,
        createdAt: now
      }
      mockInboundRecords.push(newRecord)
      const inv = mockInventories.find(i => i.productId === data.productId && i.warehouseId === data.warehouseId)
      if (inv) {
        inv.quantity += data.quantity
        inv.lastUpdated = now
      } else {
        mockInventories.push({
          id: mockInventories.length + 1,
          productId: data.productId,
          warehouseId: data.warehouseId,
          quantity: data.quantity,
          lastUpdated: now
        })
      }
      return mockResponse(newRecord)
    }
  }

  // 出库接口
  if (url.includes('/outbound')) {
    if (method === 'get') {
      return mockResponse(mockOutboundRecords)
    }
    if (method === 'post') {
      const inv = mockInventories.find(i => i.productId === data.productId && i.warehouseId === data.warehouseId)
      if (!inv || inv.quantity < data.quantity) {
        throw new Error('库存不足')
      }
      const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
      const newRecord = {
        ...data,
        id: mockOutboundRecords.length + 1,
        totalPrice: data.quantity * data.unitPrice,
        createdAt: now
      }
      mockOutboundRecords.push(newRecord)
      inv.quantity -= data.quantity
      inv.lastUpdated = now
      return mockResponse(newRecord)
    }
  }

  // 盘点接口
  if (url.includes('/check')) {
    if (url.includes('/records') && method === 'get') {
      return mockResponse(mockInventoryChecks)
    }
    if (method === 'post') {
      const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
      const newRecord = {
        ...data,
        id: mockInventoryChecks.length + 1,
        status: 'COMPLETED',
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
      return mockResponse(newRecord)
    }
  }

  // 用户接口
  if (url.includes('/users')) {
    if (method === 'get') {
      return mockResponse(mockUsers.map(u => ({ ...u, password: undefined })))
    }
    if (method === 'post') {
      const newUser = { ...data, id: mockUsers.length + 1, enabled: true }
      mockUsers.push(newUser)
      return mockResponse({ ...newUser, password: undefined })
    }
    if (method === 'put') {
      const id = parseInt(url.split('/').pop())
      const idx = mockUsers.findIndex(u => u.id === id)
      if (idx !== -1) {
        mockUsers[idx] = { ...mockUsers[idx], ...data }
        return mockResponse({ ...mockUsers[idx], password: undefined })
      }
    }
    if (method === 'delete') {
      const id = parseInt(url.split('/').pop())
      const idx = mockUsers.findIndex(u => u.id === id)
      if (idx !== -1) {
        mockUsers.splice(idx, 1)
        return mockResponse(null)
      }
    }
    if (method === 'patch') {
      const id = parseInt(url.split('/')[0].split('/').pop())
      const enabled = url.includes('enabled=true')
      const user = mockUsers.find(u => u.id === id)
      if (user) {
        user.enabled = enabled
        return mockResponse({ ...user, password: undefined })
      }
    }
  }

  // 报表统计接口
  if (url.includes('/reports') || url.includes('/data')) {
    if (url.includes('/trend')) {
      return mockResponse(mockStatistics.trendData)
    }
    if (url.includes('/category')) {
      return mockResponse(mockStatistics.categoryData)
    }
    return mockResponse(mockStatistics)
  }

  // 供应商接口
  if (url.includes('/suppliers')) {
    return mockResponse(mockSuppliers)
  }

  // 默认返回空数据
  return mockResponse([])
}

/**
 * 安装 Mock 拦截器到 axios 实例
 * 在 Mock 模式下，直接返回 Mock Promise，不发送真实请求
 */
export function installMockInterceptor(api) {
  api.interceptors.request.use((config) => {
    if (!isMockMode()) {
      return config
    }

    const url = config.url || ''
    const method = (config.method || 'get').toLowerCase()
    const data = config.data ? (typeof config.data === 'string' ? JSON.parse(config.data) : config.data) : {}

    // 直接返回 Mock 响应的 Promise
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
  })
}

// 导出检查函数
export { isMockMode }
