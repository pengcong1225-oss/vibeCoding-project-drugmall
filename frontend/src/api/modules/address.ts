import request, { http } from '../request'
import type { UserAddress } from '@/types'

// 获取地址列表
export function getAddressList() {
  return http.get<UserAddress[]>('/addresses')
}

// 获取地址详情
export function getAddressDetail(id: string) {
  return http.get<UserAddress>(`/addresses/${id}`)
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

// 解析地址（智能识别）
export function parseAddress(text: string) {
  return http.post<{
    name: string
    phone: string
    province: string
    city: string
    district: string
    detail: string
  }>('/addresses/parse', { text })
}
