import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface DoctorInfo {
  id: string
  name: string
  avatar: string
  title: string
  hospital: string
  department: string
  isCertified: boolean
  rating: number
  serviceCount: number
  responseTime: number
}

export interface TodayStats {
  pending: number
  processing: number
  completed: number
  income: number
}

export const useDoctorStore = defineStore('doctor', () => {
  // State
  const doctorInfo = ref<DoctorInfo | null>(null)
  const token = ref<string>(localStorage.getItem('token') || '')
  const isLoggedIn = computed(() => !!token.value)
  
  // 今日统计数据
  const todayStats = ref<TodayStats>({
    pending: 0,
    processing: 0,
    completed: 0,
    income: 0
  })
  
  // 待办事项数量
  const todoCount = ref(0)
  
  // 未读消息数量
  const unreadCount = ref(0)

  // Actions
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const clearToken = () => {
    token.value = ''
    localStorage.removeItem('token')
    doctorInfo.value = null
  }

  const setDoctorInfo = (info: DoctorInfo) => {
    doctorInfo.value = info
  }

  const initDoctorInfo = async () => {
    // 模拟获取医生信息
    // 实际项目中这里会调用API
    if (token.value && !doctorInfo.value) {
      // 模拟数据
      doctorInfo.value = {
        id: 'DOC001',
        name: '张医生',
        avatar: '',
        title: '主任医师',
        hospital: '北京协和医院',
        department: '心内科',
        isCertified: true,
        rating: 4.9,
        serviceCount: 1280,
        responseTime: 2
      }
      
      // 模拟今日统计
      todayStats.value = {
        pending: 5,
        processing: 2,
        completed: 28,
        income: 2580
      }
      
      todoCount.value = 8
      unreadCount.value = 3
    }
  }

  const updateTodayStats = (stats: Partial<TodayStats>) => {
    todayStats.value = { ...todayStats.value, ...stats }
  }

  return {
    doctorInfo,
    token,
    isLoggedIn,
    todayStats,
    todoCount,
    unreadCount,
    setToken,
    clearToken,
    setDoctorInfo,
    initDoctorInfo,
    updateTodayStats
  }
})
