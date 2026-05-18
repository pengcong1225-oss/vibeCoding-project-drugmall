// 用户相关类型定义

// 用户信息
export interface UserInfo {
  id: string
  phone: string
  nickname: string
  avatar: string
  isAuthenticated: boolean
  email?: string
  birthday?: string
  gender?: number  // 1=男, 2=女, 0=未知
  realName?: string
  idCard?: string
}

// 登录参数
export interface LoginParams {
  phone: string
  code: string
}

// 登录响应
export interface LoginResult {
  token: string
  userInfo: UserInfo
  expiresIn: number
}

// 患者信息（就诊人）
export interface Patient {
  id: string
  name: string
  gender: number  // 1=男, 2=女
  age: number
  idCard: string
  phone: string
  relationship: string
  isDefault: boolean
  birthday?: string
  address?: string
  allergyHistory?: string
  medicalHistory?: string
}

// 添加患者参数
export interface AddPatientParams {
  name: string
  gender: number  // 1=男, 2=女
  age: number
  idCard: string
  phone: string
  relationship: string
  isDefault?: boolean
  birthday?: string
  address?: string
  allergyHistory?: string
  medicalHistory?: string
}

// 修改患者参数
export interface UpdatePatientParams extends Partial<AddPatientParams> {
  id: string
}

// 用户收货地址
export interface UserAddress {
  id: string
  name: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: boolean
  tag?: string
  postalCode?: string
}

// 添加地址参数
export interface AddAddressParams {
  name: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault?: boolean
  tag?: string
  postalCode?: string
}

// 用户优惠券
export interface UserCoupon {
  id: string
  name: string
  type: 'full_reduction' | 'discount' | 'cash'
  value: number
  minAmount: number
  startTime: string
  endTime: string
  status: 'unused' | 'used' | 'expired'
  description?: string
  scope?: string
}

// 用户订单统计
export interface UserOrderStats {
  pendingPayment: number
  pendingShipment: number
  pendingReceipt: number
  pendingReview: number
  afterSale: number
}

// 用户浏览历史
export interface UserBrowseHistory {
  id: string
  drugId: string
  name: string
  image: string
  price: number
  browseTime: string
}
