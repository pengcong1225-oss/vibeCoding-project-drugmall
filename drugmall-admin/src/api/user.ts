import { request } from '@/utils/request'
import type { LoginParams, LoginResult, UserInfo, UserListParams, UserListResult } from '@/types/user'
import type { Order } from '@/types/order'

interface OrderListResult {
  list: Order[]
  total: number
}

// 用户登录
export const login = (data: LoginParams): Promise<LoginResult> => {
  return request.post('/admin/auth/login', data)
}

// 获取用户信息
export const getUserInfo = (): Promise<UserInfo> => {
  return request.get('/admin/auth/userinfo')
}

// 登出
export const logout = (): Promise<void> => {
  return request.post('/admin/auth/logout')
}

// 获取用户列表
export const getUserList = (params: UserListParams): Promise<UserListResult> => {
  return request.get('/admin/users', params)
}

// 获取用户详情
export const getUserDetail = (id: string): Promise<UserInfo & { orderCount: number; totalSpent: number; couponCount: number }> => {
  return request.get(`/admin/users/${id}`)
}

// 创建用户
export const createUser = (data: Partial<UserInfo>): Promise<void> => {
  return request.post('/admin/users', data)
}

// 更新用户
export const updateUser = (id: string, data: Partial<UserInfo>): Promise<void> => {
  return request.put(`/admin/users/${id}`, data)
}

// 删除用户
export const deleteUser = (id: string): Promise<void> => {
  return request.delete(`/admin/users/${id}`)
}

// 修改用户状态
export const updateUserStatus = (id: string, status: number): Promise<void> => {
  return request.patch(`/admin/users/${id}/status`, { status })
}

// 获取用户订单列表
export const getUserOrders = (id: string, params: { pageNum: number; pageSize: number }): Promise<OrderListResult> => {
  return request.get(`/admin/users/${id}/orders`, params)
}

// 获取实名认证列表
export const getUserAuthList = (params: { pageNum: number; pageSize: number; keyword?: string; status?: number }): Promise<any> => {
  return request.get('/admin/users/auth/list', params)
}

// 获取认证统计
export const getUserAuthStats = (): Promise<{ pending: number; passed: number; rejected: number; total: number }> => {
  return request.get('/admin/users/auth/stats')
}

// 审核认证
export const auditUserAuth = (id: string, data: { result: string; reason?: string }): Promise<void> => {
  return request.post(`/admin/users/auth/${id}/audit`, data)
}
