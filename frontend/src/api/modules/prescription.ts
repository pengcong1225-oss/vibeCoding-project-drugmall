import { http } from '../request'

export interface PrescriptionDrug {
  id?: string
  name: string
  spec: string
  unit?: string
  price?: number
  quantity?: number
  dosage?: string
  frequency?: string
  duration?: string
  days?: number
}

export interface Prescription {
  id: string
  prescriptionNo?: string
  patientId?: string
  patientName?: string
  patientAge?: number
  patientGender?: string
  consultationId?: string
  consultationStatus?: string
  consultationSymptom?: string
  consultationType?: string
  diagnosis: string
  drugs: PrescriptionDrug[]
  totalAmount?: number
  status: 'pending' | 'approved' | 'rejected'
  statusText: string
  createTime: string
  pharmacist?: string
  reviewTime?: string
  rejectReason?: string
}

export interface PrescriptionDetail extends Prescription {
  doctorAdvice?: string
  precautions?: string[]
}

export function getPrescriptionList(status?: string) {
  return http.get<Prescription[]>('/patient/prescriptions', { status })
}

export function getPrescriptionDetail(id: string) {
  return http.get<PrescriptionDetail>(`/patient/prescriptions/${id}`)
}
