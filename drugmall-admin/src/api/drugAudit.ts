import { request } from '@/utils/request'
import type { DrugAudit, DrugAuditQuery } from '@/types/drugAudit'

export function getDrugAuditList(params: DrugAuditQuery) {
  return request.get<{ list: DrugAudit[], total: number }>('/admin/drugs/audit', params)
}

export function auditDrug(id: string, data: { status: 'approved' | 'rejected', comment?: string }) {
  return request.put(`/admin/drugs/audit/${id}`, data)
}

export function getDrugAuditDetail(id: string) {
  return request.get<DrugAudit>(`/admin/drugs/audit/${id}`)
}
