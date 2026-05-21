import { request } from '@/utils/request'
import type { ConsultationStats, PrescriptionStats } from '@/types/stats'

export function getConsultationStats(params?: { startDate?: string, endDate?: string }) {
  return request.get<ConsultationStats>('/admin/consultations/stats', params)
}

export function getPrescriptionStats(params?: { startDate?: string, endDate?: string }) {
  return request.get<PrescriptionStats>('/admin/prescriptions/stats', params)
}
