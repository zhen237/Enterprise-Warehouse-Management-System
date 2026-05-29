import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.response.use(
  response => {
    if (response.data.code === 200) {
      return response.data.data
    } else {
      return Promise.reject(new Error(response.data.message || '请求失败'))
    }
  },
  error => {
    if (error.response) {
      const message = error.response.data?.message || '请求失败'
      return Promise.reject(new Error(message))
    }
    return Promise.reject(error)
  }
)

export default api