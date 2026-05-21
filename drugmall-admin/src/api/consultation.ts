import { request } from '@/utils/request'
import type {
  ConsultationInfo,
  ConsultationDetail,
  ConsultationMessage,
  AvailableDoctor,
  ConsultationException,
  ConsultationQueryParams,
  ExceptionQueryParams
} from '@/types/consultation'

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

// 问诊列表
export function getConsultationList(params: ConsultationQueryParams): Promise<PageResult<ConsultationInfo>> {
  return request.get('/admin/consultations', params)
}

export function getConsultationDetail(id: number): Promise<ConsultationDetail> {
  return request.get(`/admin/consultations/${id}`)
}

export function getConsultationMessages(id: number): Promise<ConsultationMessage[]> {
  return request.get(`/admin/consultations/${id}/messages`)
}

export function cancelConsultation(id: number): Promise<void> {
  return request.put(`/admin/consultations/${id}/cancel`)
}

export function refundConsultation(id: number, data?: { reason?: string }): Promise<void> {
  return request.post(`/admin/consultations/${id}/refund`, data)
}

// 问诊分配
export function getAvailableDoctors(id: number): Promise<AvailableDoctor[]> {
  return request.get(`/admin/consultations/${id}/available-doctors`)
}

export function assignConsultation(id: number, data: { doctorId: number; reason?: string }): Promise<void> {
  return request.put(`/admin/consultations/${id}/assign`, data)
}

export function getAssignRules(): Promise<any> {
  return request.get('/admin/consultations/assign-rules')
}

export function updateAssignRules(data: any): Promise<void> {
  return request.put('/admin/consultations/assign-rules', data)
}

// 问诊统计
export function getConsultationStats(params?: { timeRange?: string; startTime?: string; endTime?: string }): Promise<any> {
  return request.get('/admin/consultations/stats', params)
}

export function getConsultationTrend(params?: { timeRange?: string; startTime?: string; endTime?: string }): Promise<any> {
  return request.get('/admin/consultations/stats/trend', params)
}

export function getDepartmentRanking(params?: { timeRange?: string }): Promise<any[]> {
  return request.get('/admin/consultations/stats/departments', params)
}

export function getDoctorRanking(params?: { timeRange?: string }): Promise<any[]> {
  return request.get('/admin/consultations/stats/doctors', params)
}

export function getHourlyDistribution(params?: { timeRange?: string }): Promise<any[]> {
  return request.get('/admin/consultations/stats/hourly', params)
}

// 异常问诊
export function getExceptionList(params: ExceptionQueryParams): Promise<PageResult<ConsultationException>> {
  return request.get('/admin/consultations/exceptions', params)
}

export function getExceptionDetail(id: number): Promise<ConsultationException> {
  return request.get(`/admin/consultations/exceptions/${id}`)
}

export function handleException(id: number, data: { method: string; note: string; compensationAmount?: number }): Promise<void> {
  return request.put(`/admin/consultations/exceptions/${id}/handle`, data)
}

export function getExceptionStats(): Promise<any> {
  return request.get('/admin/consultations/exceptions/stats')
}
