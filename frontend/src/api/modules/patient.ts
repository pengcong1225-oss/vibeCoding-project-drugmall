import { http } from '../request'
import type { Patient } from '@/types'

export interface PatientInfo {
  id: string
  name: string
  gender: string
  age: number
  idCard: string
  phone: string
  birthday?: string
  relationship?: string
  isDefault?: boolean
  avatar?: string
}

export function getPatientList() {
  return http.get<PatientInfo[]>('/user/patients')
}

export function getDefaultPatient() {
  return http.get<PatientInfo | null>('/user/patients/default')
}

export function addPatient(data: Omit<PatientInfo, 'id'>) {
  return http.post<PatientInfo>('/user/patients', data)
}

export function updatePatient(id: string, data: Partial<PatientInfo>) {
  return http.put<PatientInfo>(`/user/patients/${id}`, data)
}

export function deletePatient(id: string) {
  return http.delete(`/user/patients/${id}`)
}

export function setDefaultPatient(id: string) {
  return http.put(`/user/patients/${id}/default`)
}
