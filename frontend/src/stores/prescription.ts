import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Drug } from '@/types/drug'
import type { Patient } from '@/types/user'
import { applyPrescription as apiApplyPrescription } from '@/api/consultation'
import { sendConsultationMessage } from '@/api/modules/inquiry'
import { getPrescriptionList } from '@/api/modules/prescription'

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
  const assignedDoctorId = ref<string>('')
  
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
      assignedDoctorId.value = result.doctorId || ''

      console.log('处方申请成功，问诊ID:', consultationId, '医生ID:', assignedDoctorId.value)
      return consultationId
    } catch (error) {
      console.error('提交处方申请失败:', error)
      throw error
    }
  }

  // 发送聊天消息（通过后端API持久化）
  const sendChatMessage = async (content: string): Promise<void> => {
    if (!consultationId.value) {
      console.warn('[处方Store] 无问诊ID，无法发送消息')
      return
    }

    const message: ChatMessage = {
      id: 'MSG' + Date.now(),
      type: 'user',
      content,
      time: new Date().toISOString()
    }
    addChatMessage(message)

    try {
      await sendConsultationMessage(consultationId.value, { type: 'text', content })
      console.log('[处方Store] 消息已通过后端API发送')
    } catch (error) {
      console.error('[处方Store] 发送消息失败:', error)
      throw error
    }
  }

  // 从后端加载最新处方数据
  const loadLatestPrescription = async (): Promise<ElectronicPrescription | null> => {
    try {
      const prescriptions = await getPrescriptionList('all')
      if (!prescriptions || prescriptions.length === 0) {
        console.warn('[处方Store] 未找到处方记录')
        return null
      }

      // 取最新的处方
      const latest = prescriptions[0]
      const electronic: ElectronicPrescription = {
        id: latest.id,
        prescriptionNo: latest.prescriptionNo || latest.id,
        patientName: latest.patientName || applyState.value.selectedPatient?.name || '',
        patientGender: latest.patientGender || (applyState.value.selectedPatient?.gender === 'male' ? '男' : '女'),
        patientAge: latest.patientAge || applyState.value.selectedPatient?.age || 0,
        diagnosis: latest.diagnosis || '',
        createTime: latest.createTime || new Date().toISOString(),
        drugs: (latest.drugs || []).map(d => ({
          id: d.name,
          name: d.name,
          spec: d.spec || '',
          price: 0,
          quantity: d.quantity || 1,
          usage: d.usage || '口服',
          frequency: d.frequency || '每日3次',
          days: d.days || 7
        })),
        totalAmount: 0,
        doctorAdvice: '',
        precautions: []
      }

      setElectronicPrescription(electronic)
      setStep('success')
      return electronic
    } catch (error) {
      console.error('[处方Store] 加载处方失败:', error)
      return null
    }
  }

  // 完成处方开具（保留兼容旧调用，实际从后端加载）
  const completePrescription = async (): Promise<void> => {
    await loadLatestPrescription()
  }

  return {
    // State
    currentStep,
    consultationId,
    assignedDoctorId,
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
    completePrescription,
    loadLatestPrescription
  }
})
})
