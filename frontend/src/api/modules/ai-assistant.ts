import axios from 'axios'
import type { Result } from '../request'

// 创建AI助手专用的axios实例（使用 /api 前缀）
const aiRequest = axios.create({
  baseURL: '/api',
  timeout: 60000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加token
aiRequest.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
aiRequest.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (!error.response) {
      return Promise.reject(new Error('网络错误，请检查后端服务是否启动'))
    }
    return Promise.reject(error)
  }
)

/**
 * AI对话请求参数
 */
export interface AIChatParams {
  message: string
  sessionId?: string
  history?: ChatMessage[]
  stream?: boolean
}

/**
 * 对话消息
 */
export interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
}

/**
 * AI对话响应
 */
export interface AIChatResponse {
  content: string
  sessionId: string
  drugs?: RecommendedDrug[]
  showActions?: boolean
  usage?: TokenUsage
}

/**
 * 推荐药品
 */
export interface RecommendedDrug {
  id: string
  name: string
  image: string
  price: number
  spec: string
  manufacturer: string
  isRx: boolean
}

/**
 * Token使用统计
 */
export interface TokenUsage {
  promptTokens: number
  completionTokens: number
  totalTokens: number
}

/**
 * 文件上传响应
 */
export interface FileUploadResponse {
  id: string
  filename: string
  bytes: number
  createdAt: number
  purpose: string
  status?: string
  content?: string
  recognizeType?: string
  recognizeResult?: any
}

/**
 * 症状自测请求参数
 */
export interface SymptomTestParams {
  symptoms: string[]
  bodyPart?: string
  duration?: string
  severity?: string
  description?: string
}

/**
 * 症状自测响应
 */
export interface SymptomTestResponse {
  possibleConditions?: string[]
  advice: string
  recommendedDrugs?: RecommendedDrug[]
  precautions?: string[]
  needDoctor?: boolean
  urgencyLevel?: string
  nextSteps?: string[]
}

/**
 * AI助手API
 */
export const aiAssistantApi = {
  /**
   * 发送消息给AI助手
   */
  chat(params: AIChatParams) {
    return aiRequest.post<Result<AIChatResponse>>('/ai/chat', params)
  },

  /**
   * 清除会话历史
   */
  clearSession(sessionId: string) {
    return aiRequest.delete<Result<void>>(`/ai/session/${sessionId}`)
  },

  /**
   * 上传文件（处方/药品图片）
   */
  uploadFile(file: File, purpose: string = 'medical') {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('purpose', purpose)

    return aiRequest.post<Result<FileUploadResponse>>('/ai/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 获取文件解析内容
   */
  getFileContent(fileId: string) {
    return aiRequest.get<Result<FileUploadResponse>>(`/ai/file/${fileId}`)
  },

  /**
   * 症状自测
   */
  symptomTest(params: SymptomTestParams) {
    return aiRequest.post<Result<SymptomTestResponse>>('/ai/symptom-test', params)
  }
}
