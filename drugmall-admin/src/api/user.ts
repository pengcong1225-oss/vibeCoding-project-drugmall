import { request } from '@/utils/request'
import type { LoginParams, LoginResult, UserInfo, UserListParams, UserListResult } from '@/types/user'

// 用户登录
export const login = (data: LoginParams): Promise<LoginResult> => {
  // 模拟登录，实际项目中应该调用后端API
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        token: 'mock_token_' + Date.now(),
        userInfo: {
          id: '1',
          username: data.username,
          nickname: '管理员',
          avatar: '',
          email: 'admin@drugmall.com',
          phone: '13800138000',
          status: 1,
          roles: ['admin'],
          permissions: ['*'],
          createTime: '2024-01-01 00:00:00',
          lastLoginTime: new Date().toISOString()
        },
        permissions: ['*'],
        roles: ['admin']
      })
    }, 500)
  })
}

// 获取用户信息
export const getUserInfo = (): Promise<UserInfo> => {
  // 模拟获取用户信息
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        id: '1',
        username: 'admin',
        nickname: '管理员',
        avatar: '',
        email: 'admin@drugmall.com',
        phone: '13800138000',
        status: 1,
        roles: ['admin'],
        permissions: ['*'],
        createTime: '2024-01-01 00:00:00',
        lastLoginTime: new Date().toISOString()
      })
    }, 300)
  })
}

// 获取用户列表
export const getUserList = (params: UserListParams): Promise<UserListResult> => {
  return request.get('/admin/users', params)
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
