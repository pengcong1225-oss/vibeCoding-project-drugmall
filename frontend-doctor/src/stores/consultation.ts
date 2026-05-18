import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { 
  getDoctorConsultations, 
  getDoctorConsultationDetail,
  acceptConsultation as apiAcceptConsultation,
  completeConsultation as apiCompleteConsultation,
  getConsultationMessages,
  sendConsultationMessage as apiSendMessage,
  type DoctorConsultation,
  type ConsultationMessage
} from '@/api/consultation'

export interface Consultation extends DoctorConsultation {
  messages?: ConsultationMessage[]
}

export const useConsultationStore = defineStore('consultation', () => {
  // State
  const consultations = ref<Consultation[]>([])
  const currentConsultation = ref<Consultation | null>(null)
  const loading = ref(false)

  // Getters
  const pendingConsultations = computed(() => 
    consultations.value.filter(c => c.status === 'pending')
  )
  
  const processingConsultations = computed(() => 
    consultations.value.filter(c => c.status === 'processing')
  )
  
  const completedConsultations = computed(() => 
    consultations.value.filter(c => c.status === 'completed')
  )

  // Actions
  const fetchConsultations = async (status?: string) => {
    loading.value = true
    try {
      // 调用真实API获取问诊列表
      const response = await getDoctorConsultations(status || 'all')
      
      // 响应拦截器已解包data字段，response直接是数组
      if (response && Array.isArray(response)) {
        consultations.value = response.map(item => ({
          ...item,
          messages: []
        }))
      }
    } catch (error) {
      console.error('获取问诊列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchConsultationDetail = async (id: string) => {
    loading.value = true
    try {
      // 调用真实API获取问诊详情
      const [detailResponse, messagesResponse] = await Promise.all([
        getDoctorConsultationDetail(id),
        getConsultationMessages(id)
      ])
      
      if (detailResponse.data) {
        currentConsultation.value = {
          ...detailResponse.data,
          messages: messagesResponse.data || []
        }
        
        // 更新列表中的对应项
        const index = consultations.value.findIndex(c => c.id === id)
        if (index !== -1) {
          consultations.value[index] = {
            ...consultations.value[index],
            ...detailResponse.data
          }
        }
      }
    } catch (error) {
      console.error('获取问诊详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const startConsultation = async (id: string) => {
    try {
      // 调用真实API接诊
      await apiAcceptConsultation(id)
      
      // 更新本地状态
      const index = consultations.value.findIndex(c => c.id === id)
      if (index !== -1) {
        consultations.value[index].status = 'processing'
      }
      
      // 如果当前正在查看该问诊，也更新
      if (currentConsultation.value?.id === id) {
        currentConsultation.value.status = 'processing'
      }
    } catch (error) {
      console.error('接诊失败:', error)
      throw error
    }
  }

  const endConsultation = async (id: string) => {
    try {
      // 调用真实API完成问诊
      await apiCompleteConsultation(id)
      
      // 更新本地状态
      const index = consultations.value.findIndex(c => c.id === id)
      if (index !== -1) {
        consultations.value[index].status = 'completed'
      }
      
      if (currentConsultation.value?.id === id) {
        currentConsultation.value.status = 'completed'
      }
    } catch (error) {
      console.error('结束问诊失败:', error)
      throw error
    }
  }

  const sendMessage = async (consultationId: string, message: Partial<ConsultationMessage>) => {
    try {
      // 调用真实API发送消息
      const response = await apiSendMessage(consultationId, {
        type: message.type || 'text',
        content: message.content || ''
      })
      
      // 响应拦截器已解包data字段，response直接是消息对象
      if (response && currentConsultation.value?.id === consultationId) {
        currentConsultation.value.messages = [
          ...(currentConsultation.value.messages || []),
          response
        ]
      }
      
      return response
    } catch (error) {
      console.error('发送消息失败:', error)
      throw error
    }
  }

  return {
    consultations,
    currentConsultation,
    loading,
    pendingConsultations,
    processingConsultations,
    completedConsultations,
    fetchConsultations,
    fetchConsultationDetail,
    startConsultation,
    endConsultation,
    sendMessage
  }
})
