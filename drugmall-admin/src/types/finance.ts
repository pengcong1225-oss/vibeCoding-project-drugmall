export interface FinanceStatCard {
  title: string
  value: number | string
  change: string
  trend: 'up' | 'down'
  compareText: string
}

export interface TrendData {
  dates: string[]
  income: number[]
  orders: number[]
}

export interface TopProduct {
  name: string
  amount: number
  percent: number
  change: string
}

export interface FinanceStatistics {
  cards: FinanceStatCard[]
  trendData: TrendData
  compositionData: { name: string; value: number }[]
  compareData: { categories: string[]; thisYear: number[]; lastYear: number[] }
  topProducts: TopProduct[]
}

export interface Transaction {
  id: string
  type: string
  amount: number
  status: string
  orderNo: string
  remark: string
  createTime: string
  userName: string
  userType: string
  payChannel: string
  completeTime: string | null
}

export interface TransactionQueryParams {
  pageNum: number
  pageSize: number
  type?: string
  status?: string
  keyword?: string
  userType?: string
  startTime?: string
  endTime?: string
  minAmount?: number
  maxAmount?: number
}

export interface TransactionListResult {
  list: Transaction[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}

export interface Withdrawal {
  id: string
  merchantName: string
  amount: number
  fee: number
  actualAmount: number
  status: string
  bankName: string
  bankCard: string
  bankBranch: string
  accountName: string
  applyTime: string
  transferTime: string | null
  completeTime: string | null
  rejectReason: string | null
  failReason?: string | null
}

export interface WithdrawalStats {
  todayAmount: number
  todayCount: number
  weekAmount: number
  weekCount: number
  monthAmount: number
  monthCount: number
  totalAmount: number
  totalCount: number
}
