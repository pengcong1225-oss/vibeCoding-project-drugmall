import { request } from '@/utils/request'
import type { FinanceStatistics, TransactionQueryParams, TransactionListResult, Transaction, WithdrawalStats } from '@/types/finance'

export const getFinanceStatistics = (): Promise<FinanceStatistics> => {
  return request.get('/admin/finance/statistics')
}

export const getTransactionList = (params: TransactionQueryParams): Promise<TransactionListResult> => {
  return request.get('/admin/finance/transactions', params)
}

export const getTransactionDetail = (id: string): Promise<Transaction> => {
  return request.get(`/admin/finance/transactions/${id}`)
}

export const getWithdrawalList = (params: { pageNum: number; pageSize: number; status?: string; keyword?: string }): Promise<any> => {
  return request.get('/admin/finance/withdrawals', params)
}

export const getWithdrawalStats = (): Promise<WithdrawalStats> => {
  return request.get('/admin/finance/withdrawals/stats')
}

export const auditWithdrawal = (id: string, data: { action: string; remark?: string }): Promise<void> => {
  return request.post(`/admin/finance/withdrawals/${id}/audit`, data)
}
