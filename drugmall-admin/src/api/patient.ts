import { request } from '@/utils/request'
import type { Patient, PatientQuery } from '@/types/patient'

export function getPatientList(params: PatientQuery) {
  return request.get<{ list: Patient[], total: number }>('/admin/patients', params)
}

export function getPatientDetail(id: string) {
  return request.get<Patient>(`/admin/patients/${id}`)
}

export function getPatientHealthRecord(patientId: string) {
  return request.get(`/admin/patients/${patientId}/health-record`)
}
