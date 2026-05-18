import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { mockHomePageConfig, mockInquiryList, mockPatients, mockUserCenterData } from './mock'
import { ResponseCode } from '@/constants'
import { API_CONFIG, STORAGE_KEYS } from '@/constants/config'
import { ROUTES } from '@/constants/routes'
import { messages } from '@/constants/messages'

const ENABLE_MOCK = import.meta.env.VITE_ENABLE_MOCK === 'true' || import.meta.env.VITE_USE_MOCK === 'true'

// Mock响应构造器
const createMockResponse = (data: any) => ({
  __isMock: true,
  response: {
    data: {
      code: 200,
      message: 'success',
      data
    },
    headers: {},
    status: 200,
    statusText: 'OK'
  }
})

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: API_CONFIG.TIMEOUT,
  headers: {
    'Content-Type': API_CONFIG.CONTENT_TYPE
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

// Mock拦截器 - 在请求发送前拦截并返回mock数据（优先级最高）
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 如果启用mock，拦截特定请求
    if (ENABLE_MOCK) {
      const url = config.url || ''
      
      // 拦截首页渲染配置请求
      if (url.includes('/home/render/page')) {
        return Promise.reject(createMockResponse(mockHomePageConfig))
      }
      
      // 拦截问诊列表请求
      if (url.includes('/consultation/list')) {
        return Promise.reject(createMockResponse(mockInquiryList))
      }
      
      // 拦截就诊人列表请求
      if (url.includes('/patient/list')) {
        return Promise.reject(createMockResponse(mockPatients))
      }
      
      // 拦截用户中心数据请求
      if (url.includes('/user/center')) {
        return Promise.reject(createMockResponse(mockUserCenterData))
      }
      
      // 拦截订单统计请求
      if (url.includes('/order/stats')) {
        return Promise.reject(createMockResponse(mockUserCenterData.orderStats))
      }
    }
    
    // 移除重复请求
    removePending(config)
    // 添加新请求到队列
    addPending(config)
    
    // 添加token
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN)
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
    
    if (code === ResponseCode.SUCCESS || code === ResponseCode.SUCCESS_ALT) {
      return data
    }
    
    switch (code) {
      case ResponseCode.UNAUTHORIZED:
        localStorage.removeItem(STORAGE_KEYS.TOKEN)
        localStorage.removeItem(STORAGE_KEYS.USER_INFO)
        router.push(ROUTES.LOGIN)
        ElMessage.error(messages.auth.loginExpired)
        break
      case ResponseCode.FORBIDDEN:
        ElMessage.error(messages.auth.noPermission)
        break
      case ResponseCode.NOT_FOUND:
        ElMessage.error(messages.auth.resourceNotFound)
        break
      case ResponseCode.SERVER_ERROR:
        ElMessage.error(message || messages.common.serverBusy)
        break
      default:
        ElMessage.error(message || messages.common.operationFailed)
    }
    
    return Promise.reject(new Error(message || messages.common.requestFailed))
  },
  (error: any) => {
    if (error.__isMock && error.response) {
      const { code, data } = error.response.data
      if (code === ResponseCode.SUCCESS || code === ResponseCode.SUCCESS_ALT) {
        return Promise.resolve(data)
      }
    }
    
    // 移除请求从队列
    if (error.config) {
      removePending(error.config)
    }
    
    if (error.name === 'AbortError' || error.message === 'canceled') {
      return Promise.reject(new Error(messages.common.requestCancelled))
    }
    
    if (!error.response) {
      if (ENABLE_MOCK && error.config?.url?.includes('/home/render/page')) {
        return Promise.resolve(mockHomePageConfig)
      }
      return Promise.reject(new Error(messages.common.networkError))
    }
    
    const { status, data } = error.response as AxiosResponse
    const message = data?.message || messages.common.requestFailed
    
    switch (status) {
      case ResponseCode.UNAUTHORIZED:
        localStorage.removeItem(STORAGE_KEYS.TOKEN)
        localStorage.removeItem(STORAGE_KEYS.USER_INFO)
        router.push(ROUTES.LOGIN)
        ElMessage.error(messages.auth.loginExpired)
        break
      case ResponseCode.FORBIDDEN:
        ElMessage.error(messages.auth.noPermission)
        break
      case ResponseCode.NOT_FOUND:
        ElMessage.error(messages.auth.resourceNotFound)
        break
      case ResponseCode.REQUEST_TIMEOUT:
        ElMessage.error(messages.common.requestTimeout)
        break
      case ResponseCode.SERVER_ERROR:
      case ResponseCode.BAD_GATEWAY:
      case ResponseCode.SERVICE_UNAVAILABLE:
      case ResponseCode.GATEWAY_TIMEOUT:
        ElMessage.error(messages.common.serverBusy)
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
