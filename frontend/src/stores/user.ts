import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as userApi from '@/api/modules/user'
import type { UserInfo, Patient } from '@/types'

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
    try {
      const result = await userApi.login({ phone, code })
      setToken(result.token)
      setUserInfo(result.userInfo)
      // 加载患者列表
      await loadPatients()
      return { success: true }
    } catch (error) {
      console.error('登录失败:', error)
      return { success: false, error }
    }
  }

  const logout = async () => {
    try {
      await userApi.logout()
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      token.value = ''
      userInfo.value = null
      patients.value = []
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }

  const fetchUserInfo = async () => {
    try {
      const info = await userApi.getUserInfo()
      setUserInfo(info)
      return info
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  const loadPatients = async () => {
    try {
      const list = await userApi.getPatients()
      patients.value = list
      return list
    } catch (error) {
      console.error('获取就诊人列表失败:', error)
      patients.value = []
      return []
    }
  }

  const addPatient = async (patient: Omit<Patient, 'id'>) => {
    try {
      const newPatient = await userApi.addPatient(patient)
      patients.value.push(newPatient)
      return newPatient
    } catch (error) {
      console.error('添加就诊人失败:', error)
      throw error
    }
  }

  const updatePatient = async (id: string, data: Partial<Patient>) => {
    try {
      const updated = await userApi.updatePatient(id, data)
      const index = patients.value.findIndex(p => p.id === id)
      if (index > -1) {
        patients.value[index] = updated
      }
      return updated
    } catch (error) {
      console.error('更新就诊人失败:', error)
      throw error
    }
  }

  const deletePatient = async (id: string) => {
    try {
      await userApi.deletePatient(id)
      const index = patients.value.findIndex(p => p.id === id)
      if (index > -1) {
        patients.value.splice(index, 1)
      }
    } catch (error) {
      console.error('删除就诊人失败:', error)
      throw error
    }
  }

  const setDefaultPatient = async (id: string) => {
    try {
      await userApi.setDefaultPatient(id)
      patients.value.forEach(p => {
        p.isDefault = p.id === id
      })
    } catch (error) {
      console.error('设置默认就诊人失败:', error)
      throw error
    }
  }

  // 初始化加载
  const init = () => {
    const savedToken = localStorage.getItem('token')
    const savedUserInfo = localStorage.getItem('userInfo')

    if (savedToken) {
      token.value = savedToken
    }

    if (savedUserInfo) {
      try {
        userInfo.value = JSON.parse(savedUserInfo)
      } catch {
        userInfo.value = null
      }
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
    fetchUserInfo,
    loadPatients,
    addPatient,
    updatePatient,
    deletePatient,
    setDefaultPatient
  }
})
