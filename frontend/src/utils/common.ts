// 通用工具函数

/**
 * 节流
 * @param fn 函数
 * @param delay 延迟时间（毫秒）
 * @returns 节流后的函数
 */
export function throttle<T extends (...args: unknown[]) => unknown>(
  fn: T,
  delay: number
): (...args: Parameters<T>) => ReturnType<T> | undefined {
  let lastTime = 0
  let result: ReturnType<T>
  
  return function (this: ThisParameterType<T>, ...args: Parameters<T>) {
    const now = Date.now()
    if (now - lastTime >= delay) {
      result = fn.apply(this, args) as ReturnType<T>
      lastTime = now
    }
    return result
  }
}

/**
 * 防抖
 * @param fn 函数
 * @param delay 延迟时间（毫秒）
 * @returns 防抖后的函数
 */
export function debounce<T extends (...args: unknown[]) => unknown>(
  fn: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timer: ReturnType<typeof setTimeout> | null = null
  
  return function (this: ThisParameterType<T>, ...args: Parameters<T>) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 睡眠
 * @param ms 毫秒
 * @returns Promise
 */
export function sleep(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms))
}

/**
 * 深拷贝
 * @param obj 对象
 * @returns 拷贝后的对象
 */
export function deepClone<T>(obj: T): T {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime()) as unknown as T
  if (obj instanceof Array) return obj.map(item => deepClone(item)) as unknown as T
  if (obj instanceof Object) {
    const cloned: Record<string, unknown> = {}
    for (const key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        cloned[key] = deepClone((obj as Record<string, unknown>)[key])
      }
    }
    return cloned as T
  }
  return obj
}

/**
 * 获取UUID
 * @returns UUID字符串
 */
export function getUUID(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = (Math.random() * 16) | 0
    const v = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}

/**
 * 生成随机字符串
 * @param length 长度
 * @returns 随机字符串
 */
export function randomString(length: number): string {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return result
}

/**
 * 数组去重
 * @param arr 数组
 * @param key 键（对象数组时）
 * @returns 去重后的数组
 */
export function uniqueArray<T>(arr: T[], key?: keyof T): T[] {
  if (!key) {
    return [...new Set(arr)]
  }
  const seen = new Set()
  return arr.filter(item => {
    const val = item[key]
    if (seen.has(val)) return false
    seen.add(val)
    return true
  })
}

/**
 * 数组分组
 * @param arr 数组
 * @param key 分组键
 * @returns 分组后的对象
 */
export function groupBy<T>(arr: T[], key: keyof T): Record<string, T[]> {
  return arr.reduce((result, item) => {
    const groupKey = String(item[key])
    if (!result[groupKey]) {
      result[groupKey] = []
    }
    result[groupKey].push(item)
    return result
  }, {} as Record<string, T[]>)
}

/**
 * 数组分块
 * @param arr 数组
 * @param size 块大小
 * @returns 分块后的数组
 */
export function chunkArray<T>(arr: T[], size: number): T[][] {
  const result: T[][] = []
  for (let i = 0; i < arr.length; i += size) {
    result.push(arr.slice(i, i + size))
  }
  return result
}

/**
 * 树形结构转扁平数组
 * @param tree 树形数据
 * @param childrenKey 子节点键
 * @returns 扁平数组
 */
export function treeToFlat<T extends Record<string, unknown>>(
  tree: T[],
  childrenKey: keyof T = 'children' as keyof T
): T[] {
  const result: T[] = []
  const stack = [...tree]
  
  while (stack.length) {
    const node = stack.pop()!
    result.push(node)
    const children = node[childrenKey] as T[]
    if (children && children.length) {
      stack.push(...children)
    }
  }
  
  return result
}

/**
 * 扁平数组转树形结构
 * @param list 扁平数组
 * @param idKey ID键
 * @param parentKey 父ID键
 * @returns 树形数据
 */
export function flatToTree<T extends Record<string, unknown>>(
  list: T[],
  idKey: keyof T = 'id' as keyof T,
  parentKey: keyof T = 'parentId' as keyof T
): T[] {
  const map: Record<string, T & { children?: T[] }> = {}
  const result: T[] = []
  
  list.forEach(item => {
    const id = String(item[idKey])
    map[id] = { ...item, children: [] }
  })
  
  list.forEach(item => {
    const id = String(item[idKey])
    const parentId = item[parentKey]
    const node = map[id]
    
    if (parentId && map[String(parentId)]) {
      if (!map[String(parentId)].children) {
        map[String(parentId)].children = []
      }
      map[String(parentId)].children!.push(node)
    } else {
      result.push(node)
    }
  })
  
  return result
}

/**
 * 千分位格式化
 * @param num 数字
 * @returns 千分位字符串
 */
export function toThousands(num: number): string {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

/**
 * 下载文件
 * @param content 内容
 * @param filename 文件名
 * @param type MIME类型
 */
export function downloadFile(content: BlobPart, filename: string, type = 'text/plain'): void {
  const blob = new Blob([content], { type })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

/**
 * 复制到剪贴板
 * @param text 文本
 * @returns 是否成功
 */
export async function copyToClipboard(text: string): Promise<boolean> {
  try {
    await navigator.clipboard.writeText(text)
    return true
  } catch (error) {
    // 降级方案
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.style.position = 'fixed'
    textarea.style.opacity = '0'
    document.body.appendChild(textarea)
    textarea.select()
    const result = document.execCommand('copy')
    document.body.removeChild(textarea)
    return result
  }
}
