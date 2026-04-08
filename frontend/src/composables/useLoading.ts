import { ref } from 'vue'

interface LoadingOptions {
  initialValue?: boolean
  minDuration?: number
  delay?: number
}

interface TaskOptions {
  minDuration?: number
  onSuccess?: () => void
  onError?: (error: Error) => void
}

/**
 * 加载状态组合式函数
 * @param options 配置选项
 * @returns 加载状态和方法
 */
export function useLoading(options: LoadingOptions = {}) {
  const { initialValue = false, minDuration = 0, delay = 0 } = options
  
  const loading = ref(initialValue)
  const loadingText = ref('')
  let loadingTimer: ReturnType<typeof setTimeout> | null = null
  let startTime = 0

  /**
   * 开始加载
   * @param text 加载提示文本
   */
  const startLoading = (text?: string) => {
    if (loadingTimer) {
      clearTimeout(loadingTimer)
    }
    
    const showLoading = () => {
      loading.value = true
      loadingText.value = text || ''
      startTime = Date.now()
    }
    
    if (delay > 0) {
      loadingTimer = setTimeout(showLoading, delay)
    } else {
      showLoading()
    }
  }

  /**
   * 结束加载
   */
  const stopLoading = () => {
    if (loadingTimer) {
      clearTimeout(loadingTimer)
      loadingTimer = null
    }
    
    const elapsed = Date.now() - startTime
    const remaining = Math.max(0, minDuration - elapsed)
    
    if (remaining > 0) {
      setTimeout(() => {
        loading.value = false
        loadingText.value = ''
      }, remaining)
    } else {
      loading.value = false
      loadingText.value = ''
    }
  }

  /**
   * 切换加载状态
   */
  const toggleLoading = () => {
    if (loading.value) {
      stopLoading()
    } else {
      startLoading()
    }
  }

  /**
   * 执行异步任务
   * @param task 异步任务
   * @param options 任务选项
   * @returns 任务结果
   */
  const runWithLoading = async <T>(
    task: () => Promise<T>,
    options: TaskOptions = {}
  ): Promise<T | undefined> => {
    const { onSuccess, onError } = options
    
    startLoading()
    
    try {
      const result = await task()
      onSuccess?.()
      return result
    } catch (error) {
      onError?.(error as Error)
      throw error
    } finally {
      stopLoading()
    }
  }

  return {
    loading,
    loadingText,
    startLoading,
    stopLoading,
    toggleLoading,
    runWithLoading
  }
}

export default useLoading
