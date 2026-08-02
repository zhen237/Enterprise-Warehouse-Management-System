/**
 * HTTP 请求封装
 * 
 * 策略：
 *   1. 创建独立的 Mock 客户端对象
 *   2. 根据 isMockMode() 动态切换
 *   3. 错误时自动降级为 Mock
 */
import axios from 'axios'
import { getMockResponse } from '@/mock/interceptor.js'
import { enableMockMode, isMockMode as checkMockMode } from '@/mock/data.js'

// ========== Mock 客户端 ==========
const mockClient = {
  get(url, config) {
    return handleMock(url, 'get', null)
  },
  post(url, data, config) {
    return handleMock(url, 'post', data)
  },
  put(url, data, config) {
    return handleMock(url, 'put', data)
  },
  delete(url, config) {
    return handleMock(url, 'delete', null)
  }
}

function handleMock(url, method, data) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      try {
        const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
        let fullUrl = url
        if (!url.startsWith('http')) {
          fullUrl = baseURL + (url.startsWith('/') ? url : '/' + url)
        }
        const result = getMockResponse(fullUrl, method, data || {})
        
        if (result?.code === 200) {
          resolve(result.data ?? result)
        } else if (result?.code !== undefined) {
          reject(new Error(result.message || '请求失败'))
        } else {
          resolve(result)
        }
      } catch (e) {
        reject(new Error(e.message || '请求失败'))
      }
    }, 30)
  })
}

// ========== 真实 axios 实例 ==========
const realApi = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

// 响应拦截 - 处理 Mock 格式 + 错误降级
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

    // 失败则降级到 Mock
    if (!checkMockMode()) {
      enableMockMode()
      console.warn('🚀 检测到后端不可用，自动切换到 Mock 演示模式')
    }
    
    try {
      const result = getMockResponse(url, method, data)
      if (result?.code === 200) {
        return result.data ?? result
      }
      return result
    } catch (e) {
      return Promise.reject(new Error(e.message || '请求失败'))
    }
  }
)

// ========== 导出：根据模式选择客户端 ==========
function apiGet(url, config) {
  return checkMockMode() ? mockClient.get(url, config) : realApi.get(url, config)
}

function apiPost(url, data, config) {
  return checkMockMode() ? mockClient.post(url, data, config) : realApi.post(url, data, config)
}

function apiPut(url, data, config) {
  return checkMockMode() ? mockClient.put(url, data, config) : realApi.put(url, data, config)
}

function apiDelete(url, config) {
  return checkMockMode() ? mockClient.delete(url, config) : realApi.delete(url, config)
}

const api = {
  get: apiGet,
  post: apiPost,
  put: apiPut,
  delete: apiDelete,
  // 暴露给其他地方使用
  axios: realApi
}

export function isMockMode() {
  return checkMockMode()
}

export default api
