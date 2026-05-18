import request from '@/utils/request'

export interface DoctorConsultation {
  id: string
  patientId: string
  patientName: string
  patientAge: number
  patientGender: '男' | '女'
  patientAvatar: string
  type: string
  status: 'pending' | 'processing' | 'completed' | 'closed'
  symptom: string
  waitTime: string
  remainingTime: string
  isUrgent: boolean
  isRx: boolean
  createTime: string
}

export interface ConsultationMessage {
  id: string
  consultationId: string
  sender: 'doctor' | 'patient' | 'system'
  type: 'text' | 'image' | 'voice' | 'prescription'
  content: string
  time: string
  status?: 'sending' | 'sent' | 'read'
}

export interface Drug {
  id: string | number
  productName?: string  // 后端返回的字段名
  name?: string         // 兼容旧代码
  specification?: string
  price?: number
  unit?: string
  description?: string
  manufacturer?: string
  productCode?: string
  categoryId?: number
  brandId?: number
  mainImage?: string
  images?: string
  genericName?: string
  brand?: string
  approvalNumber?: string
  barCode?: string
  medicalInsuranceCode?: string
  traceabilityCode?: string
  isLongPrescription?: boolean
  insuranceCategory?: string
  originalPrice?: number
  stock?: number
  warningStock?: number
  sales?: number
}

// 获取医生问诊列表
export function getDoctorConsultations(status?: string) {
  return request<DoctorConsultation[]>({
    url: '/consultations',
    method: 'get',
    params: { status }
  })
}

// 获取问诊详情
export function getDoctorConsultationDetail(id: string) {
  return request<DoctorConsultation>({
    url: `/consultations/${id}`,
    method: 'get'
  })
}

// 接诊
export function acceptConsultation(id: string) {
  return request<{ success: boolean; consultationId: string; status: string }>({
    url: `/consultations/${id}/accept`,
    method: 'post'
  })
}

// 拒绝接诊
export function rejectConsultation(id: string, reason: string) {
  return request<{ success: boolean; consultationId: string; status: string }>({
    url: `/consultations/${id}/reject`,
    method: 'post',
    data: { reason }
  })
}

// 完成问诊
export function completeConsultation(id: string) {
  return request<boolean>({
    url: `/consultations/${id}/end`,
    method: 'post'
  })
}

// 获取问诊消息
export function getConsultationMessages(id: string) {
  return request<ConsultationMessage[]>({
    url: `/consultations/${id}/messages`,
    method: 'get'
  })
}

// 发送消息
export function sendConsultationMessage(id: string, data: { type: string; content: string }) {
  return request<ConsultationMessage>({
    url: `/consultations/${id}/messages`,
    method: 'post',
    data
  })
}

// 获取患者申请的药品列表
export function getRequestedDrugs(consultationId: string) {
  return request<Drug[]>({
    url: `/consultations/${consultationId}/requested-drugs`,
    method: 'get'
  })
}
