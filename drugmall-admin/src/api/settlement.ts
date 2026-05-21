import { request } from '@/utils/request'
import type { DoctorSettlement, SettlementQuery } from '@/types/settlement'

export function getSettlementList(params: SettlementQuery) {
  return request.get<{ list: DoctorSettlement[], total: number }>('/admin/finance/settlements', params)
}

export function auditSettlement(id: string, data: { status: 'approved' | 'rejected', remark?: string }) {
  return request.put(`/admin/finance/settlements/${id}/audit`, data)
}

export function getSettlementDetail(id: string) {
  return request.get<DoctorSettlement>(`/admin/finance/settlements/${id}`)
}
