import request from '@/utils/request'

// 医生信息
export interface DoctorProfile {
  id: string
  name: string
  avatar: string
  title: string
  hospital: string
  department: string
  isCertified: boolean
  rating: number
  serviceCount: number
  responseTime: number
  specialties?: string[]
  introduction?: string
}

// 今日统计
export interface TodayStats {
  pending: number
  processing: number
  completed: number
  income: number
}

// 待办数量
export interface TodoCount {
  todoCount: number
  unreadCount: number
}

// 待审核处方数量
export interface PendingPrescriptionCount {
  count: number
}

// 获取医生信息
export function getDoctorProfile() {
  return request<DoctorProfile>({
    url: '/doctor/profile',
    method: 'get'
  })
}

// 获取今日统计
export function getTodayStats() {
  return request<TodayStats>({
    url: '/doctor/stats',
    method: 'get'
  })
}

// 获取待办数量
export function getTodoCount() {
  return request<TodoCount>({
    url: '/doctor/todo-count',
    method: 'get'
  })
}

// 获取待审核处方数量
export function getPendingPrescriptionCount() {
  return request<PendingPrescriptionCount>({
    url: '/doctor/prescription-count',
    method: 'get'
  })
}
