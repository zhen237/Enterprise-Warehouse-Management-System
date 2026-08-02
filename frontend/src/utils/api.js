/**
 * Axios HTTP 请求封装
 * 
 * Mock 模式：
 *   - GitHub Pages 等无后端环境下自动启用
 *   - 首次请求失败时自动降级到 Mock 模式
 * 
 * 超时时间：10 秒
 */
import axios from 'axios'
import { installMockInterceptor, isMockMode } from '@/mock/interceptor.js'
import { enableMockMode } from '@/mock/data.js'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

// 安装 Mock 拦截器
installMockInterceptor(api)

// 标记是否正在降级中
let isDegrading = false

// 响应拦截器
api.interceptors.response.use(
  response => {
    // Mock 响应处理：getMockResponse 返回的是 {code, data, message} 格式
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
    // 如果已经是 Mock 模式，直接返回错误
    if (isMockMode()) {
      return Promise.reject(new Error(error.message || '请求失败'))
    }
    
    // 请求失败，自动降级到 Mock 模式
    if (!isDegrading && !error.config?._mockTried) {
      isDegrading = true
      enableMockMode()
      console.warn('🚀 检测到后端不可用，自动切换到 Mock 演示模式')
      
      try {
        // 用 Mock 数据重试
        const { getMockResponse } = await import('@/mock/interceptor.js')
        const url = error.config.url || ''
        const method = (error.config.method || 'get').toLowerCase()
        const data = error.config.data ? (typeof error.config.data === 'string' ? JSON.parse(error.config.data) : error.config.data) : {}
        
        const mockData = getMockResponse(url, method, data)
        isDegrading = false
        
        // 模拟响应拦截器处理
        if (mockData?.code === 200) {
          return mockData.data ?? mockData
        }
        return mockData
      } catch (mockError) {
        isDegrading = false
        return Promise.reject(new Error(mockError.message || '请求失败'))
      }
    }
    
    isDegrading = false
    if (error.response) {
      const message = error.response.data?.message || '请求失败'
      return Promise.reject(new Error(message))
    }
    return Promise.reject(new Error('网络连接失败'))
  }
)

// 导出 Mock 模式检查函数
export { isMockMode }

export default api
