import { ref, onMounted, onUnmounted } from 'vue'

interface ScrollOptions {
  threshold?: number
  immediate?: boolean
}

interface ScrollInfo {
  scrollTop: number
  scrollHeight: number
  clientHeight: number
  scrollBottom: number
  scrollPercent: number
  direction: 'up' | 'down' | 'none'
}

/**
 * 滚动组合式函数
 * @param options 配置选项
 * @returns 滚动相关状态和方法
 */
export function useScroll(options: ScrollOptions = {}) {
  const { threshold = 100, immediate = false } = options
  
  const scrollRef = ref<HTMLElement | Window | null>(null)
  const isScrolling = ref(false)
  const isReachBottom = ref(false)
  const isReachTop = ref(true)
  const scrollInfo = ref<ScrollInfo>({
    scrollTop: 0,
    scrollHeight: 0,
    clientHeight: 0,
    scrollBottom: 0,
    scrollPercent: 0,
    direction: 'none'
  })
  
  let scrollTimer: ReturnType<typeof setTimeout> | null = null
  let lastScrollTop = 0

  const getScrollElement = (): HTMLElement => {
    if (!scrollRef.value || scrollRef.value === window) {
      return document.documentElement || document.body
    }
    return scrollRef.value as HTMLElement
  }

  const updateScrollInfo = () => {
    const el = getScrollElement()
    const scrollTop = el.scrollTop || window.pageYOffset
    const scrollHeight = el.scrollHeight
    const clientHeight = el.clientHeight
    
    scrollInfo.value = {
      scrollTop,
      scrollHeight,
      clientHeight,
      scrollBottom: scrollHeight - scrollTop - clientHeight,
      scrollPercent: scrollHeight > 0 ? (scrollTop / scrollHeight) * 100 : 0,
      direction: scrollTop > lastScrollTop ? 'down' : 'up'
    }
    
    lastScrollTop = scrollTop
    isReachTop.value = scrollTop <= 0
    isReachBottom.value = scrollHeight - scrollTop - clientHeight <= threshold
  }

  const handleScroll = () => {
    isScrolling.value = true
    updateScrollInfo()
    
    if (scrollTimer) {
      clearTimeout(scrollTimer)
    }
    
    scrollTimer = setTimeout(() => {
      isScrolling.value = false
    }, 150)
  }

  const scrollTo = (position: number, behavior: ScrollBehavior = 'smooth') => {
    const el = getScrollElement()
    el.scrollTo({ top: position, behavior })
  }

  const scrollToTop = (behavior: ScrollBehavior = 'smooth') => {
    scrollTo(0, behavior)
  }

  const scrollToBottom = (behavior: ScrollBehavior = 'smooth') => {
    const el = getScrollElement()
    scrollTo(el.scrollHeight, behavior)
  }

  onMounted(() => {
    if (immediate) {
      updateScrollInfo()
    }
    
    const el = scrollRef.value || window
    el.addEventListener('scroll', handleScroll, { passive: true })
  })

  onUnmounted(() => {
    const el = scrollRef.value || window
    el.removeEventListener('scroll', handleScroll)
    
    if (scrollTimer) {
      clearTimeout(scrollTimer)
    }
  })

  return {
    scrollRef,
    isScrolling,
    isReachTop,
    isReachBottom,
    scrollInfo,
    scrollTo,
    scrollToTop,
    scrollToBottom
  }
}

export default useScroll
