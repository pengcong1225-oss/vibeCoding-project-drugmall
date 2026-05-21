export interface DoctorSettlement {
  id: string
  doctorId: string
  doctorName: string
  period: string
  consultationIncome: number
  prescriptionIncome: number
  totalIncome: number
  platformFee: number
  actualIncome: number
  status: 'pending' | 'processing' | 'completed' | 'rejected'
  settlementCycle: 'T+7' | 'T+15' | 'monthly'
  applyTime: string
  processTime?: string
  remark?: string
}

export interface SettlementQuery {
  pageNum: number
  pageSize: number
  doctorId?: string
  doctorName?: string
  status?: string
  period?: string
}
