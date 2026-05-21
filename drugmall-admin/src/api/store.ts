import { request } from '@/utils/request'
import type {
  StoreInfo,
  StoreDetail,
  StoreAudit,
  StoreDrugItem,
  StoreStats,
  StoreQueryParams,
  AuditQueryParams,
  StoreDrugQueryParams
} from '@/types/store'

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

// 门店列表
export function getStoreList(params: StoreQueryParams): Promise<PageResult<StoreInfo>> {
  return request.get('/admin/stores', params)
}

export function getStoreDetail(id: number): Promise<StoreDetail> {
  return request.get(`/admin/stores/${id}`)
}

export function updateStore(id: number, data: Partial<StoreInfo>): Promise<void> {
  return request.put(`/admin/stores/${id}`, data)
}

export function updateStoreStatus(id: number, status: number): Promise<void> {
  return request.patch(`/admin/stores/${id}/status`, { status })
}

export function getStoreStats(id: number): Promise<StoreStats> {
  return request.get(`/admin/stores/${id}/stats`)
}

// 门店审核
export function getAuditList(params: AuditQueryParams): Promise<PageResult<StoreAudit>> {
  return request.get('/admin/stores/audit', params)
}

export function getAuditDetail(id: number): Promise<any> {
  return request.get(`/admin/stores/audit/${id}`)
}

export function auditStore(id: number, data: { result: string; opinion?: string }): Promise<void> {
  return request.put(`/admin/stores/${id}/audit`, data)
}

// 门店药品
export function getStoreDrugs(storeId: number, params: StoreDrugQueryParams): Promise<PageResult<StoreDrugItem>> {
  return request.get(`/admin/stores/${storeId}/drugs`, params)
}

export function addStoreDrug(storeId: number, data: { drugId: number; price: number; stock: number }): Promise<void> {
  return request.post(`/admin/stores/${storeId}/drugs`, data)
}

export function updateStoreDrug(storeId: number, drugId: number, data: { price?: number; stock?: number; status?: number }): Promise<void> {
  return request.put(`/admin/stores/${storeId}/drugs/${drugId}`, data)
}

export function removeStoreDrug(storeId: number, drugId: number): Promise<void> {
  return request.delete(`/admin/stores/${storeId}/drugs/${drugId}`)
}
