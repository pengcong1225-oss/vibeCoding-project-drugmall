import { request } from '@/utils/request'
import type { 
  Order, 
  OrderQueryParams, 
  OrderListResult, 
  ShipFormData,
  RefundFormData 
} from '@/types/order'

// 订单管理
export const getOrderList = (params: OrderQueryParams): Promise<OrderListResult> => {
  return request.get('/admin/orders', params)
}

export const getOrderDetail = (id: string): Promise<Order> => {
  return request.get(`/admin/orders/${id}`)
}

export const confirmOrder = (id: string): Promise<void> => {
  return request.post(`/admin/orders/${id}/confirm`)
}

export const shipOrder = (data: ShipFormData): Promise<void> => {
  return request.post('/admin/orders/ship', data)
}

export const cancelOrder = (id: string, reason?: string): Promise<void> => {
  return request.post(`/admin/orders/${id}/cancel`, { reason })
}

export const handleRefund = (data: RefundFormData): Promise<void> => {
  return request.post('/admin/orders/refund', data)
}

// 获取物流轨迹
export const getLogisticsTraces = (orderId: string): Promise<any[]> => {
  return request.get(`/admin/orders/${orderId}/traces`)
}
