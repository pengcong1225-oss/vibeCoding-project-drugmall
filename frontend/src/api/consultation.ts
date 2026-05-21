import { http } from './request'

export interface ConsultationApplyRequest {
  drugId: string
  specificationId?: number  // 规格ID
  patientId: number
  diseases: string
  symptoms?: string
}

export interface ConsultationApplyResponse {
  consultationId: string
  status: string
  doctorId?: string
}

// 申请处方药（创建问诊）
export function applyPrescription(data: ConsultationApplyRequest) {
  return http.post<ConsultationApplyResponse>('/patient/consultations/apply-prescription', data)
}

// 检查医生接诊状态
export function checkDoctorAcceptance(consultationId: string) {
  return http.get<{ accepted: boolean; status: string; doctorId: string }>(`/patient/consultations/${consultationId}/acceptance`)
}

// 获取患者问诊列表
export function getPatientConsultations(status?: string) {
  return http.get('/patient/consultations', { status })
}
