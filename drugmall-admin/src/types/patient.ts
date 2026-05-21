export interface Patient {
  id: string
  userId: string
  userName: string
  name: string
  gender: 'male' | 'female'
  age: number
  idCard: string
  phone: string
  relationship: 'self' | 'parent' | 'child' | 'spouse' | 'other'
  healthRecord: {
    allergies: string[]
    chronicDiseases: string[]
    bloodType: string
    lastCheckup?: string
  }
  consultationCount: number
  prescriptionCount: number
  orderCount: number
  createTime: string
  updateTime: string
}

export interface PatientQuery {
  pageNum: number
  pageSize: number
  userId?: string
  name?: string
  relationship?: string
}
