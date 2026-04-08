// 格式化工具函数

/**
 * 格式化价格
 * @param price 价格（分或元）
 * @param isFen 是否为分
 * @param decimals 小数位数
 * @returns 格式化后的价格字符串
 */
export function formatPrice(price: number | string, isFen = false, decimals = 2): string {
  if (price === undefined || price === null) return '0.00'
  
  let num = typeof price === 'string' ? parseFloat(price) : price
  
  if (isNaN(num)) return '0.00'
  
  // 如果是分，转换为元
  if (isFen) {
    num = num / 100
  }
  
  return num.toFixed(decimals)
}

/**
 * 格式化价格（带¥符号）
 * @param price 价格
 * @param isFen 是否为分
 * @returns 格式化后的价格字符串
 */
export function formatPriceWithSymbol(price: number | string, isFen = false): string {
  return `¥${formatPrice(price, isFen)}`
}

/**
 * 格式化手机号（隐藏中间4位）
 * @param phone 手机号
 * @returns 格式化后的手机号
 */
export function formatPhone(phone: string): string {
  if (!phone || phone.length !== 11) return phone
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * 格式化身份证号（隐藏中间8位）
 * @param idCard 身份证号
 * @returns 格式化后的身份证号
 */
export function formatIdCard(idCard: string): string {
  if (!idCard) return ''
  if (idCard.length === 18) {
    return idCard.replace(/(\d{4})\d{10}(\w{4})/, '$1**********$2')
  }
  if (idCard.length === 15) {
    return idCard.replace(/(\d{4})\d{8}(\w{3})/, '$1********$2')
  }
  return idCard
}

/**
 * 格式化日期
 * @param date 日期
 * @param format 格式
 * @returns 格式化后的日期字符串
 */
export function formatDate(date: Date | string | number, format = 'YYYY-MM-DD'): string {
  if (!date) return ''
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  const second = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
    .replace('ss', second)
}

/**
 * 格式化日期时间
 * @param date 日期
 * @returns 格式化后的日期时间字符串
 */
export function formatDateTime(date: Date | string | number): string {
  return formatDate(date, 'YYYY-MM-DD HH:mm:ss')
}

/**
 * 格式化相对时间
 * @param date 日期
 * @returns 相对时间字符串
 */
export function formatRelativeTime(date: Date | string | number): string {
  if (!date) return ''
  
  const d = new Date(date)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  const month = 30 * day
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < week) {
    return `${Math.floor(diff / day)}天前`
  } else if (diff < month) {
    return `${Math.floor(diff / week)}周前`
  } else {
    return formatDate(date, 'YYYY-MM-DD')
  }
}

/**
 * 格式化数量（超过1000显示为1k）
 * @param count 数量
 * @returns 格式化后的数量字符串
 */
export function formatCount(count: number): string {
  if (count >= 1000000) {
    return (count / 1000000).toFixed(1) + 'M'
  }
  if (count >= 1000) {
    return (count / 1000).toFixed(1) + 'k'
  }
  return String(count)
}

/**
 * 格式化文件大小
 * @param bytes 字节数
 * @param decimals 小数位数
 * @returns 格式化后的文件大小字符串
 */
export function formatFileSize(bytes: number, decimals = 2): string {
  if (bytes === 0) return '0 Bytes'
  
  const k = 1024
  const dm = decimals < 0 ? 0 : decimals
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
  
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i]
}

/**
 * 格式化地址（省市区+详细地址）
 * @param province 省
 * @param city 市
 * @param district 区
 * @param detail 详细地址
 * @returns 完整地址字符串
 */
export function formatAddress(province: string, city: string, district: string, detail: string): string {
  const parts = [province, city, district, detail].filter(Boolean)
  return parts.join('')
}
