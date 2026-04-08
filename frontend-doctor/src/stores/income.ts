import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface IncomeOverview {
  balance: number
  monthIncome: number
  monthIncomeRatio: number
  totalIncome: number
  todayIncome: number
  weekIncome: number
  pendingSettlement: number
  totalWithdraw: number
}

export interface IncomeRecord {
  id: string
  type: string
  typeIcon: string
  amount: number
  patientName: string
  source: string
  time: string
  status: '待结算' | '已结算' | '已提现'
  inquiryId?: string
}

export interface TrendData {
  date: string
  income: number
  quantity: number
}

export interface CompositionData {
  type: string
  amount: number
  percentage: number
}

export interface WithdrawRecord {
  withdrawId: string
  amount: number
  method: 'bank' | 'alipay'
  methodIcon: string
  methodName: string
  status: 'processing' | 'success' | 'rejected'
  statusText: string
  applyTime: string
  arrivalTime?: string
  rejectReason?: string
}

export const useIncomeStore = defineStore('income', () => {
  // State
  const overview = ref<IncomeOverview>({
    balance: 0,
    monthIncome: 0,
    monthIncomeRatio: 0,
    totalIncome: 0,
    todayIncome: 0,
    weekIncome: 0,
    pendingSettlement: 0,
    totalWithdraw: 0
  })
  
  const incomeList = ref<IncomeRecord[]>([])
  const trendData = ref<TrendData[]>([])
  const compositionData = ref<CompositionData[]>([])
  const withdrawList = ref<WithdrawRecord[]>([])
  const loading = ref(false)

  // Actions
  const fetchOverview = async () => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 400))
      
      overview.value = {
        balance: 5000.00,
        monthIncome: 8500.00,
        monthIncomeRatio: 15.2,
        totalIncome: 128000.00,
        todayIncome: 1200.00,
        weekIncome: 5600.00,
        pendingSettlement: 3000.00,
        totalWithdraw: 85000.00
      }
    } finally {
      loading.value = false
    }
  }

  const fetchIncomeList = async (params?: any) => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 500))
      
      incomeList.value = [
        {
          id: 'INC202412070001',
          type: '图文问诊',
          typeIcon: 'icon-chat',
          amount: 80.00,
          patientName: '李*',
          source: '图文问诊',
          time: '2024-12-07 14:30:00',
          status: '已结算',
          inquiryId: 'INQ202412070001'
        },
        {
          id: 'INC202412070002',
          type: '复诊开方',
          typeIcon: 'icon-prescription',
          amount: 60.00,
          patientName: '王*',
          source: '复诊开方',
          time: '2024-12-07 10:15:00',
          status: '待结算',
          inquiryId: 'INQ202412070002'
        },
        {
          id: 'INC202412060003',
          type: '图文问诊',
          typeIcon: 'icon-chat',
          amount: 80.00,
          patientName: '张*',
          source: '图文问诊',
          time: '2024-12-06 16:45:00',
          status: '已提现',
          inquiryId: 'INQ202412060003'
        }
      ]
    } finally {
      loading.value = false
    }
  }

  const fetchTrendData = async (dimension: string = 'week') => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 300))
      
      // 模拟趋势数据
      trendData.value = Array.from({ length: 7 }, (_, i) => ({
        date: `12-0${i + 1}`,
        income: Math.floor(Math.random() * 500) + 200,
        quantity: Math.floor(Math.random() * 10) + 1
      }))
      
      compositionData.value = [
        { type: '图文问诊', amount: 6000, percentage: 70.6 },
        { type: '复诊开方', amount: 2000, percentage: 23.5 },
        { type: '其他', amount: 500, percentage: 5.9 }
      ]
    } finally {
      loading.value = false
    }
  }

  const fetchWithdrawList = async () => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 400))
      
      withdrawList.value = [
        {
          withdrawId: 'WIT202404070001',
          amount: 5000.00,
          method: 'bank',
          methodIcon: 'icon-bank',
          methodName: '工商银行(尾号8888)',
          status: 'processing',
          statusText: '处理中',
          applyTime: '2024-04-07 15:30:00',
          arrivalTime: '',
          rejectReason: ''
        },
        {
          withdrawId: 'WIT202403150002',
          amount: 3000.00,
          method: 'alipay',
          methodIcon: 'icon-alipay',
          methodName: '支付宝',
          status: 'success',
          statusText: '已到账',
          applyTime: '2024-03-15 10:00:00',
          arrivalTime: '2024-03-15 14:30:00',
          rejectReason: ''
        }
      ]
    } finally {
      loading.value = false
    }
  }

  const applyWithdraw = async (data: any) => {
    // 模拟提现申请
    await new Promise(resolve => setTimeout(resolve, 500))
    return { success: true }
  }

  return {
    overview,
    incomeList,
    trendData,
    compositionData,
    withdrawList,
    loading,
    fetchOverview,
    fetchIncomeList,
    fetchTrendData,
    fetchWithdrawList,
    applyWithdraw
  }
})
