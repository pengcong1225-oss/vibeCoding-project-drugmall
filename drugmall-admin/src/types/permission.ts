export interface Role {
  id: string
  name: string
  code: string
  description: string
  permissions: string[]
  userCount: number
  status: 'active' | 'disabled'
  createTime: string
  updateTime: string
}

export interface Permission {
  id: string
  name: string
  code: string
  type: 'menu' | 'button' | 'api'
  parentId: string
  path?: string
  icon?: string
  sort: number
  children?: Permission[]
}

export interface AdminUser {
  id: string
  username: string
  nickname: string
  email: string
  phone: string
  roles: string[]
  status: 'active' | 'disabled'
  lastLoginTime?: string
  createTime: string
}

export interface RoleQuery {
  pageNum: number
  pageSize: number
  name?: string
  status?: string
}
