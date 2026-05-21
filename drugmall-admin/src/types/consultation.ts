// 问诊管理模块类型定义

// 问诊信息
export interface ConsultationInfo {
  id: number
  consultationNo: string
  patientNickname: string
  patientPhone?: string
  doctorId: number
  doctorName: string
  departmentId: number
  departmentName: string
  type: 'text' | 'video' | 'phone' | 'ai'
  symptom: string
  fee: number
  status: number
  responseTime?: number
  createTime: string
  endTime?: string
}

// 问诊详情
export interface ConsultationDetail extends ConsultationInfo {
  patientGender?: number
  patientAge?: number
  patientId?: number
  allergyHistory?: string
  medicationHistory?: string
  images?: string[]
  prescriptionIds?: number[]
  payAmount?: number
  payType?: string
  payTime?: string
  payStatus?: number
  operationLogs?: OperationLog[]
}

// 问诊消息
export interface ConsultationMessage {
  id: number
  consultationId: number
  senderType: 'patient' | 'doctor' | 'system'
  senderId: number
  senderName: string
  content: string
  messageType: 'text' | 'image' | 'system' | 'prescription'
  images?: string[]
  prescriptionId?: number
  createTime: string
}

// 分配记录
export interface AssignRecord {
  id: number
  consultationId: number
  fromDoctorId?: number
  fromDoctorName?: string
  toDoctorId: number
  toDoctorName: string
  assignType: 'manual' | 'auto'
  assignerId?: number
  assignerName?: string
  reason?: string
  createTime: string
}

// 操作日志
export interface OperationLog {
  id: number
  operatorName: string
  action: string
  detail: string
  createTime: string
}

// 异常问诊
export interface ConsultationException {
  id: number
  consultationId: number
  consultationNo: string
  exceptionType: 'timeout_no_response' | 'timeout_no_reply' | 'complaint' | 'refund' | 'abnormal_cancel'
  patientNickname: string
  doctorName: string
  description: string
  status: 'pending' | 'processing' | 'handled'
  handlerName?: string
  handleMethod?: string
  handleNote?: string
  compensationAmount?: number
  handledAt?: string
  createTime: string
}

// 可分配医生
export interface AvailableDoctor {
  id: number
  name: string
  departmentName: string
  title: string
  rating: number
  currentConsultations: number
  maxConsultations: number
  onlineStatus: boolean
}

// 查询参数
export interface ConsultationQueryParams {
  pageNum?: number
  pageSize?: number
  consultationNo?: string
  patientPhone?: string
  doctorId?: number
  departmentId?: number
  type?: string
  status?: number
  startTime?: string
  endTime?: string
  feeMin?: number
  feeMax?: number
}

export interface ExceptionQueryParams {
  pageNum?: number
  pageSize?: number
  exceptionType?: string
  status?: string
  startTime?: string
  endTime?: string
}
