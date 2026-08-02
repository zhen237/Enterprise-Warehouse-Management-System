/**
 * HTTP 请求封装
 * 
 * Mock 模式处理策略（最可靠方案）：
 *   - 创建独立的 Mock 客户端，完全绕过 axios
 *   - 根据 isMockMode() 动态切换
 *   - 双重保障：请求拦截器 + 错误降级
 */
import axios from 'axios'
import { getMockResponse } from '@/mock/interceptor.js'
import { enableMockMode, isMockMode as checkMockMode } from '@/mock/data.js'

// ========== Mock 客户端 ==========
// 一个简单的对象，模拟 axios 的 get/post/put/delete 方法
const mockClient = {
  get(url, config) {
    return handleMockRequest(url, 'get', null, config)
  },
  post(url, data, config) {
    return handleMockRequest(url, 'post', data, config)
  },
  put(url, data, config) {
    return handleMockRequest(url, 'put', data, config)
  },
  delete(url, config) {
    return handleMockRequest(url, 'delete', null, config)
  },
  request(config) {
    return handleMockRequest(config.url, (config.method || 'get').toLowerCase(), config.data, config)
  }
}

function handleMockRequest(url, method, data, config) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      try {
        const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
        const fullUrl = url.startsWith('http') ? url : (url.startsWith('/') ? baseURL + url : baseURL + '/' + url)
        const mockResult = getMockResponse(fullUrl, method, data || {})
        
        // 处理响应格式
        if (mockResult?.code === 200) {
          resolve(mockResult.data ?? mockResult)
        } else if (mockResult?.code !== undefined) {
          reject(new Error(mockResult.message || '请求失败'))
        } else {
          resolve(mockResult)
        }
      } catch (error) {
        reject(new Error(error.message || '请求失败'))
      }
    }, 50)
  })
}

// ========== 真实 axios 实例 ==========
const realApi = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

// 响应拦截器 - 错误时尝试 Mock 降级
realApi.interceptors.response.use(
  response => {
    if (response.data?.code !== undefined) {
      if (response.data.code === 200) {
        return response.data.data ?? response.data
      } else {
        return Promise.reject(new Error(response.data.message || '请求失败'))
      }
    }
    return response.data
  },
  async error => {
    const config = error.config || {}
    const url = config.url || ''
    const method = (config.method || 'get').toLowerCase()
    const data = config.data ? (typeof config.data === 'string' ? JSON.parse(config.data) : config.data) : {}

    // 请求失败 → 自动降级到 Mock 模式
    try {
      if (!checkMockMode()) {
        enableMockMode()
        console.warn('🚀 检测到后端不可用，自动切换到 Mock 演示模式')
      }
      const mockData = getMockResponse(url, method, data)
      if (mockData?.code === 200) {
        return mockData.data ?? mockData
      }
      return mockData
    } catch (mockError) {
      return Promise.reject(new Error(mockError.message || '请求失败'))
    }
  }
)

// ========== 导出智能客户端 ==========
// 根据当前模式自动选择 Mock 或真实客户端
const api = new Proxy(realApi, {
  get(target, prop) {
    if (prop === 'get' || prop === 'post' || prop === 'put' || prop === 'delete' || prop === 'request') {
      return function(...args) {
        if (checkMockMode()) {
          return mockClient[prop](...args)
        }
        return target[prop](...args)
      }
    }
    return target[prop]
  }
})

// 重新导出
export function isMockMode() {
  return checkMockMode()
}

export default api
