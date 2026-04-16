import { http } from '../request'

export interface StoreInfo {
  id: string
  name: string
  rating: number
  isOpen: boolean
  deliveryTime: number
  minDelivery: number
  distance: string
  address: string
  phone: string
  tags: string[]
  businessHours?: string
  licenseNo?: string
  description?: string
  images?: string[]
}

export interface StoreDrug {
  id: string
  name: string
  specification: string
  price: number
  originalPrice?: number
  stock: number
  sales: number
  isRx: boolean
  image?: string
  imageColor?: string
  imageText?: string
}

export interface StoreCoupon {
  id: string
  name: string
  value: number
  minSpend: number
}

export function getStoreList() {
  return http.get<StoreInfo[]>('/stores')
}

export function getStoreDetail(id: string) {
  return http.get<StoreInfo>(`/stores/${id}`)
}

export function getStoreDrugs(id: string) {
  return http.get<StoreDrug[]>(`/stores/${id}/drugs`)
}
