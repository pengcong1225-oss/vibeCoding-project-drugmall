import { request } from '@/utils/request'
import type { BasicConfig, PaymentConfig } from '@/types/settings'

export const getBasicSettings = (): Promise<BasicConfig> => {
  return request.get('/admin/settings/basic')
}

export const saveBasicSettings = (data: BasicConfig): Promise<void> => {
  return request.put('/admin/settings/basic', data)
}

export const getPaymentSettings = (): Promise<PaymentConfig> => {
  return request.get('/admin/settings/payment')
}

export const savePaymentSettings = (data: PaymentConfig): Promise<void> => {
  return request.put('/admin/settings/payment', data)
}
