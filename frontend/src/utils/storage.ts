// 本地存储工具函数

type StorageType = 'local' | 'session'

interface StorageItem<T> {
  value: T
  expire?: number
}

/**
 * 设置存储
 * @param key 键
 * @param value 值
 * @param type 存储类型
 * @param expire 过期时间（毫秒）
 */
export function setStorage<T>(
  key: string,
  value: T,
  type: StorageType = 'local',
  expire?: number
): void {
  const storage = type === 'local' ? localStorage : sessionStorage
  const item: StorageItem<T> = { value }
  
  if (expire && expire > 0) {
    item.expire = Date.now() + expire
  }
  
  try {
    storage.setItem(key, JSON.stringify(item))
  } catch (error) {
    console.error('Storage set error:', error)
  }
}

/**
 * 获取存储
 * @param key 键
 * @param type 存储类型
 * @returns 值
 */
export function getStorage<T>(key: string, type: StorageType = 'local'): T | null {
  const storage = type === 'local' ? localStorage : sessionStorage
  
  try {
    const data = storage.getItem(key)
    if (!data) return null
    
    const item: StorageItem<T> = JSON.parse(data)
    
    // 检查是否过期
    if (item.expire && Date.now() > item.expire) {
      storage.removeItem(key)
      return null
    }
    
    return item.value
  } catch (error) {
    console.error('Storage get error:', error)
    return null
  }
}

/**
 * 移除存储
 * @param key 键
 * @param type 存储类型
 */
export function removeStorage(key: string, type: StorageType = 'local'): void {
  const storage = type === 'local' ? localStorage : sessionStorage
  storage.removeItem(key)
}

/**
 * 清空存储
 * @param type 存储类型
 */
export function clearStorage(type: StorageType = 'local'): void {
  const storage = type === 'local' ? localStorage : sessionStorage
  storage.clear()
}

/**
 * 获取存储并设置默认值
 * @param key 键
 * @param defaultValue 默认值
 * @param type 存储类型
 * @returns 值
 */
export function getStorageWithDefault<T>(
  key: string,
  defaultValue: T,
  type: StorageType = 'local'
): T {
  const value = getStorage<T>(key, type)
  return value !== null ? value : defaultValue
}

/**
 * Token工具
 */
export const tokenStorage = {
  get(): string | null {
    return getStorage('token')
  },
  set(token: string, expire?: number): void {
    setStorage('token', token, 'local', expire)
  },
  remove(): void {
    removeStorage('token')
  }
}

/**
 * 用户信息工具
 */
export const userStorage = {
  get<T>(): T | null {
    return getStorage('userInfo')
  },
  set<T>(userInfo: T): void {
    setStorage('userInfo', userInfo)
  },
  remove(): void {
    removeStorage('userInfo')
  }
}

/**
 * 购物车工具
 */
export const cartStorage = {
  get<T>(): T | null {
    return getStorage('cart')
  },
  set<T>(cart: T): void {
    setStorage('cart', cart)
  },
  remove(): void {
    removeStorage('cart')
  }
}
