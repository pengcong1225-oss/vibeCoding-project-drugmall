import { ref, computed, onUnmounted } from 'vue'

/**
 * 倒计时组合式函数
 * @param initialSeconds 初始秒数
 * @returns 倒计时相关状态和方法
 */
export function useCountdown(initialSeconds: number = 60) {
  const seconds = ref(initialSeconds)
  const isRunning = ref(false)
  let timer: ReturnType<typeof setInterval> | null = null

  // 格式化的倒计时显示
  const formatted = computed(() => {
    const mins = Math.floor(seconds.value / 60)
    const secs = seconds.value % 60
    return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
  })

  // 开始倒计时
  const start = (secs?: number) => {
    if (timer) clearInterval(timer)
    if (secs !== undefined) {
      seconds.value = secs
    }
    isRunning.value = true
    timer = setInterval(() => {
      if (seconds.value > 0) {
        seconds.value--
      } else {
        stop()
      }
    }, 1000)
  }

  // 停止倒计时
  const stop = () => {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
    isRunning.value = false
  }

  // 重置倒计时
  const reset = (secs: number = initialSeconds) => {
    stop()
    seconds.value = secs
  }

  // 组件卸载时清理
  onUnmounted(() => {
    stop()
  })

  return {
    seconds,
    isRunning,
    formatted,
    start,
    stop,
    reset
  }
}

export default useCountdown
