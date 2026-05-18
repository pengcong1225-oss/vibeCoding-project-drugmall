import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getDoctorProfile, getTodayStats, getTodoCount, getPendingPrescriptionCount } from '@/api/doctor'
import { getIncomeOverview } from '@/api/income'

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
  
  // 待审核处方数量
  const pendingPrescriptions = ref(0)
  
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
    if (token.value && !doctorInfo.value) {
      try {
        // 调用真实API获取医生信息
        const profileRes = await getDoctorProfile()
        if (profileRes.data) {
          doctorInfo.value = {
            id: profileRes.data.id,
            name: profileRes.data.name,
            avatar: profileRes.data.avatar || '',
            title: profileRes.data.title,
            hospital: profileRes.data.hospital,
            department: profileRes.data.department,
            isCertified: profileRes.data.isCertified,
            rating: profileRes.data.rating,
            serviceCount: profileRes.data.serviceCount,
            responseTime: profileRes.data.responseTime
          }
        }
        
        // 调用真实API获取今日统计
        const statsRes = await getTodayStats()
        if (statsRes.data) {
          todayStats.value = {
            pending: statsRes.data.pending || 0,
            processing: statsRes.data.processing || 0,
            completed: statsRes.data.completed || 0,
            income: statsRes.data.income || 0
          }
        }
        
        // 获取待审核处方数量
        try {
          const prescriptionRes = await getPendingPrescriptionCount()
          if (prescriptionRes.data) {
            pendingPrescriptions.value = prescriptionRes.data.count || 0
          }
        } catch (error) {
          console.error('获取待审核处方数量失败:', error)
          // 暂时默认为0
          pendingPrescriptions.value = 0
        }
        
        // 调用真实API获取待办数量
        const todoRes = await getTodoCount()
        if (todoRes.data) {
          todoCount.value = todoRes.data.todoCount || 0
          unreadCount.value = todoRes.data.unreadCount || 0
        }
        
        console.log('医生信息初始化成功')
      } catch (error) {
        console.error('获取医生信息失败:', error)
        // 失败时使用fallback数据
        if (!doctorInfo.value) {
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
        }
      }
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
    pendingPrescriptions,
    todoCount,
    unreadCount,
    setToken,
    clearToken,
    setDoctorInfo,
    initDoctorInfo,
    updateTodayStats
  }
})
