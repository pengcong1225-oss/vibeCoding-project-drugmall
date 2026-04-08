import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface Patient {
  id: string
  name: string
  age: number
  gender: '男' | '女'
  phone: string
  avatar: string
  tags: string[]
  diagnosis: string[]
  lastVisit: string
  visitCount: number
  isVip: boolean
  allergies?: string
  medicalHistory?: string
}

export interface MedicalRecord {
  id: string
  patientId: string
  date: string
  type: '初诊' | '复诊'
  diagnosis: string
  prescription: string
  notes: string
  doctor: string
}

export const usePatientStore = defineStore('patient', () => {
  // State
  const patients = ref<Patient[]>([])
  const currentPatient = ref<Patient | null>(null)
  const medicalRecords = ref<MedicalRecord[]>([])
  const loading = ref(false)

  // Actions
  const fetchPatients = async () => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 500))
      
      patients.value = [
        {
          id: 'P001',
          name: '李*',
          age: 62,
          gender: '女',
          phone: '138****5678',
          avatar: '',
          tags: ['高血压', '糖尿病', '慢病管理', 'VIP患者'],
          diagnosis: ['高血压', '糖尿病'],
          lastVisit: '2天前',
          visitCount: 5,
          isVip: true,
          allergies: '青霉素过敏',
          medicalHistory: '高血压5年，糖尿病3年'
        },
        {
          id: 'P002',
          name: '王*',
          age: 45,
          gender: '男',
          phone: '139****1234',
          avatar: '',
          tags: ['上呼吸道感染', '普通患者'],
          diagnosis: ['上呼吸道感染'],
          lastVisit: '5天前',
          visitCount: 3,
          isVip: false
        },
        {
          id: 'P003',
          name: '张*',
          age: 38,
          gender: '女',
          phone: '136****8888',
          avatar: '',
          tags: ['慢性胃炎', '复诊患者'],
          diagnosis: ['慢性胃炎'],
          lastVisit: '1周前',
          visitCount: 8,
          isVip: false
        }
      ]
    } finally {
      loading.value = false
    }
  }

  const fetchPatientDetail = async (id: string) => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 300))
      
      const patient = patients.value.find(p => p.id === id)
      if (patient) {
        currentPatient.value = patient
      }
    } finally {
      loading.value = false
    }
  }

  const fetchMedicalRecords = async (patientId: string) => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 400))
      
      medicalRecords.value = [
        {
          id: 'MR001',
          patientId,
          date: '2024-12-05 14:30',
          type: '复诊',
          diagnosis: '高血压、糖尿病',
          prescription: '硝苯地平、二甲双胍',
          notes: '血压控制良好，继续用药',
          doctor: '张医生'
        },
        {
          id: 'MR002',
          patientId,
          date: '2024-11-15 10:00',
          type: '初诊',
          diagnosis: '上呼吸道感染',
          prescription: '感冒灵、阿莫西林',
          notes: '注意休息，多饮水',
          doctor: '张医生'
        },
        {
          id: 'MR003',
          patientId,
          date: '2024-10-20 15:30',
          type: '复诊',
          diagnosis: '急性肠胃炎',
          prescription: '诺氟沙星、蒙脱石散',
          notes: '饮食清淡，避免油腻',
          doctor: '张医生'
        }
      ]
    } finally {
      loading.value = false
    }
  }

  return {
    patients,
    currentPatient,
    medicalRecords,
    loading,
    fetchPatients,
    fetchPatientDetail,
    fetchMedicalRecords
  }
})
