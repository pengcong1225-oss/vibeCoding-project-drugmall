import axios, { AxiosError, AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response
    
    // 根据后端的响应结构判断
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      
      // 处理特定错误码
      if (data.code === 401) {
        // token过期，清除登录状态并跳转到登录页
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    
    return data.data
  },
  (error: AxiosError) => {
    console.error('Response error:', error)
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          ElMessage.error(data?.message || '请求参数错误')
          break
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('没有权限执行此操作')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error(data?.message || '服务器内部错误')
          break
        default:
          ElMessage.error('网络错误')
      }
    } else if (error.request) {
      ElMessage.error('网络请求超时，请稍后重试')
    } else {
      ElMessage.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export default service

// 封装请求方法
export const request = {
  get: <T>(url: string, params?: object) => {
    return service.get<T>(url, { params }) as Promise<T>
  },
  post: <T>(url: string, data?: object) => {
    return service.post<T>(url, data) as Promise<T>
  },
  put: <T>(url: string, data?: object) => {
    return service.put<T>(url, data) as Promise<T>
  },
  delete: <T>(url: string, params?: object) => {
    return service.delete<T>(url, { params }) as Promise<T>
  }
}
