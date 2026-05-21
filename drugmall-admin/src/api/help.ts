import { request } from '@/utils/request'
import type { HelpCenter, HelpCenterQuery } from '@/types/help'

export function getHelpCenterList(params: HelpCenterQuery) {
  return request.get<{ list: HelpCenter[], total: number }>('/admin/help-center', params)
}

export function createHelpCenter(data: Partial<HelpCenter>) {
  return request.post('/admin/help-center', data)
}

export function updateHelpCenter(id: string, data: Partial<HelpCenter>) {
  return request.put(`/admin/help-center/${id}`, data)
}

export function deleteHelpCenter(id: string) {
  return request.delete(`/admin/help-center/${id}`)
}
