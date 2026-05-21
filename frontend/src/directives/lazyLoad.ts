import type { Directive, DirectiveBinding } from 'vue'

interface LazyOptions {
  threshold?: number
  rootMargin?: string
  placeholder?: string
  errorImage?: string
}

const defaultOptions: LazyOptions = {
  threshold: 0.01,
  rootMargin: '50px',
  placeholder: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1 1"%3E%3C/svg%3E',
  errorImage: ''
}

const imageCache = new Set<string>()

const loadImage = (src: string): Promise<void> => {
  return new Promise((resolve, reject) => {
    if (imageCache.has(src)) {
      resolve()
      return
    }
    const img = new Image()
    img.onload = () => {
      imageCache.add(src)
      resolve()
    }
    img.onerror = reject
    img.src = src
  })
}

export const lazyLoad: Directive = {
  mounted(el: HTMLImageElement, binding: DirectiveBinding<string | LazyOptions>) {
    const options: LazyOptions = typeof binding.value === 'string'
      ? { ...defaultOptions, placeholder: binding.value }
      : { ...defaultOptions, ...binding.value }

    // 保存原始src
    const originalSrc = el.getAttribute('data-src') || el.src
    if (!originalSrc || originalSrc === options.placeholder) {
      return
    }

    el.setAttribute('data-src', originalSrc)
    el.src = options.placeholder || defaultOptions.placeholder!
    el.classList.add('lazy-image', 'lazy-loading')

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            const target = entry.target as HTMLImageElement
            const src = target.getAttribute('data-src')

            if (src) {
              loadImage(src)
                .then(() => {
                  target.src = src
                  target.classList.remove('lazy-loading')
                  target.classList.add('lazy-loaded')
                })
                .catch(() => {
                  if (options.errorImage) {
                    target.src = options.errorImage
                  }
                  target.classList.remove('lazy-loading')
                  target.classList.add('lazy-error')
                })
            }

            observer.unobserve(target)
          }
        })
      },
      {
        threshold: options.threshold,
        rootMargin: options.rootMargin
      }
    )

    observer.observe(el)

    // 保存observer实例用于清理
    ;(el as any).__lazyObserver = observer
  },

  unmounted(el: HTMLImageElement) {
    const observer = (el as any).__lazyObserver
    if (observer) {
      observer.disconnect()
      delete (el as any).__lazyObserver
    }
  }
}

export default lazyLoad
