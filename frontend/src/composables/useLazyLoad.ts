import { ref, onMounted, onUnmounted, type Ref } from 'vue'

interface LazyLoadOptions {
  root?: HTMLElement | null
  rootMargin?: string
  threshold?: number | number[]
}

/**
 * 懒加载组合式函数
 * @param options 配置选项
 * @returns 懒加载相关状态和方法
 */
export function useLazyLoad(options: LazyLoadOptions = {}) {
  const { root = null, rootMargin = '0px', threshold = 0 } = options
  
  const targetRef = ref<HTMLElement | null>(null)
  const isIntersecting = ref(false)
  const isLoaded = ref(false)
  let observer: IntersectionObserver | null = null

  const initObserver = () => {
    if (!targetRef.value) return

    observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            isIntersecting.value = true
            isLoaded.value = true
            // 加载完成后取消观察
            if (observer && targetRef.value) {
              observer.unobserve(targetRef.value)
            }
          }
        })
      },
      { root, rootMargin, threshold }
    )

    observer.observe(targetRef.value)
  }

  onMounted(() => {
    initObserver()
  })

  onUnmounted(() => {
    if (observer) {
      observer.disconnect()
      observer = null
    }
  })

  return {
    targetRef,
    isIntersecting,
    isLoaded
  }
}

export default useLazyLoad
