import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getPrescriptionList, getPrescriptionDetail, createPrescription as apiCreatePrescription } from '@/api/prescription'
import type { CreatePrescriptionDTO } from '@/api/prescription'

export interface PrescriptionDrug {
  id: string
  name: string
  spec: string
  unit: string
  price: number
  quantity: number
  dosage: string
  frequency: string
  duration: string
  remark?: string
}

export interface Prescription {
  id: string
  patientId: string
  patientName: string
  patientAge: number
  patientGender: string
  consultationId: string
  consultationStatus?: string
  consultationSymptom?: string
  consultationType?: string
  diagnosis: string
  drugs: PrescriptionDrug[]
  totalAmount: number
  status: 'pending' | 'approved' | 'rejected' | 'cancelled'
  statusText: string
  createTime: string
  pharmacist?: string
  reviewTime?: string
  rejectReason?: string
}

export const usePrescriptionStore = defineStore('prescription', () => {
  // State
  const prescriptions = ref<Prescription[]>([])
  const currentPrescription = ref<Prescription | null>(null)
  const loading = ref(false)

  // Getters
  const pendingPrescriptions = computed(() => 
    prescriptions.value.filter(p => p.status === 'pending')
  )
  
  const approvedPrescriptions = computed(() => 
    prescriptions.value.filter(p => p.status === 'approved')
  )
  
  const rejectedPrescriptions = computed(() => 
    prescriptions.value.filter(p => p.status === 'rejected')
  )

  // Actions
  const fetchPrescriptions = async (status?: string) => {
    loading.value = true
    try {
      const data = await getPrescriptionList(status || 'all')
      prescriptions.value = data || []
    } catch (error) {
      console.error('获取处方列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  const fetchPrescriptionDetail = async (id: string) => {
    loading.value = true
    try {
      const data = await getPrescriptionDetail(id)
      currentPrescription.value = data || null
      return data
    } catch (error) {
      console.error('获取处方详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createPrescription = async (data: CreatePrescriptionDTO) => {
    loading.value = true
    try {
      const prescription = await apiCreatePrescription(data)
      prescriptions.value.unshift(prescription)
      return prescription
    } catch (error) {
      console.error('创建处方失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updatePrescription = async (id: string, data: Partial<Prescription>) => {
    const index = prescriptions.value.findIndex(p => p.id === id)
    if (index !== -1) {
      prescriptions.value[index] = { ...prescriptions.value[index], ...data }
    }
  }

  return {
    prescriptions,
    currentPrescription,
    loading,
    pendingPrescriptions,
    approvedPrescriptions,
    rejectedPrescriptions,
    fetchPrescriptions,
    fetchPrescriptionDetail,
    createPrescription,
    updatePrescription
  }
})
