import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface UserInfo {
  id: string
  phone: string
  nickname: string
  avatar: string
  isAuthenticated: boolean
}

export interface Patient {
  id: string
  name: string
  gender: 'male' | 'female'
  age: number
  idCard: string
  phone: string
  relationship: string
  isDefault: boolean
}

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)
  const patients = ref<Patient[]>([])
  
  // Getters
  const isLoggedIn = computed(() => !!token.value)
  
  const defaultPatient = computed(() => {
    return patients.value.find(p => p.isDefault) || patients.value[0] || null
  })
  
  // Actions
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  const login = async (phone: string, code: string) => {
    // 模拟登录
    const mockToken = 'mock_token_' + Date.now()
    const mockUserInfo: UserInfo = {
      id: '1',
      phone,
      nickname: '用户' + phone.slice(-4),
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      isAuthenticated: true
    }
    
    setToken(mockToken)
    setUserInfo(mockUserInfo)
    
    // 加载患者列表
    loadPatients()
    
    return { success: true }
  }
  
  const logout = () => {
    token.value = ''
    userInfo.value = null
    patients.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('patients')
  }
  
  const loadPatients = () => {
    const data = localStorage.getItem('patients')
    if (data) {
      patients.value = JSON.parse(data)
    } else {
      // 模拟默认患者
      patients.value = [
        {
          id: '1',
          name: '张三',
          gender: 'male',
          age: 30,
          idCard: '110101199001011234',
          phone: '13800138000',
          relationship: '本人',
          isDefault: true
        }
      ]
      savePatients()
    }
  }
  
  const savePatients = () => {
    localStorage.setItem('patients', JSON.stringify(patients.value))
  }
  
  const addPatient = (patient: Omit<Patient, 'id'>) => {
    const newPatient: Patient = {
      ...patient,
      id: Date.now().toString()
    }
    patients.value.push(newPatient)
    savePatients()
  }
  
  const updatePatient = (id: string, data: Partial<Patient>) => {
    const index = patients.value.findIndex(p => p.id === id)
    if (index > -1) {
      patients.value[index] = { ...patients.value[index], ...data }
      savePatients()
    }
  }
  
  const deletePatient = (id: string) => {
    const index = patients.value.findIndex(p => p.id === id)
    if (index > -1) {
      patients.value.splice(index, 1)
      savePatients()
    }
  }
  
  const setDefaultPatient = (id: string) => {
    patients.value.forEach(p => {
      p.isDefault = p.id === id
    })
    savePatients()
  }
  
  // 初始化加载
  const init = () => {
    const savedToken = localStorage.getItem('token')
    const savedUserInfo = localStorage.getItem('userInfo')
    
    if (savedToken) {
      token.value = savedToken
    }
    
    if (savedUserInfo) {
      userInfo.value = JSON.parse(savedUserInfo)
      loadPatients()
    }
  }
  
  init()
  
  return {
    token,
    userInfo,
    patients,
    isLoggedIn,
    defaultPatient,
    setToken,
    setUserInfo,
    login,
    logout,
    addPatient,
    updatePatient,
    deletePatient,
    setDefaultPatient
  }
})
