import { describe, it, expect } from 'vitest'
import {
  formatPrice,
  formatPriceWithSymbol,
  formatPhone,
  formatIdCard,
  formatDate,
  formatDateTime,
  formatRelativeTime,
  formatCount,
  formatFileSize,
  formatAddress
} from './format'
import {
  isValidPhone,
  isValidEmail,
  isValidIdCard,
  getPasswordStrength,
  isEmpty,
  isValidUrl,
  isNumeric,
  isInteger,
  isPositiveInteger,
  isValidAmount,
  isChinese,
  isValidPlate,
  isValidPostalCode
} from './validate'
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

// Format Tests
describe('Format Utils', () => {
  describe('formatPrice', () => {
    it('formats price correctly', () => {
      expect(formatPrice(28.5)).toBe('28.50')
      expect(formatPrice(100)).toBe('100.00')
      expect(formatPrice(1234.5)).toBe('1234.50')
    })

    it('handles fen to yuan conversion', () => {
      expect(formatPrice(2850, true)).toBe('28.50')
      expect(formatPrice(10000, true)).toBe('100.00')
    })

    it('handles string input', () => {
      expect(formatPrice('28.5')).toBe('28.50')
      expect(formatPrice('100')).toBe('100.00')
    })

    it('handles undefined and null', () => {
      expect(formatPrice(undefined)).toBe('0.00')
      expect(formatPrice(null)).toBe('0.00')
    })

    it('handles NaN', () => {
      expect(formatPrice(NaN)).toBe('0.00')
      expect(formatPrice('not-a-number')).toBe('0.00')
    })

    it('respects custom decimals', () => {
      expect(formatPrice(28.555, false, 0)).toBe('29')
      expect(formatPrice(28.555, false, 1)).toBe('28.6')
      expect(formatPrice(28.555, false, 3)).toBe('28.555')
    })
  })

  describe('formatPriceWithSymbol', () => {
    it('formats price with symbol', () => {
      expect(formatPriceWithSymbol(28.5)).toBe('¥28.50')
      expect(formatPriceWithSymbol(100)).toBe('¥100.00')
    })

    it('handles fen conversion with symbol', () => {
      expect(formatPriceWithSymbol(2850, true)).toBe('¥28.50')
    })
  })

  describe('formatPhone', () => {
    it('formats phone number correctly', () => {
      expect(formatPhone('13800138000')).toBe('138****8000')
    })

    it('returns original for invalid phone', () => {
      expect(formatPhone('123')).toBe('123')
      expect(formatPhone('')).toBe('')
    })
  })

  describe('formatIdCard', () => {
    it('formats 18-digit ID card', () => {
      expect(formatIdCard('110101199001011234')).toBe('1101**********1234')
    })

    it('formats 15-digit ID card', () => {
      expect(formatIdCard('110101900101123')).toBe('1101********123')
    })

    it('returns empty for empty input', () => {
      expect(formatIdCard('')).toBe('')
    })

    it('returns original for invalid ID card', () => {
      expect(formatIdCard('123456')).toBe('123456')
    })
  })

  describe('formatDate', () => {
    it('formats date correctly', () => {
      expect(formatDate(new Date(2024, 0, 15))).toBe('2024-01-15')
      expect(formatDate('2024-01-15')).toBe('2024-01-15')
    })

    it('formats with custom format', () => {
      expect(formatDate(new Date(2024, 0, 15), 'YYYY/MM/DD')).toBe('2024/01/15')
      expect(formatDate(new Date(2024, 0, 15, 14, 30), 'HH:mm')).toBe('14:30')
    })

    it('returns empty for invalid date', () => {
      expect(formatDate('invalid')).toBe('')
      expect(formatDate('')).toBe('')
    })
  })

  describe('formatDateTime', () => {
    it('formats date time correctly', () => {
      const date = new Date(2024, 0, 15, 14, 30, 0)
      expect(formatDateTime(date)).toBe('2024-01-15 14:30:00')
    })
  })

  describe('formatRelativeTime', () => {
    it('formats just now', () => {
      const now = new Date()
      expect(formatRelativeTime(now)).toBe('刚刚')
    })

    it('formats minutes ago', () => {
      const date = new Date(Date.now() - 5 * 60 * 1000)
      expect(formatRelativeTime(date)).toBe('5分钟前')
    })

    it('formats hours ago', () => {
      const date = new Date(Date.now() - 2 * 60 * 60 * 1000)
      expect(formatRelativeTime(date)).toBe('2小时前')
    })

    it('formats days ago', () => {
      const date = new Date(Date.now() - 3 * 24 * 60 * 60 * 1000)
      expect(formatRelativeTime(date)).toBe('3天前')
    })

    it('returns empty for empty input', () => {
      expect(formatRelativeTime('')).toBe('')
    })
  })

  describe('formatCount', () => {
    it('formats count less than 1000', () => {
      expect(formatCount(0)).toBe('0')
      expect(formatCount(999)).toBe('999')
    })

    it('formats count in thousands', () => {
      expect(formatCount(1000)).toBe('1.0k')
      expect(formatCount(1500)).toBe('1.5k')
      expect(formatCount(9999)).toBe('10.0k')
    })

    it('formats count in millions', () => {
      expect(formatCount(1000000)).toBe('1.0M')
      expect(formatCount(2500000)).toBe('2.5M')
    })
  })

  describe('formatFileSize', () => {
    it('formats bytes', () => {
      expect(formatFileSize(0)).toBe('0 Bytes')
      expect(formatFileSize(512)).toBe('512 Bytes')
    })

    it('formats KB', () => {
      expect(formatFileSize(1024)).toBe('1 KB')
      expect(formatFileSize(1536)).toBe('1.5 KB')
    })

    it('formats MB', () => {
      expect(formatFileSize(1024 * 1024)).toBe('1 MB')
      expect(formatFileSize(2.5 * 1024 * 1024)).toBe('2.5 MB')
    })

    it('formats GB', () => {
      expect(formatFileSize(1024 * 1024 * 1024)).toBe('1 GB')
    })
  })

  describe('formatAddress', () => {
    it('formats full address', () => {
      expect(formatAddress('北京市', '北京市', '朝阳区', '建国路88号')).toBe('北京市北京市朝阳区建国路88号')
    })

    it('handles empty parts', () => {
      expect(formatAddress('北京市', '', '朝阳区', '建国路88号')).toBe('北京市朝阳区建国路88号')
      expect(formatAddress('', '', '', '建国路88号')).toBe('建国路88号')
    })
  })
})
