// 验证工具函数

/**
 * 验证手机号
 * @param phone 手机号
 * @returns 是否有效
 */
export function isValidPhone(phone: string): boolean {
  return /^1[3-9]\d{9}$/.test(phone)
}

/**
 * 验证邮箱
 * @param email 邮箱
 * @returns 是否有效
 */
export function isValidEmail(email: string): boolean {
  return /^[\w-+]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(email)
}

/**
 * 验证身份证号
 * @param idCard 身份证号
 * @returns 是否有效
 */
export function isValidIdCard(idCard: string): boolean {
  // 15位或18位身份证
  return /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)
}

/**
 * 验证密码强度
 * @param password 密码
 * @returns 强度等级 0-3（0: 弱, 1: 较弱, 2: 中等, 3: 强）
 */
export function getPasswordStrength(password: string): number {
  let strength = 0
  
  if (password.length < 6) return 0
  
  // 包含数字
  if (/\d/.test(password)) strength++
  // 包含小写字母
  if (/[a-z]/.test(password)) strength++
  // 包含大写字母
  if (/[A-Z]/.test(password)) strength++
  // 包含特殊字符
  if (/[^\da-zA-Z]/.test(password)) strength++
  
  return Math.min(strength, 3)
}

/**
 * 验证是否为空
 * @param value 值
 * @returns 是否为空
 */
export function isEmpty(value: unknown): boolean {
  if (value === null || value === undefined) return true
  if (typeof value === 'string') return value.trim() === ''
  if (Array.isArray(value)) return value.length === 0
  if (typeof value === 'object') return Object.keys(value).length === 0
  return false
}

/**
 * 验证是否为URL
 * @param url URL
 * @returns 是否有效
 */
export function isValidUrl(url: string): boolean {
  try {
    new URL(url)
    return true
  } catch {
    return false
  }
}

/**
 * 验证是否为数字
 * @param value 值
 * @returns 是否有效
 */
export function isNumeric(value: unknown): boolean {
  return !isNaN(parseFloat(String(value))) && isFinite(Number(value))
}

/**
 * 验证是否为整数
 * @param value 值
 * @returns 是否有效
 */
export function isInteger(value: unknown): boolean {
  return isNumeric(value) && Number.isInteger(Number(value))
}

/**
 * 验证是否为正整数
 * @param value 值
 * @returns 是否有效
 */
export function isPositiveInteger(value: unknown): boolean {
  return isInteger(value) && Number(value) > 0
}

/**
 * 验证是否为金额（最多两位小数）
 * @param value 值
 * @returns 是否有效
 */
export function isValidAmount(value: string | number): boolean {
  return /^\d+\.?\d{0,2}$/.test(String(value))
}

/**
 * 验证是否为中文
 * @param value 值
 * @returns 是否有效
 */
export function isChinese(value: string): boolean {
  return /^[\u4e00-\u9fa5]+$/.test(value)
}

/**
 * 验证是否为车牌号
 * @param plate 车牌号
 * @returns 是否有效
 */
export function isValidPlate(plate: string): boolean {
  // 新能源车牌 + 传统车牌
  return /^([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[DF])|([DF]([A-HJ-NP-Z0-9])[0-9]{4})))|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳])$/.test(plate)
}

/**
 * 验证是否为邮政编码
 * @param code 邮政编码
 * @returns 是否有效
 */
export function isValidPostalCode(code: string): boolean {
  return /^\d{6}$/.test(code)
}

/**
 * 验证表单
 * @param rules 验证规则
 * @param data 表单数据
 * @returns 验证结果
 */
export function validateForm(
  rules: Record<string, Array<{ required?: boolean; message: string; validator?: (value: unknown) => boolean }>>,
  data: Record<string, unknown>
): { valid: boolean; errors: Record<string, string> } {
  const errors: Record<string, string> = {}
  
  for (const [field, fieldRules] of Object.entries(rules)) {
    for (const rule of fieldRules) {
      const value = data[field]
      
      // 必填验证
      if (rule.required && isEmpty(value)) {
        errors[field] = rule.message
        break
      }
      
      // 自定义验证器
      if (rule.validator && !isEmpty(value) && !rule.validator(value)) {
        errors[field] = rule.message
        break
      }
    }
  }
  
  return {
    valid: Object.keys(errors).length === 0,
    errors
  }
}
