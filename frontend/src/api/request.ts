import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求队列（用于取消重复请求）
const pendingMap = new Map<string, AbortController>()

// 生成请求key
const getRequestKey = (config: InternalAxiosRequestConfig): string => {
  return `${config.method}&${config.url}&${JSON.stringify(config.params)}&${JSON.stringify(config.data)}`
}

// 添加请求到队列
const addPending = (config: InternalAxiosRequestConfig): void => {
  const key = getRequestKey(config)
  const controller = new AbortController()
  config.signal = controller.signal
  
  if (!pendingMap.has(key)) {
    pendingMap.set(key, controller)
  }
}

// 移除请求从队列
const removePending = (config: InternalAxiosRequestConfig): void => {
  const key = getRequestKey(config)
  if (pendingMap.has(key)) {
    const controller = pendingMap.get(key)
    controller?.abort()
    pendingMap.delete(key)
  }
}

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 移除重复请求
    removePending(config)
    // 添加新请求到队列
    addPending(config)
    
    // 添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 添加时间戳（防止缓存）
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }
    
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    // 移除请求从队列
    removePending(response.config)
    
    const { code, message, data } = response.data
    
    // 成功响应
    if (code === 200 || code === 0) {
      return data
    }
    
    // 特殊错误处理
    switch (code) {
      case 401:
        // 未授权，清除token并跳转到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
        break
      case 403:
        ElMessage.error('没有权限执行此操作')
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 500:
        ElMessage.error(message || '服务器内部错误')
        break
      default:
        ElMessage.error(message || '操作失败')
    }
    
    return Promise.reject(new Error(message || '请求失败'))
  },
  (error: AxiosError) => {
    // 移除请求从队列
    if (error.config) {
      removePending(error.config)
    }
    
    // 请求被取消
    if (error.name === 'AbortError' || error.message === 'canceled') {
      return Promise.reject(new Error('请求已取消'))
    }
    
    // 网络错误
    if (!error.response) {
      ElMessage.error('网络错误，请检查网络连接')
      return Promise.reject(new Error('网络错误'))
    }
    
    // 根据状态码处理错误
    const { status, data } = error.response as AxiosResponse
    const message = data?.message || '请求失败'
    
    switch (status) {
      case 400:
        ElMessage.error(message)
        break
      case 401:
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
        break
      case 403:
        ElMessage.error('没有权限执行此操作')
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 408:
        ElMessage.error('请求超时，请稍后重试')
        break
      case 500:
      case 502:
      case 503:
      case 504:
        ElMessage.error('服务器繁忙，请稍后重试')
        break
      default:
        ElMessage.error(message)
    }
    
    return Promise.reject(error)
  }
)

// 导出请求方法
export const http = {
  get: <T>(url: string, params?: object) => {
    return request.get<T, T>(url, { params })
  },
  post: <T>(url: string, data?: object) => {
    return request.post<T, T>(url, data)
  },
  put: <T>(url: string, data?: object) => {
    return request.put<T, T>(url, data)
  },
  delete: <T>(url: string, params?: object) => {
    return request.delete<T, T>(url, { params })
  },
  patch: <T>(url: string, data?: object) => {
    return request.patch<T, T>(url, data)
  }
}

export default request
