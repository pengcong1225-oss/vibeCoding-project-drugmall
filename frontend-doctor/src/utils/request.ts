/**
 * HTTP请求封装
 */
import axios, { type AxiosRequestConfig, type AxiosResponse } from 'axios'

// 简单的消息提示函数
const showMessage = (message: string, type: 'success' | 'warning' | 'error' | 'info' = 'info') => {
  console.log(`[Request ${type.toUpperCase()}] ${message}`)
}

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    
    // 根据code判断请求是否成功
    if (res.code === 200 || res.code === 0) {
      return res.data
    } else {
      // 显示错误消息
      showMessage(res.message || '请求失败', 'error')
      if (res.code === 401) {
        // 未登录或token过期
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  (error) => {
    console.error('响应错误:', error)
    
    // 处理HTTP错误
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          showMessage('未登录或登录已过期', 'error')
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          showMessage('没有权限执行此操作', 'error')
          break
        case 404:
          showMessage('请求的资源不存在', 'error')
          break
        case 500:
          showMessage(data.message || '服务器内部错误', 'error')
          break
        default:
          showMessage(data.message || '请求失败', 'error')
      }
    } else if (error.request) {
      showMessage('网络错误，请检查网络连接', 'error')
    } else {
      showMessage('请求配置错误', 'error')
    }
    
    return Promise.reject(error)
  }
)

// 封装请求方法
export interface RequestOptions extends AxiosRequestConfig {
  url: string
  method?: 'get' | 'post' | 'put' | 'delete' | 'patch'
  params?: any
  data?: any
}

export default function request<T = any>(options: RequestOptions): Promise<T> {
  return service(options)
}
