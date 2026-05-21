import request from '@/utils/request'

// 收入概览
export interface IncomeOverview {
  totalIncome: number
  availableBalance: number
  pendingSettlement: number
  totalWithdraw: number
}

// 收入记录
export interface IncomeRecord {
  id: string
  type: string
  patientName: string
  amount: number
  time: string
  status?: string
}

// 收入趋势
export interface IncomeTrend {
  date: string
  amount: number
  count: number
}

// 收入构成
export interface IncomeComposition {
  type: string
  amount: number
  percentage: number
}

// 提现记录
export interface WithdrawRecord {
  withdrawId: string
  amount: number
  method: string
  methodName: string
  status: string
  statusText: string
  applyTime: string
  arriveTime?: string
}

// 提现申请
export interface WithdrawApply {
  amount: number
  method: string
  accountName: string
  accountNo: string
  bankName?: string
}

// 获取收入概览
export function getIncomeOverview() {
  return request<IncomeOverview>({
    url: '/income/overview',
    method: 'get'
  })
}

// 获取收入明细列表
export function getIncomeList(params?: { page?: number; size?: number; type?: string }) {
  return request<IncomeRecord[]>({
    url: '/income/list',
    method: 'get',
    params
  })
}

// 获取收入趋势
export function getIncomeTrend(dimension: string = 'week') {
  return request<IncomeTrend[]>({
    url: '/income/trend',
    method: 'get',
    params: { dimension }
  })
}

// 获取收入构成
export function getIncomeComposition() {
  return request<IncomeComposition[]>({
    url: '/income/composition',
    method: 'get'
  })
}

// 获取提现记录
export function getWithdrawList(params?: { page?: number; size?: number }) {
  return request<WithdrawRecord[]>({
    url: '/income/withdraw/list',
    method: 'get',
    params
  })
}

// 申请提现
export function applyWithdraw(data: WithdrawApply) {
  return request<boolean>({
    url: '/income/withdraw',
    method: 'post',
    data
  })
}
