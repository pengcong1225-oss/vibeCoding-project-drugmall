import request from '@/utils/request'

export interface PatientDetail {
  id: string
  name: string
  age: number
  gender: string
  phone: string
  avatar: string
  tags: string[]
  diagnosis: string[]
  lastVisit: string
  visitCount: number
  isVip: boolean
  allergies: string
  medicalHistory: string
}

// 获取患者详情
export function getPatientDetail(id: string) {
  return request<PatientDetail>({
    url: `/patients/${id}`,
    method: 'get'
  })
}

// 获取患者列表
export function getPatientList(keyword?: string) {
  return request<PatientDetail[]>({
    url: '/patients',
    method: 'get',
    params: { keyword }
  })
}

// 获取患者病历记录
export function getMedicalRecords(patientId: string) {
  return request<any[]>({
    url: `/patients/${patientId}/records`,
    method: 'get'
  })
}
