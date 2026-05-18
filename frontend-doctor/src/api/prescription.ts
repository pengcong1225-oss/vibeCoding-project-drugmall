import request from '@/utils/request'

export interface PrescriptionDrug {
  id: string
  name: string
  spec: string
  unit: string
  price: number
  quantity: number
  dosage: string
  frequency: string
  duration: string
  remark?: string
}

export interface Prescription {
  id: string
  patientId: string
  patientName: string
  patientAge: number
  patientGender: string
  consultationId: string
  consultationStatus?: string
  consultationSymptom?: string
  consultationType?: string
  diagnosis: string
  drugs: PrescriptionDrug[]
  totalAmount: number
  status: 'pending' | 'approved' | 'rejected'
  statusText: string
  createTime: string
  pharmacist?: string
  reviewTime?: string
  rejectReason?: string
}

export interface CreatePrescriptionDTO {
  patientId: string
  consultationId: string
  diagnosis: string
  drugs: Array<{
    name: string
    spec: string
    quantity: number
    dosage: string
    frequency: string
    duration: string
    price: number
  }>
}

// 获取处方列表
export function getPrescriptionList(status: string = 'all') {
  return request<Prescription[]>({
    url: '/prescriptions',
    method: 'get',
    params: { status }
  })
}

// 获取处方详情
export function getPrescriptionDetail(id: string) {
  return request<Prescription>({
    url: `/prescriptions/${id}`,
    method: 'get'
  })
}

// 创建处方
export function createPrescription(data: CreatePrescriptionDTO) {
  return request<Prescription>({
    url: '/prescriptions',
    method: 'post',
    data
  })
}
