// 医生管理模块类型定义

// 医生信息
export interface DoctorInfo {
  id: number
  name: string
  phone: string
  avatar: string
  gender: number
  age: number
  departmentId: number
  departmentName: string
  title: string
  hospital: string
  rating: number
  serviceCount: number
  responseTime: number
  status: number
  joinTime: string
  createTime: string
}

// 医生统计数据
export interface DoctorStats {
  totalConsultations: number
  avgRating: number
  avgResponseTime: number
  monthIncome: number
  positiveRate: number
}

// 医生审核信息
export interface DoctorAudit {
  id: number
  doctorId: number
  name: string
  phone: string
  departmentId: number
  departmentName: string
  title: string
  hospital: string
  status: number
  submitTime: string
  auditOpinion?: string
  auditTime?: string
  auditorName?: string
}

// 审核材料
export interface AuditMaterials {
  idCardFront: string
  idCardBack: string
  certificate: string
  license: string
  titleCertificate?: string
  workProof?: string
  avatar: string
}

// 医生详情
export interface DoctorDetail extends DoctorInfo {
  certificateNo: string
  licenseNo: string
  practiceScope: string
  workYears: number
  introduction: string
  auditMaterials: AuditMaterials
  stats: DoctorStats
  recentConsultations?: Array<Record<string, unknown>>
  recentPrescriptions?: Array<Record<string, unknown>>
  recentReviews?: DoctorReview[]
  recentSchedules?: ScheduleInfo[]
}

// 排班信息
export interface ScheduleInfo {
  id: number
  doctorId: number
  date: string
  morningEnabled: boolean
  morningMax: number
  afternoonEnabled: boolean
  afternoonMax: number
  eveningEnabled: boolean
  eveningMax: number
  consultationTypes: string[]
  status: number
}

// 科室信息
export interface DepartmentInfo {
  id: number
  name: string
  parentId: number
  parentName?: string
  icon?: string
  doctorCount: number
  sortOrder: number
  status: number
  description?: string
  children?: DepartmentInfo[]
}

// 医生评价
export interface DoctorReview {
  id: number
  doctorId: number
  doctorName: string
  userId: number
  patientNickname: string
  consultationId: number
  consultationType: string
  rating: number
  content: string
  reply?: string
  replyTime?: string
  status: number
  createTime: string
}

// 查询参数
export interface DoctorQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  phone?: string
  departmentId?: number
  title?: string
  hospital?: string
  status?: number
  ratingMin?: number
  ratingMax?: number
  startTime?: string
  endTime?: string
}

export interface AuditQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  status?: number
  departmentId?: number
  startTime?: string
  endTime?: string
}

export interface ScheduleQueryParams {
  doctorId: number
  startDate?: string
  endDate?: string
}

export interface DepartmentQueryParams {
  keyword?: string
  status?: number
}

export interface ReviewQueryParams {
  pageNum?: number
  pageSize?: number
  doctorId?: number
  ratingMin?: number
  ratingMax?: number
  startTime?: string
  endTime?: string
  status?: number
}
