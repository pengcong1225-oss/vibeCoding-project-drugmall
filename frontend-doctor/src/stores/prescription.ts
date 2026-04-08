import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

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
      await new Promise(resolve => setTimeout(resolve, 500))
      
      prescriptions.value = [
        {
          id: 'PRES202412070001',
          patientId: 'P001',
          patientName: '李*',
          patientAge: 35,
          patientGender: '女',
          consultationId: 'C001',
          diagnosis: '急性上呼吸道感染、发热',
          drugs: [
            { id: 'D001', name: '布洛芬缓释胶囊', spec: '0.3g*20粒', unit: '盒', price: 25.00, quantity: 1, dosage: '1粒', frequency: '每日2次', duration: '3天' },
            { id: 'D002', name: '感冒灵颗粒', spec: '10g*9袋', unit: '盒', price: 18.50, quantity: 2, dosage: '1袋', frequency: '每日3次', duration: '3天' }
          ],
          totalAmount: 62.00,
          status: 'pending',
          statusText: '待审核',
          createTime: '2024-12-07 11:30:00'
        },
        {
          id: 'PRES202412070002',
          patientId: 'P002',
          patientName: '王*',
          patientAge: 28,
          patientGender: '男',
          consultationId: 'C002',
          diagnosis: '慢性胃炎',
          drugs: [
            { id: 'D003', name: '奥美拉唑肠溶胶囊', spec: '20mg*28粒', unit: '盒', price: 45.00, quantity: 1, dosage: '1粒', frequency: '每日1次', duration: '14天', remark: '饭前服用' }
          ],
          totalAmount: 45.00,
          status: 'approved',
          statusText: '已通过',
          createTime: '2024-12-07 10:15:00',
          pharmacist: '李药师',
          reviewTime: '2024-12-07 10:30:00'
        },
        {
          id: 'PRES202412060003',
          patientId: 'P003',
          patientName: '张*',
          patientAge: 42,
          patientGender: '女',
          consultationId: 'C003',
          diagnosis: '过敏性鼻炎',
          drugs: [
            { id: 'D004', name: '氯雷他定片', spec: '10mg*6片', unit: '盒', price: 12.50, quantity: 1, dosage: '1片', frequency: '每日1次', duration: '6天' }
          ],
          totalAmount: 12.50,
          status: 'rejected',
          statusText: '已拒绝',
          createTime: '2024-12-06 15:00:00',
          pharmacist: '王药师',
          reviewTime: '2024-12-06 15:15:00',
          rejectReason: '药品库存不足，请更换其他药品'
        }
      ]
    } finally {
      loading.value = false
    }
  }

  const fetchPrescriptionDetail = async (id: string) => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 300))
      
      const prescription = prescriptions.value.find(p => p.id === id)
      if (prescription) {
        currentPrescription.value = prescription
      }
    } finally {
      loading.value = false
    }
  }

  const createPrescription = async (data: Partial<Prescription>) => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 500))
      
      const newPrescription: Prescription = {
        id: `PRES${Date.now()}`,
        patientId: data.patientId || '',
        patientName: data.patientName || '',
        patientAge: data.patientAge || 0,
        patientGender: data.patientGender || '',
        consultationId: data.consultationId || '',
        diagnosis: data.diagnosis || '',
        drugs: data.drugs || [],
        totalAmount: data.totalAmount || 0,
        status: 'pending',
        statusText: '待审核',
        createTime: new Date().toISOString()
      }
      
      prescriptions.value.unshift(newPrescription)
      return newPrescription
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
