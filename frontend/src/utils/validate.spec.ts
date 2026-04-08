import { describe, it, expect } from 'vitest'
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

describe('Validate Utils', () => {
  describe('isValidPhone', () => {
    it('returns true for valid Chinese mobile phone numbers', () => {
      expect(isValidPhone('13800138000')).toBe(true)
      expect(isValidPhone('13912345678')).toBe(true)
      expect(isValidPhone('15012345678')).toBe(true)
      expect(isValidPhone('18812345678')).toBe(true)
    })

    it('returns false for invalid phone numbers', () => {
      expect(isValidPhone('1380013800')).toBe(false)
      expect(isValidPhone('138001380000')).toBe(false)
      expect(isValidPhone('23800138000')).toBe(false)
      expect(isValidPhone('1380013800a')).toBe(false)
      expect(isValidPhone('')).toBe(false)
    })
  })

  describe('isValidEmail', () => {
    it('returns true for valid email addresses', () => {
      expect(isValidEmail('test@example.com')).toBe(true)
      expect(isValidEmail('user.name@domain.com')).toBe(true)
      expect(isValidEmail('user+tag@example.co.uk')).toBe(true)
      expect(isValidEmail('123@example.com')).toBe(true)
    })

    it('returns false for invalid email addresses', () => {
      expect(isValidEmail('test@')).toBe(false)
      expect(isValidEmail('@example.com')).toBe(false)
      expect(isValidEmail('test@example')).toBe(false)
      expect(isValidEmail('test example.com')).toBe(false)
      expect(isValidEmail('')).toBe(false)
    })
  })

  describe('isValidIdCard', () => {
    it('returns true for valid 18-digit ID cards', () => {
      expect(isValidIdCard('110101199001011234')).toBe(true)
      expect(isValidIdCard('31010119900101123X')).toBe(true)
      expect(isValidIdCard('440106199001011234')).toBe(true)
    })

    it('returns true for valid 15-digit ID cards', () => {
      expect(isValidIdCard('110101900101123')).toBe(true)
      expect(isValidIdCard('310101900101123')).toBe(true)
    })

    it('returns false for invalid ID cards', () => {
      expect(isValidIdCard('11010119900101123')).toBe(false)
      expect(isValidIdCard('1101011990010112345')).toBe(false)
      expect(isValidIdCard('11010190010112')).toBe(false)
      expect(isValidIdCard('')).toBe(false)
      expect(isValidIdCard('abcdefghijklmnopqr')).toBe(false)
    })
  })

  describe('getPasswordStrength', () => {
    it('returns 0 for passwords shorter than 6 characters', () => {
      expect(getPasswordStrength('12345')).toBe(0)
      expect(getPasswordStrength('abc')).toBe(0)
      expect(getPasswordStrength('')).toBe(0)
    })

    it('returns 1 for weak passwords (1 criteria)', () => {
      expect(getPasswordStrength('123456')).toBe(1)
      expect(getPasswordStrength('abcdef')).toBe(1)
      expect(getPasswordStrength('ABCDEF')).toBe(1)
    })

    it('returns 2 for medium passwords (2-3 criteria)', () => {
      expect(getPasswordStrength('abc123')).toBe(2)
      expect(getPasswordStrength('Abc123')).toBe(3)
    })

    it('returns 3 for strong passwords (4-5 criteria)', () => {
      expect(getPasswordStrength('Abc123!')).toBe(3)
      expect(getPasswordStrength('Abc123!@#')).toBe(3)
    })
  })

  describe('isEmpty', () => {
    it('returns true for null and undefined', () => {
      expect(isEmpty(null)).toBe(true)
      expect(isEmpty(undefined)).toBe(true)
    })

    it('returns true for empty string', () => {
      expect(isEmpty('')).toBe(true)
      expect(isEmpty('   ')).toBe(true)
    })

    it('returns true for empty array', () => {
      expect(isEmpty([])).toBe(true)
    })

    it('returns true for empty object', () => {
      expect(isEmpty({})).toBe(true)
    })

    it('returns false for non-empty values', () => {
      expect(isEmpty('hello')).toBe(false)
      expect(isEmpty([1, 2, 3])).toBe(false)
      expect(isEmpty({ a: 1 })).toBe(false)
      expect(isEmpty(0)).toBe(false)
      expect(isEmpty(false)).toBe(false)
    })
  })

  describe('isValidUrl', () => {
    it('returns true for valid URLs', () => {
      expect(isValidUrl('https://example.com')).toBe(true)
      expect(isValidUrl('http://localhost:3000')).toBe(true)
      expect(isValidUrl('ftp://files.example.com')).toBe(true)
    })

    it('returns false for invalid URLs', () => {
      expect(isValidUrl('not-a-url')).toBe(false)
      expect(isValidUrl('http://')).toBe(false)
      expect(isValidUrl('')).toBe(false)
    })
  })

  describe('isNumeric', () => {
    it('returns true for numeric values', () => {
      expect(isNumeric(123)).toBe(true)
      expect(isNumeric('123')).toBe(true)
      expect(isNumeric(3.14)).toBe(true)
      expect(isNumeric('3.14')).toBe(true)
      expect(isNumeric(-5)).toBe(true)
    })

    it('returns false for non-numeric values', () => {
      expect(isNumeric('abc')).toBe(false)
      expect(isNumeric('')).toBe(false)
      expect(isNumeric(null)).toBe(false)
      expect(isNumeric(undefined)).toBe(false)
      expect(isNumeric(NaN)).toBe(false)
    })
  })

  describe('isInteger', () => {
    it('returns true for integers', () => {
      expect(isInteger(123)).toBe(true)
      expect(isInteger('123')).toBe(true)
      expect(isInteger(-456)).toBe(true)
      expect(isInteger(0)).toBe(true)
    })

    it('returns false for non-integers', () => {
      expect(isInteger(3.14)).toBe(false)
      expect(isInteger('3.14')).toBe(false)
      expect(isInteger('abc')).toBe(false)
    })
  })

  describe('isPositiveInteger', () => {
    it('returns true for positive integers', () => {
      expect(isPositiveInteger(1)).toBe(true)
      expect(isPositiveInteger(100)).toBe(true)
      expect(isPositiveInteger('50')).toBe(true)
    })

    it('returns false for non-positive integers', () => {
      expect(isPositiveInteger(0)).toBe(false)
      expect(isPositiveInteger(-5)).toBe(false)
      expect(isPositiveInteger(3.14)).toBe(false)
    })
  })

  describe('isValidAmount', () => {
    it('returns true for valid amounts', () => {
      expect(isValidAmount('100')).toBe(true)
      expect(isValidAmount('100.50')).toBe(true)
      expect(isValidAmount('0.99')).toBe(true)
      expect(isValidAmount(100.5)).toBe(true)
    })

    it('returns false for invalid amounts', () => {
      expect(isValidAmount('100.555')).toBe(false)
      expect(isValidAmount('-100')).toBe(false)
      expect(isValidAmount('abc')).toBe(false)
      expect(isValidAmount('')).toBe(false)
    })
  })

  describe('isChinese', () => {
    it('returns true for Chinese characters', () => {
      expect(isChinese('中文')).toBe(true)
      expect(isChinese('你好世界')).toBe(true)
    })

    it('returns false for non-Chinese characters', () => {
      expect(isChinese('hello')).toBe(false)
      expect(isChinese('123')).toBe(false)
      expect(isChinese('hello中文')).toBe(false)
    })
  })

  describe('isValidPlate', () => {
    it('returns true for valid license plates', () => {
      expect(isValidPlate('京A12345')).toBe(true)
      expect(isValidPlate('沪B12345')).toBe(true)
      expect(isValidPlate('粤A12345')).toBe(true)
    })

    it('returns false for invalid license plates', () => {
      expect(isValidPlate('A12345')).toBe(false)
      expect(isValidPlate('京12345')).toBe(false)
      expect(isValidPlate('京A1234')).toBe(false)
      expect(isValidPlate('')).toBe(false)
    })
  })

  describe('isValidPostalCode', () => {
    it('returns true for valid postal codes', () => {
      expect(isValidPostalCode('100000')).toBe(true)
      expect(isValidPostalCode('200000')).toBe(true)
      expect(isValidPostalCode('518000')).toBe(true)
    })

    it('returns false for invalid postal codes', () => {
      expect(isValidPostalCode('10000')).toBe(false)
      expect(isValidPostalCode('1000000')).toBe(false)
      expect(isValidPostalCode('abcdef')).toBe(false)
      expect(isValidPostalCode('')).toBe(false)
    })
  })
})
