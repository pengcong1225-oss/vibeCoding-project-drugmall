import request, { http } from '../request'
import type { Order, OrderItem, CreateOrderParams, OrderQueryParams, OrderStats, PayParams, PayResult, RefundApplyParams, RefundInfo, LogisticsInfo } from '@/types'

// 创建订单
export function createOrder(data: CreateOrderParams) {
  return http.post<Order>('/orders', data)
}

// 获取订单列表
export function getOrders(params?: OrderQueryParams) {
  return http.get<{ list: Order[]; total: number; stats: OrderStats }>('/orders', params)
}

// 获取订单详情
export function getOrderDetail(id: string) {
  return http.get<Order>(`/orders/${id}`)
}

// 取消订单
export function cancelOrder(id: string, reason?: string) {
  return http.put(`/orders/${id}/cancel`, { reason })
}

// 删除订单
export function deleteOrder(id: string) {
  return http.delete(`/orders/${id}`)
}

// 确认收货
export function confirmReceipt(id: string) {
  return http.put(`/orders/${id}/confirm`)
}

// 再次购买
export function reorder(id: string) {
  return http.post<{ cartItemIds: string[] }>(`/orders/${id}/reorder`)
}

// ==================== 支付 ====================

// 支付订单
export function payOrder(data: PayParams) {
  return http.post<PayResult>('/orders/pay', data)
}

// 获取支付状态
export function getPayStatus(orderId: string) {
  return http.get<{ status: string; payTime?: string }>(`/orders/${orderId}/pay-status`)
}

// ==================== 物流 ====================

// 获取物流信息
export function getLogisticsInfo(orderId: string) {
  return http.get<{ company: string; no: string; list: LogisticsInfo[] }>(`/orders/${orderId}/logistics`)
}

// ==================== 退款/售后 ====================

// 申请退款
export function applyRefund(data: RefundApplyParams) {
  return http.post<RefundInfo>('/orders/refund', data)
}

// 获取退款信息
export function getRefundInfo(orderId: string) {
  return http.get<RefundInfo>(`/orders/${orderId}/refund`)
}

// 取消退款申请
export function cancelRefund(orderId: string) {
  return http.put(`/orders/${orderId}/refund/cancel`)
}

// ==================== 评价 ====================

// 获取待评价列表
export function getPendingReviews(params?: { page?: number; size?: number }) {
  return http.get<{ list: OrderItem[]; total: number }>('/orders/pending-reviews', params)
}

// 提交评价
export function submitReview(data: {
  orderItemId: string
  rating: number
  content: string
  images?: string[]
  isAnonymous?: boolean
  tags?: string[]
}) {
  return http.post('/orders/review', data)
}

// ==================== 统计 ====================

// 获取订单统计
export function getOrderStatistics() {
  return http.get<OrderStats>('/orders/statistics')
}

// 获取订单状态数量
export function getOrderStatusCounts() {
  return http.get<Record<string, number>>('/orders/status-counts')
}
