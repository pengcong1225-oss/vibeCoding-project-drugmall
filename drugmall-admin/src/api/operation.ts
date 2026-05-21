import { request } from '@/utils/request'
import type { 
  Feedback, Complaint, SymptomQuestion, AIConfig, 
  FeedbackQuery, ComplaintQuery, SymptomQuestionQuery, AIConfigQuery 
} from '@/types/operation'

export function getFeedbackList(params: FeedbackQuery) {
  return request.get<{ list: Feedback[], total: number }>('/admin/feedbacks', params)
}

export function replyFeedback(id: string, data: { reply: string }) {
  return request.put(`/admin/feedbacks/${id}/reply`, data)
}

export function getComplaintList(params: ComplaintQuery) {
  return request.get<{ list: Complaint[], total: number }>('/admin/complaints', params)
}

export function handleComplaint(id: string, data: { result: string, status: string }) {
  return request.put(`/admin/complaints/${id}/handle`, data)
}

export function getSymptomQuestionList(params: SymptomQuestionQuery) {
  return request.get<{ list: SymptomQuestion[], total: number }>('/admin/symptoms/questions', params)
}

export function saveSymptomQuestion(data: Partial<SymptomQuestion>) {
  return request.post('/admin/symptoms/questions', data)
}

export function deleteSymptomQuestion(id: string) {
  return request.delete(`/admin/symptoms/questions/${id}`)
}

export function getAIConfigList(params?: AIConfigQuery) {
  return request.get<{ list: AIConfig[], total: number }>('/admin/ai/configs', params)
}

export function saveAIConfig(data: Partial<AIConfig>) {
  return request.post('/admin/ai/configs', data)
}

export function updateAIConfig(id: string, data: Partial<AIConfig>) {
  return request.put(`/admin/ai/configs/${id}`, data)
}

export function deleteAIConfig(id: string) {
  return request.delete(`/admin/ai/configs/${id}`)
}
