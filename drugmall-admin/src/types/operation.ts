export interface Feedback {
  id: string
  userId: string
  userName: string
  type: 'suggestion' | 'bug' | 'feature' | 'other'
  content: string
  images: string[]
  contact?: string
  status: 'pending' | 'processing' | 'resolved' | 'closed'
  reply?: string
  handler?: string
  handleTime?: string
  createTime: string
}

export interface Complaint {
  id: string
  complainantId: string
  complainantName: string
  targetId: string
  targetName: string
  targetType: 'doctor' | 'store' | 'product' | 'order'
  type: 'service' | 'quality' | 'delivery' | 'price' | 'other'
  content: string
  evidence: string[]
  status: 'pending' | 'processing' | 'resolved' | 'rejected'
  result?: string
  handler?: string
  handleTime?: string
  createTime: string
}

export interface SymptomQuestion {
  id: string
  symptom: string
  department: string
  questions: {
    question: string
    options: string[]
    nextQuestion?: string
  }[]
  possibleDiseases: string[]
  suggestions: string
  status: 'active' | 'disabled'
  createTime: string
  updateTime: string
}

export interface AIConfig {
  id: string
  name: string
  type: 'knowledge' | 'qa' | 'recommendation'
  content: Record<string, any>
  status: 'active' | 'disabled'
  version: string
  updateTime: string
}

export interface FeedbackQuery {
  pageNum: number
  pageSize: number
  type?: string
  status?: string
  startDate?: string
  endDate?: string
}

export interface ComplaintQuery {
  pageNum: number
  pageSize: number
  type?: string
  status?: string
  targetType?: string
  startDate?: string
  endDate?: string
}

export interface SymptomQuestionQuery {
  pageNum: number
  pageSize: number
  symptom?: string
  department?: string
  status?: string
}

export interface AIConfigQuery {
  pageNum: number
  pageSize: number
  name?: string
  type?: string
  status?: string
}
