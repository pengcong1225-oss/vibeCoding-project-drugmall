// 处方管理模块类型定义

// 处方信息
export interface PrescriptionInfo {
  id: number
  prescriptionNo: string
  patientName: string
  doctorId: number
  doctorName: string
  diagnosis: string
  drugCount: number
  totalAmount: number
  type: 'normal' | 'emergency' | 'chronic'
  status: number
  createTime: string
  auditTime?: string
}

// 处方明细
export interface PrescriptionItem {
  id: number
  prescriptionId: number
  drugId: number
  drugName: string
  specification: string
  manufacturer: string
  usage: string
  dosage: string
  frequency: string
  duration: number
  quantity: number
  unitPrice: number
  amount: number
  notes?: string
}

// 处方详情
export interface PrescriptionDetail extends PrescriptionInfo {
  patientGender?: number
  patientAge?: number
  allergyHistory?: string
  medicationHistory?: string
  doctorDepartment?: string
  doctorTitle?: string
  doctorHospital?: string
  consultationId?: number
  items: PrescriptionItem[]
  auditOpinion?: string
  auditorName?: string
  orderId?: number
  orderNo?: string
}

// 系统预审结果
export interface PreCheckResult {
  status: 'pass' | 'warn' | 'reject'
  items: PreCheckItem[]
}

export interface PreCheckItem {
  checkType: string
  severity: 'high' | 'medium' | 'low'
  description: string
  drugNames?: string[]
}

// 审核记录
export interface AuditRecord {
  id: number
  prescriptionId: number
  auditorId: number
  auditorName: string
  result: 'pass' | 'reject'
  opinion: string
  action: string
  preCheckResult?: PreCheckResult
  auditTime: string
}

// 处方模板
export interface PrescriptionTemplate {
  id: number
  name: string
  doctorId: number
  doctorName: string
  departmentId: number
  departmentName: string
  diagnosis: string
  drugCount: number
  useCount: number
  usageNotes?: string
  precautions?: string
  status: number
  createTime: string
}

// 模板药品明细
export interface TemplateDrugItem {
  id: number
  drugId: number
  drugName: string
  specification: string
  usage: string
  dosage: string
  frequency: string
  duration: number
  quantity: number
  notes?: string
}

// 查询参数
export interface PrescriptionQueryParams {
  pageNum?: number
  pageSize?: number
  prescriptionNo?: string
  patientName?: string
  doctorId?: number
  type?: string
  status?: number
  startTime?: string
  endTime?: string
  amountMin?: number
  amountMax?: number
}

export interface TemplateQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  departmentId?: number
  status?: number
}
