import { request } from '@/utils/request'
import type {
  PrescriptionInfo,
  PrescriptionDetail,
  PreCheckResult,
  AuditRecord,
  PrescriptionTemplate,
  PrescriptionQueryParams,
  TemplateQueryParams
} from '@/types/prescription'

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

// 处方列表
export function getPrescriptionList(params: PrescriptionQueryParams): Promise<PageResult<PrescriptionInfo>> {
  return request.get('/admin/prescriptions', params)
}

export function getPrescriptionDetail(id: number): Promise<PrescriptionDetail> {
  return request.get(`/admin/prescriptions/${id}`)
}

export function cancelPrescription(id: number): Promise<void> {
  return request.put(`/admin/prescriptions/${id}/cancel`)
}

// 处方审核
export function getAuditList(params?: PrescriptionQueryParams): Promise<PageResult<PrescriptionInfo>> {
  return request.get('/admin/prescriptions/audit', params)
}

export function getAuditDetail(id: number): Promise<PrescriptionDetail> {
  return request.get(`/admin/prescriptions/audit/${id}`)
}

export function auditPrescription(id: number, data: { result: string; opinion?: string; suggestion?: string }): Promise<void> {
  return request.put(`/admin/prescriptions/audit/${id}`, data)
}

export function getPreCheckResult(id: number): Promise<PreCheckResult> {
  return request.get(`/admin/prescriptions/${id}/pre-check`)
}

export function getAuditLogs(id: number): Promise<AuditRecord[]> {
  return request.get(`/admin/prescriptions/${id}/audit-logs`)
}

// 处方模板
export function getTemplateList(params?: TemplateQueryParams): Promise<PageResult<PrescriptionTemplate>> {
  return request.get('/admin/prescriptions/templates', params)
}

export function createTemplate(data: Partial<PrescriptionTemplate>): Promise<void> {
  return request.post('/admin/prescriptions/templates', data)
}

export function updateTemplate(id: number, data: Partial<PrescriptionTemplate>): Promise<void> {
  return request.put(`/admin/prescriptions/templates/${id}`, data)
}

export function deleteTemplate(id: number): Promise<void> {
  return request.delete(`/admin/prescriptions/templates/${id}`)
}

export function copyTemplate(id: number): Promise<void> {
  return request.post(`/admin/prescriptions/templates/${id}/copy`)
}

// 处方统计
export function getPrescriptionStats(params?: { timeRange?: string; startTime?: string; endTime?: string }): Promise<any> {
  return request.get('/admin/prescriptions/stats', params)
}

export function getPrescriptionTrend(params?: { timeRange?: string }): Promise<any> {
  return request.get('/admin/prescriptions/stats/trend', params)
}

export function getDepartmentRanking(params?: { timeRange?: string }): Promise<any[]> {
  return request.get('/admin/prescriptions/stats/departments', params)
}

export function getDoctorRanking(params?: { timeRange?: string }): Promise<any[]> {
  return request.get('/admin/prescriptions/stats/doctors', params)
}

export function getDrugRanking(params?: { timeRange?: string }): Promise<any[]> {
  return request.get('/admin/prescriptions/stats/drugs', params)
}
