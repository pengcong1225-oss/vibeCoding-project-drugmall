import { http } from '../request'

export interface Prescription {
  id: string
  prescriptionNo: string
  doctorName: string
  title?: string
  hospital: string
  department: string
  diagnosis: string
  status: 'pending' | 'active' | 'expired'
  statusText: string
  createTime: string
  drugs: PrescriptionDrug[]
}

export interface PrescriptionDrug {
  name: string
  spec: string
  quantity?: number
  usage?: string
  frequency?: string
  days?: number
}

export interface PrescriptionDetail extends Prescription {
  patientName: string
  patientAge: number
  patientGender: string
  doctorAdvice?: string
  reviewStatus?: string
  reviewTime?: string
}

export function getPrescriptionList(status?: string) {
  return http.get<Prescription[]>('/patient/prescriptions', { status })
}

export function getPrescriptionDetail(id: string) {
  return http.get<PrescriptionDetail>(`/patient/prescriptions/${id}`)
}
