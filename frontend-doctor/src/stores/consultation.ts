import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface Consultation {
  id: string
  patientId: string
  patientName: string
  patientAge: number
  patientGender: '男' | '女'
  patientAvatar: string
  type: '图文问诊' | '视频问诊' | '复诊'
  status: 'pending' | 'processing' | 'completed' | 'closed'
  symptom: string
  waitTime: string
  remainingTime: string
  isUrgent: boolean
  isRx: boolean
  createTime: string
  messages?: Message[]
}

export interface Message {
  id: string
  sender: 'doctor' | 'patient' | 'system'
  type: 'text' | 'image' | 'voice' | 'prescription'
  content: string
  time: string
  status?: 'sending' | 'sent' | 'read'
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
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 500))
      
      // 模拟数据
      consultations.value = [
        {
          id: 'C001',
          patientId: 'P001',
          patientName: '李*',
          patientAge: 35,
          patientGender: '女',
          patientAvatar: '',
          type: '图文问诊',
          status: 'pending',
          symptom: '头疼、发烧三天，伴有咳嗽症状...',
          waitTime: '15分钟',
          remainingTime: '8分钟',
          isUrgent: true,
          isRx: true,
          createTime: '2024-12-07 10:30:00'
        },
        {
          id: 'C002',
          patientId: 'P002',
          patientName: '王*',
          patientAge: 28,
          patientGender: '男',
          patientAvatar: '',
          type: '复诊',
          status: 'processing',
          symptom: '慢性胃炎复诊，咨询用药...',
          waitTime: '8分钟',
          remainingTime: '22分钟',
          isUrgent: false,
          isRx: false,
          createTime: '2024-12-07 09:45:00'
        },
        {
          id: 'C003',
          patientId: 'P003',
          patientName: '张*',
          patientAge: 42,
          patientGender: '女',
          patientAvatar: '',
          type: '图文问诊',
          status: 'completed',
          symptom: '皮肤过敏，瘙痒...',
          waitTime: '-',
          remainingTime: '-',
          isUrgent: false,
          isRx: true,
          createTime: '2024-12-06 14:20:00'
        }
      ]
    } finally {
      loading.value = false
    }
  }

  const fetchConsultationDetail = async (id: string) => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 300))
      
      const consultation = consultations.value.find(c => c.id === id)
      if (consultation) {
        currentConsultation.value = {
          ...consultation,
          messages: [
            {
              id: 'M001',
              sender: 'system',
              type: 'text',
              content: '问诊已开始，请医生尽快接诊',
              time: '10:30'
            },
            {
              id: 'M002',
              sender: 'patient',
              type: 'text',
              content: '医生您好，我头疼发烧三天了，请问应该吃什么药？',
              time: '10:31'
            },
            {
              id: 'M003',
              sender: 'doctor',
              type: 'text',
              content: '您好，请问您除了头疼发烧，还有其他症状吗？比如咳嗽、咽痛？',
              time: '10:32',
              status: 'read'
            }
          ]
        }
      }
    } finally {
      loading.value = false
    }
  }

  const startConsultation = async (id: string) => {
    const index = consultations.value.findIndex(c => c.id === id)
    if (index !== -1) {
      consultations.value[index].status = 'processing'
    }
  }

  const endConsultation = async (id: string) => {
    const index = consultations.value.findIndex(c => c.id === id)
    if (index !== -1) {
      consultations.value[index].status = 'completed'
    }
  }

  const sendMessage = async (consultationId: string, message: Partial<Message>) => {
    const consultation = currentConsultation.value
    if (consultation && consultation.id === consultationId) {
      const newMessage: Message = {
        id: `M${Date.now()}`,
        sender: 'doctor',
        type: message.type || 'text',
        content: message.content || '',
        time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
        status: 'sent'
      }
      consultation.messages = [...(consultation.messages || []), newMessage]
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
