import { request } from '@/utils/request'
import type { DashboardOverview, GmvTrendData, OrderSourceItem } from '@/types/dashboard'

export const getDashboardOverview = (): Promise<DashboardOverview> => {
  return request.get('/admin/dashboard/overview')
}

export const getGmvTrend = (timeRange: string = 'month'): Promise<GmvTrendData> => {
  return request.get('/admin/dashboard/gmv-trend', { timeRange })
}

export const getOrderSource = (): Promise<OrderSourceItem[]> => {
  return request.get('/admin/dashboard/order-source')
}
