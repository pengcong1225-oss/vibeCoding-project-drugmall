export interface ConsultationStats {
  totalConsultations: number
  todayConsultations: number
  avgResponseTime: number
  completionRate: number
  consultationTypeStats: {
    type: string
    count: number
    percentage: number
  }[]
  trendData: {
    date: string
    count: number
  }[]
  doctorRanking: {
    doctorId: string
    doctorName: string
    count: number
    avgRating: number
  }[]
  departmentStats: {
    department: string
    count: number
    avgResponseTime: number
  }[]
}

export interface PrescriptionStats {
  totalPrescriptions: number
  todayPrescriptions: number
  approvalRate: number
  rejectionRate: number
  trendData: {
    date: string
    count: number
    approved: number
    rejected: number
  }[]
  drugRanking: {
    drugId: string
    drugName: string
    count: number
  }[]
  doctorRanking: {
    doctorId: string
    doctorName: string
    count: number
    approvalRate: number
  }[]
  statusDistribution: {
    status: string
    count: number
    percentage: number
  }[]
}
