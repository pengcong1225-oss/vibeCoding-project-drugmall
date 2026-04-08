// 用户相关类型定义

// 登录参数
export interface LoginParams {
  username: string
  password: string
  captcha?: string
}

// 登录响应
export interface LoginResult {
  token: string
  userInfo: UserInfo
  permissions: string[]
  roles: string[]
}

// 用户信息
export interface UserInfo {
  id: string
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  status: number
  roles: string[]
  permissions: string[]
  createTime: string
  lastLoginTime: string
}

// 用户列表查询参数
export interface UserListParams {
  pageNum: number
  pageSize: number
  keyword?: string
  status?: number
  startTime?: string
  endTime?: string
}

// 用户列表响应
export interface UserListResult {
  list: UserInfo[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}
