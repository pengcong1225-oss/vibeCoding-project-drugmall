import { request } from '@/utils/request'
import type { OperationLog, LogQuery } from '@/types/log'

export function getOperationLogs(params: LogQuery) {
  return request.get<{ list: OperationLog[], total: number }>('/admin/logs/operations', params)
}

export function exportOperationLogs(params: LogQuery) {
  return request.get('/admin/logs/operations/export', params)
}
