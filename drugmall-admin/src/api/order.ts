import { request } from '@/utils/request'
import type { Order, OrderQueryParams, Refund, AbnormalOrder, RefundQuery, AbnormalOrderQuery } from '@/types/order'

export function getOrderList(params: OrderQueryParams) {
  return request.get<{ list: Order[], total: number }>('/admin/orders', params)
}

export function cancelOrder(id: string) {
  return request.put(`/admin/orders/${id}/cancel`)
}

export function shipOrder(id: string, data: { trackingNo: string, company: string }) {
  return request.put(`/admin/orders/${id}/ship`, data)
}

export function getRefundList(params: RefundQuery) {
  return request.get<{ list: Refund[], total: number }>('/admin/refunds', params)
}

export function getRefundDetail(id: string) {
  return request.get<Refund>(`/admin/refunds/${id}`)
}

export function auditRefund(id: string, data: { status: 'approved' | 'rejected', reason?: string }) {
  return request.put(`/admin/refunds/${id}/audit`, data)
}

export function getAbnormalOrderList(params: AbnormalOrderQuery) {
  return request.get<{ list: AbnormalOrder[], total: number }>('/admin/orders/abnormal', params)
}

export function handleAbnormalOrder(id: string, data: { result: string, remark?: string }) {
  return request.put(`/admin/orders/abnormal/${id}/handle`, data)
}
