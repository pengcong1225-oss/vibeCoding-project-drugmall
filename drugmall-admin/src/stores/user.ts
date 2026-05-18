import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { LoginParams, UserInfo } from '@/types/user'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/user'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const permissions = ref<string[]>([])
  const roles = ref<string[]>([])

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const hasPermission = computed(() => (permission: string) => {
    return permissions.value.includes(permission) || permissions.value.includes('*')
  })
  const hasRole = computed(() => (role: string) => {
    return roles.value.includes(role)
  })

  // Actions
  async function login(params: LoginParams) {
    try {
      const res = await loginApi(params)
      token.value = res.token
      userInfo.value = res.userInfo
      permissions.value = res.permissions || []
      roles.value = res.roles || []
      
      // 保存到 localStorage
      localStorage.setItem('token', res.token)
      
      return true
    } catch (error) {
      return false
    }
  }

  async function getUserInfo() {
    try {
      const res = await getUserInfoApi()
      userInfo.value = res
      permissions.value = res.permissions || []
      roles.value = res.roles || []
      return res
    } catch (error) {
      // 如果获取失败，不抛出异常，返回 null
      console.warn('获取用户信息失败')
      return null
    }
  }

  async function logout() {
    // 清除所有状态
    token.value = ''
    userInfo.value = null
    permissions.value = []
    roles.value = []
    
    // 清除 localStorage
    localStorage.removeItem('token')
    
    // 跳转到登录页
    router.push('/login')
  }

  function checkLoginStatus() {
    const savedToken = localStorage.getItem('token')
    if (savedToken) {
      token.value = savedToken
      // 尝试获取用户信息
      getUserInfo().catch(() => {
        // 如果获取失败，清除登录状态
        logout()
      })
    }
  }

  return {
    // State
    token,
    userInfo,
    permissions,
    roles,
    // Getters
    isLoggedIn,
    hasPermission,
    hasRole,
    // Actions
    login,
    getUserInfo,
    logout,
    checkLoginStatus
  }
})
