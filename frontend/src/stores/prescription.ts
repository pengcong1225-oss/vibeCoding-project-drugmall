import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Drug } from '@/types/drug'
import type { Patient } from '@/types/user'
import { applyPrescription as apiApplyPrescription } from '@/api/consultation'

// 处方流程步骤
export type PrescriptionStep = 'apply' | 'chat' | 'success' | 'pay'

// 疾病标签
export interface DiseaseTag {
  id: string
  name: string
  selected?: boolean
}

// 聊天消息
export interface ChatMessage {
  id: string
  type: 'user' | 'doctor'
  content: string
  time: string
  avatar?: string
}

// 医生信息
export interface DoctorInfo {
  id: string
  name: string
  title: string
  hospital: string
  department: string
  avatar: string
  isCertified: boolean
}

// 处方药品
export interface PrescriptionDrug {
  id: string
  name: string
  spec: string
  price: number
  quantity: number
  usage: string
  frequency: string
  days: number
  image?: string
}

// 电子处方信息
export interface ElectronicPrescription {
  id: string
  prescriptionNo: string
  patientName: string
  patientGender: string
  patientAge: number
  diagnosis: string
  createTime: string
  drugs: PrescriptionDrug[]
  totalAmount: number
  doctorAdvice: string
  precautions: string[]
}

// 处方申请状态
export interface PrescriptionApplyState {
  selectedPatient: Patient | null
  selectedDrugs: Drug[]
  selectedDiseases: string[]
  symptoms: string
  medicalHistory: string
  allergyHistory: string
  agreedToConsent: boolean
}

export const usePrescriptionStore = defineStore('prescription', () => {
  // ========== State ==========
  const currentStep = ref<PrescriptionStep>('apply')
  const consultationId = ref<string>('')
  
  // 申请页面状态
  const applyState = ref<PrescriptionApplyState>({
    selectedPatient: null,
    selectedDrugs: [],
    selectedDiseases: [],
    symptoms: '',
    medicalHistory: '',
    allergyHistory: '',
    agreedToConsent: false
  })

  // 医生信息
  const doctorInfo = ref<DoctorInfo | null>(null)

  // 聊天记录
  const chatMessages = ref<ChatMessage[]>([])

  // 电子处方
  const electronicPrescription = ref<ElectronicPrescription | null>(null)

  // 订单ID（用于支付）
  const orderId = ref<string>('')

  // ========== Getters ==========
  const hasSelectedPatient = computed(() => !!applyState.value.selectedPatient)
  const hasSelectedDiseases = computed(() => applyState.value.selectedDiseases.length > 0)
  const canSubmitApply = computed(() => {
    return hasSelectedPatient.value && 
           hasSelectedDiseases.value && 
           applyState.value.agreedToConsent
  })

  const totalDrugAmount = computed(() => {
    if (!electronicPrescription.value) return 0
    return electronicPrescription.value.drugs.reduce((sum, drug) => {
      return sum + drug.price * drug.quantity
    }, 0)
  })

  // ========== Actions ==========
  
  // 设置当前步骤
  const setStep = (step: PrescriptionStep) => {
    currentStep.value = step
  }

  // 设置咨询ID
  const setConsultationId = (id: string) => {
    consultationId.value = id
  }

  // 选择用药人
  const selectPatient = (patient: Patient) => {
    applyState.value.selectedPatient = patient
  }

  // 添加药品
  const addDrug = (drug: Drug) => {
    const exists = applyState.value.selectedDrugs.find(d => d.id === drug.id)
    if (!exists) {
      applyState.value.selectedDrugs.push(drug)
    }
  }

  // 移除药品
  const removeDrug = (drugId: string) => {
    applyState.value.selectedDrugs = applyState.value.selectedDrugs.filter(d => d.id !== drugId)
  }

  // 设置药品列表
  const setSelectedDrugs = (drugs: Drug[]) => {
    applyState.value.selectedDrugs = drugs
  }

  // 选择疾病
  const toggleDisease = (diseaseId: string) => {
    const index = applyState.value.selectedDiseases.indexOf(diseaseId)
    if (index > -1) {
      applyState.value.selectedDiseases.splice(index, 1)
    } else {
      applyState.value.selectedDiseases.push(diseaseId)
    }
  }

  // 设置症状描述
  const setSymptoms = (symptoms: string) => {
    applyState.value.symptoms = symptoms
  }

  // 设置知情同意
  const setAgreedToConsent = (agreed: boolean) => {
    applyState.value.agreedToConsent = agreed
  }

  // 设置医生信息
  const setDoctorInfo = (doctor: DoctorInfo) => {
    doctorInfo.value = doctor
  }

  // 添加聊天消息
  const addChatMessage = (message: ChatMessage) => {
    chatMessages.value.push(message)
  }

  // 设置聊天消息
  const setChatMessages = (messages: ChatMessage[]) => {
    chatMessages.value = messages
  }

  // 设置电子处方
  const setElectronicPrescription = (prescription: ElectronicPrescription) => {
    electronicPrescription.value = prescription
  }

  // 设置订单ID
  const setOrderId = (id: string) => {
    orderId.value = id
  }

  // 重置申请状态
  const resetApplyState = () => {
    applyState.value = {
      selectedPatient: null,
      selectedDrugs: [],
      selectedDiseases: [],
      symptoms: '',
      medicalHistory: '',
      allergyHistory: '',
      agreedToConsent: false
    }
  }

  // 重置所有状态
  const resetAll = () => {
    currentStep.value = 'apply'
    consultationId.value = ''
    resetApplyState()
    doctorInfo.value = null
    chatMessages.value = []
    electronicPrescription.value = null
    orderId.value = ''
  }

  // 提交处方申请
  const submitPrescriptionApply = async (): Promise<string> => {
    if (!applyState.value.selectedPatient) {
      throw new Error('未选择用药人')
    }
    
    if (!applyState.value.selectedPatient.id) {
      throw new Error('患者ID无效')
    }
    
    if (applyState.value.selectedDiseases.length === 0) {
      throw new Error('未选择疾病症状')
    }
    
    if (applyState.value.selectedDrugs.length === 0) {
      throw new Error('未选择药品')
    }
    
    try {
      const selectedDrug = applyState.value.selectedDrugs[0]
      
      // 获取当前选中的规格ID（如果有）
      let specificationId: number | undefined
      if (selectedDrug.specifications && selectedDrug.specifications.length > 0) {
        // 优先使用apply页面保存的选中规格ID
        specificationId = Number((selectedDrug as any).selectedSpecificationId || selectedDrug.specifications[0].id)
      }
      
      // 调用真实API创建问诊记录
      const result = await apiApplyPrescription({
        drugId: selectedDrug.id,
        specificationId: specificationId,
        patientId: parseInt(applyState.value.selectedPatient.id),
        diseases: applyState.value.selectedDiseases.join(','),
        symptoms: applyState.value.symptoms
      })
      
      // http拦截器已经提取了data字段，result就是ConsultationApplyResponse
      const consultationId = result.consultationId
      setConsultationId(consultationId)
      
      console.log('处方申请成功，问诊ID:', consultationId)
      return consultationId
    } catch (error) {
      console.error('提交处方申请失败:', error)
      throw error
    }
  }

  // 发送聊天消息
  const sendChatMessage = async (content: string): Promise<void> => {
    const message: ChatMessage = {
      id: 'MSG' + Date.now(),
      type: 'user',
      content,
      time: new Date().toISOString()
    }
    addChatMessage(message)
    
    // 模拟医生回复
    setTimeout(() => {
      const reply: ChatMessage = {
        id: 'MSG' + (Date.now() + 1),
        type: 'doctor',
        content: '收到您的信息，我正在为您开具处方，请稍候。',
        time: new Date().toISOString(),
        avatar: doctorInfo.value?.avatar
      }
      addChatMessage(reply)
    }, 1000)
  }

  // 完成处方开具
  const completePrescription = async (): Promise<void> => {
    // 模拟生成电子处方
    const mockPrescription: ElectronicPrescription = {
      id: 'PRE' + Date.now(),
      prescriptionNo: 'P' + new Date().toISOString().slice(0, 10).replace(/-/g, '') + Math.floor(Math.random() * 10000).toString().padStart(4, '0'),
      patientName: applyState.value.selectedPatient?.name || '',
      patientGender: applyState.value.selectedPatient?.gender === 'male' ? '男' : '女',
      patientAge: applyState.value.selectedPatient?.age || 0,
      diagnosis: applyState.value.selectedDiseases.join('、'),
      createTime: new Date().toISOString(),
      drugs: applyState.value.selectedDrugs.map(drug => ({
        id: drug.id,
        name: drug.name,
        spec: drug.specification,
        price: drug.price,
        quantity: 1,
        usage: '口服',
        frequency: '每日3次',
        days: 7,
        image: drug.image
      })),
      totalAmount: applyState.value.selectedDrugs.reduce((sum, d) => sum + d.price, 0),
      doctorAdvice: '请按时服药，注意饮食清淡，避免辛辣刺激食物。如症状未缓解，请及时复诊。',
      precautions: [
        '饭后服用，避免空腹',
        '服药期间禁止饮酒',
        '如出现过敏反应请立即停药就医'
      ]
    }
    setElectronicPrescription(mockPrescription)
    setStep('success')
  }

  return {
    // State
    currentStep,
    consultationId,
    applyState,
    doctorInfo,
    chatMessages,
    electronicPrescription,
    orderId,
    // Getters
    hasSelectedPatient,
    hasSelectedDiseases,
    canSubmitApply,
    totalDrugAmount,
    // Actions
    setStep,
    setConsultationId,
    selectPatient,
    addDrug,
    removeDrug,
    setSelectedDrugs,
    toggleDisease,
    setSymptoms,
    setAgreedToConsent,
    setDoctorInfo,
    addChatMessage,
    setChatMessages,
    setElectronicPrescription,
    setOrderId,
    resetApplyState,
    resetAll,
    submitPrescriptionApply,
    sendChatMessage,
    completePrescription
  }
})
