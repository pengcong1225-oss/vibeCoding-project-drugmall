import { describe, it, expect, beforeEach, vi } from 'vitest'
import {
  setStorage,
  getStorage,
  removeStorage,
  clearStorage,
  getStorageWithDefault,
  tokenStorage,
  userStorage,
  cartStorage
} from './storage'

describe('Storage Utils', () => {
  let localStorageMock: Storage
  let sessionStorageMock: Storage

  beforeEach(() => {
    // Reset mocks
    vi.clearAllMocks()
    localStorageMock = localStorage
    sessionStorageMock = sessionStorage
  })

  describe('setStorage', () => {
    it('sets localStorage item correctly', () => {
      setStorage('test-key', 'test-value')
      expect(localStorageMock.setItem).toHaveBeenCalledWith(
        'test-key',
        JSON.stringify({ value: 'test-value' })
      )
    })

    it('sets sessionStorage item correctly', () => {
      setStorage('test-key', 'test-value', 'session')
      expect(sessionStorageMock.setItem).toHaveBeenCalledWith(
        'test-key',
        JSON.stringify({ value: 'test-value' })
      )
    })

    it('sets item with expiration', () => {
      const now = Date.now()
      vi.spyOn(Date, 'now').mockReturnValue(now)
      
      setStorage('test-key', 'test-value', 'local', 3600000)
      
      expect(localStorageMock.setItem).toHaveBeenCalledWith(
        'test-key',
        JSON.stringify({ value: 'test-value', expire: now + 3600000 })
      )
    })

    it('handles setItem error gracefully', () => {
      vi.spyOn(console, 'error').mockImplementation(() => {})
      vi.spyOn(localStorageMock, 'setItem').mockImplementation(() => {
        throw new Error('Storage full')
      })
      
      expect(() => setStorage('test-key', 'test-value')).not.toThrow()
    })
  })

  describe('getStorage', () => {
    it('gets localStorage item correctly', () => {
      const storedData = JSON.stringify({ value: 'test-value' })
      vi.spyOn(localStorageMock, 'getItem').mockReturnValue(storedData)
      
      const result = getStorage('test-key')
      
      expect(localStorageMock.getItem).toHaveBeenCalledWith('test-key')
      expect(result).toBe('test-value')
    })

    it('gets sessionStorage item correctly', () => {
      const storedData = JSON.stringify({ value: 'test-value' })
      vi.spyOn(sessionStorageMock, 'getItem').mockReturnValue(storedData)
      
      const result = getStorage('test-key', 'session')
      
      expect(sessionStorageMock.getItem).toHaveBeenCalledWith('test-key')
      expect(result).toBe('test-value')
    })

    it('returns null for non-existent key', () => {
      vi.spyOn(localStorageMock, 'getItem').mockReturnValue(null)
      
      const result = getStorage('non-existent-key')
      
      expect(result).toBeNull()
    })

    it('returns expired item and removes it', () => {
      const now = Date.now()
      vi.spyOn(Date, 'now').mockReturnValue(now + 1000)
      
      const expiredData = JSON.stringify({ value: 'test-value', expire: now - 100 })
      vi.spyOn(localStorageMock, 'getItem').mockReturnValue(expiredData)
      const removeItemSpy = vi.spyOn(localStorageMock, 'removeItem')
      
      const result = getStorage('expired-key')
      
      expect(result).toBeNull()
      expect(removeItemSpy).toHaveBeenCalledWith('expired-key')
    })

    it('handles getItem error gracefully', () => {
      vi.spyOn(console, 'error').mockImplementation(() => {})
      vi.spyOn(localStorageMock, 'getItem').mockImplementation(() => {
        throw new Error('Storage error')
      })
      
      const result = getStorage('test-key')
      
      expect(result).toBeNull()
    })
  })

  describe('removeStorage', () => {
    it('removes localStorage item', () => {
      removeStorage('test-key')
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('test-key')
    })

    it('removes sessionStorage item', () => {
      removeStorage('test-key', 'session')
      expect(sessionStorageMock.removeItem).toHaveBeenCalledWith('test-key')
    })
  })

  describe('clearStorage', () => {
    it('clears localStorage', () => {
      clearStorage()
      expect(localStorageMock.clear).toHaveBeenCalled()
    })

    it('clears sessionStorage', () => {
      clearStorage('session')
      expect(sessionStorageMock.clear).toHaveBeenCalled()
    })
  })

  describe('getStorageWithDefault', () => {
    it('returns stored value when exists', () => {
      vi.spyOn(localStorageMock, 'getItem').mockReturnValue(JSON.stringify({ value: 'stored-value' }))
      
      const result = getStorageWithDefault('test-key', 'default-value')
      
      expect(result).toBe('stored-value')
    })

    it('returns default value when key does not exist', () => {
      vi.spyOn(localStorageMock, 'getItem').mockReturnValue(null)
      
      const result = getStorageWithDefault('test-key', 'default-value')
      
      expect(result).toBe('default-value')
    })

    it('returns default value for expired item', () => {
      const now = Date.now()
      vi.spyOn(Date, 'now').mockReturnValue(now + 1000)
      
      const expiredData = JSON.stringify({ value: 'expired-value', expire: now - 100 })
      vi.spyOn(localStorageMock, 'getItem').mockReturnValue(expiredData)
      
      const result = getStorageWithDefault('test-key', 'default-value')
      
      expect(result).toBe('default-value')
    })
  })

  describe('tokenStorage', () => {
    it('gets token', () => {
      vi.spyOn(localStorageMock, 'getItem').mockReturnValue(JSON.stringify({ value: 'test-token' }))
      
      const result = tokenStorage.get()
      
      expect(result).toBe('test-token')
    })

    it('sets token', () => {
      tokenStorage.set('new-token', 3600000)
      
      expect(localStorageMock.setItem).toHaveBeenCalledWith(
        'token',
        expect.stringContaining('new-token')
      )
    })

    it('removes token', () => {
      tokenStorage.remove()
      
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('token')
    })
  })

  describe('userStorage', () => {
    it('gets user info', () => {
      const userInfo = { name: 'Test User', id: 1 }
      vi.spyOn(localStorageMock, 'getItem').mockReturnValue(JSON.stringify({ value: userInfo }))
      
      const result = userStorage.get()
      
      expect(result).toEqual(userInfo)
    })

    it('sets user info', () => {
      const userInfo = { name: 'Test User', id: 1 }
      userStorage.set(userInfo)
      
      expect(localStorageMock.setItem).toHaveBeenCalledWith(
        'userInfo',
        JSON.stringify({ value: userInfo })
      )
    })

    it('removes user info', () => {
      userStorage.remove()
      
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('userInfo')
    })
  })

  describe('cartStorage', () => {
    it('gets cart', () => {
      const cart = [{ id: 1, name: 'Drug 1' }]
      vi.spyOn(localStorageMock, 'getItem').mockReturnValue(JSON.stringify({ value: cart }))
      
      const result = cartStorage.get()
      
      expect(result).toEqual(cart)
    })

    it('sets cart', () => {
      const cart = [{ id: 1, name: 'Drug 1' }]
      cartStorage.set(cart)
      
      expect(localStorageMock.setItem).toHaveBeenCalledWith(
        'cart',
        JSON.stringify({ value: cart })
      )
    })

    it('removes cart', () => {
      cartStorage.remove()
      
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('cart')
    })
  })
})
