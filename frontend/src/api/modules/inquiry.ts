import { http } from '../request'

export interface Consultation {
  id: string
  patientId: string
  patientName: string
  patientAge: number
  patientGender: string
  patientAvatar: string
  doctorId?: string
  doctorName?: string
  doctorTitle?: string
  doctorAvatar?: string
  hospital?: string
  department?: string
  price?: number
  type: string
  status: string
  symptom: string
  waitTime?: string
  remainingTime?: string
  isUrgent: boolean
  isRx: boolean
  isReviewed?: boolean
  createTime: string
}

export interface ConsultationDetail extends Consultation {
  messages: ConsultationMessage[]
  doctor?: DoctorInfo
  orderInfo?: OrderInfo
}

export interface OrderInfo {
  orderNo: string
  type: string
  serviceMode: string
  createTime: string
  amount: number
  paidAmount: number
}

export interface ConsultationMessage {
  id: string
  consultationId: string
  sender: 'patient' | 'doctor' | 'system' | 'assistant'
  type: string
  content: string
  time: string
  status?: string
}

export interface DoctorInfo {
  id: string
  name: string
  title: string
  hospital: string
  department: string
  avatar: string
  isOnline: boolean
  tags: string[]
  inquiryCount: number
  rating: number
  specialty?: string
  consultationCount?: number
  waitTime?: number
  price?: number
  introduction?: string
  workYears?: number
  serviceMode?: string
}

export interface DoctorReview {
  id: string
  userName: string
  type: string
  date: string
  satisfaction: string
  satisfactionText: string
  content: string
  tags: string[]
}

export interface DoctorReviewTag {
  name: string
  count: number
}

export interface PatientProfile {
  id: string
  name: string
  gender: 'male' | 'female'
  age: number
  relation?: string
  isDefault?: boolean
}

export interface CreateConsultationParams {
  doctorId: string
  symptom: string
  patientId: string
  type: string
}

export interface CreateConsultationResult {
  id: string
  orderNo: string
  status: string
  expireTime: string
}

export interface PayConsultationParams {
  paymentMethod: string
}

export interface PayConsultationResult {
  success: boolean
  consultationId: string
  status: string
}

export function getConsultationList(status?: string) {
  return http.get<Consultation[]>('/patient/consultations', { status })
}

export function getConsultationDetail(id: string) {
  return http.get<ConsultationDetail>(`/patient/consultations/${id}`)
}

export function startConsultation(id: string) {
  return http.post<boolean>(`/patient/consultations/${id}/start`)
}

export function endConsultation(id: string) {
  return http.post<boolean>(`/patient/consultations/${id}/end`)
}

export function getConsultationMessages(id: string) {
  return http.get<ConsultationMessage[]>(`/patient/consultations/${id}/messages`)
}

export function sendConsultationMessage(id: string, data: { type: string; content: string }) {
  return http.post<ConsultationMessage>(`/patient/consultations/${id}/messages`, data)
}

export function getDoctorList(params?: { department?: string; keyword?: string }) {
  return http.get<DoctorInfo[]>('/patient/consultations/doctors', params)
}

export function getDoctorDetail(id: string) {
  return http.get<DoctorInfo>(`/patient/consultations/doctors/${id}`)
}

export function getDoctorReviews(id: string, params?: { tag?: string; page?: number; size?: number }) {
  return http.get<DoctorReview[]>(`/patient/consultations/doctors/${id}/reviews`, params)
}

export function getDoctorReviewTags(id: string) {
  return http.get<DoctorReviewTag[]>(`/patient/consultations/doctors/${id}/review-tags`)
}

// 创建问诊（预问诊）
export function createConsultation(data: CreateConsultationParams) {
  return http.post<CreateConsultationResult>('/patient/consultations', data)
}

// 获取患者档案列表
export function getPatientProfiles() {
  return http.get<PatientProfile[]>('/patient/profiles')
}

// 支付问诊
export function payConsultation(id: string, data: PayConsultationParams) {
  return http.post<PayConsultationResult>(`/patient/consultations/${id}/pay`, data)
}

// 检查医生接诊状态
export function checkDoctorAcceptance(id: string) {
  return http.get<{ accepted: boolean; doctorId?: string }>(`/patient/consultations/${id}/acceptance`)
}
