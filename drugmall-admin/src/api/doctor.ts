import { request } from '@/utils/request'
import type {
  DoctorInfo,
  DoctorDetail,
  DoctorAudit,
  ScheduleInfo,
  DepartmentInfo,
  DoctorReview,
  DoctorStats,
  DoctorQueryParams,
  AuditQueryParams,
  ScheduleQueryParams,
  DepartmentQueryParams,
  ReviewQueryParams
} from '@/types/doctor'

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

// 医生列表
export function getDoctorList(params: DoctorQueryParams): Promise<PageResult<DoctorInfo>> {
  return request.get('/admin/doctors', params)
}

export function getDoctorDetail(id: number): Promise<DoctorDetail> {
  return request.get(`/admin/doctors/${id}`)
}

export function updateDoctor(id: number, data: Partial<DoctorInfo>): Promise<void> {
  return request.put(`/admin/doctors/${id}`, data)
}

export function updateDoctorStatus(id: number, status: number): Promise<void> {
  return request.patch(`/admin/doctors/${id}/status`, { status })
}

export function getDoctorStats(id: number): Promise<DoctorStats> {
  return request.get(`/admin/doctors/${id}/stats`)
}

// 入驻审核
export function getAuditList(params: AuditQueryParams): Promise<PageResult<DoctorAudit>> {
  return request.get('/admin/doctors/audit', params)
}

export function getAuditDetail(id: number): Promise<any> {
  return request.get(`/admin/doctors/audit/${id}`)
}

export function auditDoctor(id: number, data: { result: string; opinion?: string }): Promise<void> {
  return request.put(`/admin/doctors/audit/${id}`, data)
}

export function getAuditLogs(id: number): Promise<any[]> {
  return request.get(`/admin/doctors/audit/${id}/logs`)
}

// 排班管理
export function getScheduleList(doctorId: number, params?: ScheduleQueryParams): Promise<ScheduleInfo[]> {
  return request.get(`/admin/doctors/${doctorId}/schedule`, params)
}

export function createSchedule(doctorId: number, data: Partial<ScheduleInfo>): Promise<void> {
  return request.post(`/admin/doctors/${doctorId}/schedule`, data)
}

export function updateSchedule(doctorId: number, id: number, data: Partial<ScheduleInfo>): Promise<void> {
  return request.put(`/admin/doctors/${doctorId}/schedule/${id}`, data)
}

export function deleteSchedule(doctorId: number, id: number): Promise<void> {
  return request.delete(`/admin/doctors/${doctorId}/schedule/${id}`)
}

export function getDoctorSchedule(doctorId: number): Promise<ScheduleInfo[]> {
  return request.get(`/admin/doctors/${doctorId}/schedule`)
}

export function batchCreateSchedule(doctorId: number, data: Partial<ScheduleInfo>[]): Promise<void> {
  return request.post(`/admin/doctors/${doctorId}/schedule/batch`, data)
}

export function suspendSchedule(doctorId: number, data: { startDate: string; endDate: string; reason: string; autoReply?: string }): Promise<void> {
  return request.post(`/admin/doctors/${doctorId}/schedule/suspend`, data)
}

// 科室管理
export function getDepartmentList(params?: DepartmentQueryParams): Promise<DepartmentInfo[]> {
  return request.get('/admin/departments', params)
}

export function createDepartment(data: Partial<DepartmentInfo>): Promise<void> {
  return request.post('/admin/departments', data)
}

export function updateDepartment(id: number, data: Partial<DepartmentInfo>): Promise<void> {
  return request.put(`/admin/departments/${id}`, data)
}

export function deleteDepartment(id: number): Promise<void> {
  return request.delete(`/admin/departments/${id}`)
}

export function getDepartmentDoctors(id: number): Promise<any[]> {
  return request.get(`/admin/departments/${id}/doctors`)
}

export function assignDoctorsToDepartment(id: number, doctorIds: number[]): Promise<void> {
  return request.post(`/admin/departments/${id}/doctors`, { doctorIds })
}

// 评价管理
export function getReviewList(params: ReviewQueryParams): Promise<PageResult<DoctorReview>> {
  return request.get('/admin/doctors/reviews', params)
}

export function getReviewDetail(id: number): Promise<DoctorReview> {
  return request.get(`/admin/doctors/reviews/${id}`)
}

export function replyReview(id: number, data: { content: string }): Promise<void> {
  return request.post(`/admin/doctors/reviews/${id}/reply`, data)
}

export function hideReview(id: number): Promise<void> {
  return request.put(`/admin/doctors/reviews/${id}/hide`)
}

export function getNegativeReviews(params: ReviewQueryParams): Promise<PageResult<DoctorReview>> {
  return request.get('/admin/doctors/reviews/negative', params)
}

export function handleNegativeReview(id: number, data: { method: string; result: string; note: string }): Promise<void> {
  return request.put(`/admin/doctors/reviews/${id}/handle`, data)
}
