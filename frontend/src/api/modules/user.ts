import request, { http } from '../request'
import type { UserInfo, LoginParams, LoginResult, Patient, AddPatientParams, UpdatePatientParams, UserAddress, UserCoupon, UserOrderStats } from '@/types'

// 用户登录
export function login(data: LoginParams) {
  return http.post<LoginResult>('/user/login', data)
}

// 发送验证码
export function sendVerifyCode(phone: string, type: 'login' | 'register' | 'reset' = 'login') {
  return http.post('/user/send-code', { phone, type })
}

// 用户登出
export function logout() {
  return http.post('/user/logout')
}

// 获取用户信息
export function getUserInfo() {
  return http.get<UserInfo>('/user/info')
}

// 更新用户信息
export function updateUserInfo(data: Partial<UserInfo>) {
  return http.put('/user/info', data)
}

// 上传头像
export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return http.post<{ url: string }>('/user/avatar', formData)
}

// 实名认证
export function realNameAuth(data: { name: string; idCard: string }) {
  return http.post('/user/real-name-auth', data)
}

// ==================== 就诊人管理 ====================

// 获取就诊人列表
export function getPatients() {
  return http.get<Patient[]>('/user/patients')
}

// 获取默认就诊人
export function getDefaultPatient() {
  return http.get<Patient | null>('/user/patients/default')
}

// 添加就诊人
export function addPatient(data: AddPatientParams) {
  return http.post<Patient>('/user/patients', data)
}

// 更新就诊人
export function updatePatient(id: string, data: Partial<Patient>) {
  return http.put<Patient>(`/user/patients/${id}`, data)
}

// 删除就诊人
export function deletePatient(id: string) {
  return http.delete(`/user/patients/${id}`)
}

// 设置默认就诊人
export function setDefaultPatient(id: string) {
  return http.put(`/user/patients/${id}/default`)
}

// ==================== 地址管理 ====================

// 获取地址列表
export function getAddresses() {
  return http.get<UserAddress[]>('/addresses')
}

// 获取默认地址
export function getDefaultAddress() {
  return http.get<UserAddress | null>('/addresses/default')
}

// 添加地址
export function addAddress(data: Omit<UserAddress, 'id'>) {
  return http.post<UserAddress>('/addresses', data)
}

// 更新地址
export function updateAddress(id: string, data: Partial<UserAddress>) {
  return http.put<UserAddress>(`/addresses/${id}`, data)
}

// 删除地址
export function deleteAddress(id: string) {
  return http.delete(`/addresses/${id}`)
}

// 设置默认地址
export function setDefaultAddress(id: string) {
  return http.put(`/addresses/${id}/default`)
}

// ==================== 优惠券 ====================

// 获取优惠券列表
export function getCoupons(params?: { status?: 'unused' | 'used' | 'expired' }) {
  return http.get<UserCoupon[]>('/user/coupons', params)
}

// 领取优惠券
export function receiveCoupon(couponId: string) {
  return http.post('/user/coupons/receive', { couponId })
}

// 获取可用优惠券列表（下单时）
export function getAvailableCoupons(orderAmount: number) {
  return http.get<UserCoupon[]>('/user/coupons/available', { orderAmount })
}

// ==================== 订单统计 ====================

// 获取订单统计
export function getOrderStats() {
  return http.get<UserOrderStats>('/user/order-stats')
}

// ==================== 其他 ====================

// 获取浏览历史
export function getBrowseHistory(params?: { page?: number; size?: number }) {
  return http.get<{ list: unknown[]; total: number }>('/user/browse-history', params)
}

// 清空浏览历史
export function clearBrowseHistory() {
  return http.delete('/user/browse-history')
}

// 检查手机号是否已注册
export function checkPhoneRegistered(phone: string) {
  return http.get<boolean>('/user/check-phone', { phone })
}
