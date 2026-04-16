import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { homeApi } from '@/api/modules/home'
import type { HomePageConfig, HomeSection } from '@/types/home'

export const useHomeStore = defineStore('home', () => {
  // State
  const loading = ref(false)
  const error = ref<string | null>(null)
  const pageConfig = ref<HomePageConfig['pageConfig'] | null>(null)
  const sections = ref<HomeSection[]>([])

  // Getters
  /** 获取可见且排序后的模块列表 */
  const visibleSections = computed(() => {
    return sections.value
      .filter(s => s.visible)
      .sort((a, b) => a.sortOrder - b.sortOrder)
  })

  // Actions
  /**
   * 获取首页配置
   */
  async function fetchHomePageConfig() {
    loading.value = true
    error.value = null
    try {
      const data = await homeApi.getHomePageRender()
      if (data) {
        pageConfig.value = data.pageConfig
        sections.value = data.sections
      }
    } catch (e: any) {
      error.value = e.message || '获取首页配置失败'
      console.error('获取首页配置失败:', e)
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    pageConfig,
    sections,
    visibleSections,
    fetchHomePageConfig
  }
})
