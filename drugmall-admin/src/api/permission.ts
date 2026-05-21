import { request } from '@/utils/request'
import type { Role, Permission, AdminUser, RoleQuery } from '@/types/permission'

export function getRoleList(params: RoleQuery) {
  return request.get<{ list: Role[], total: number }>('/admin/roles', params)
}

export function createRole(data: Partial<Role>) {
  return request.post('/admin/roles', data)
}

export function updateRole(id: string, data: Partial<Role>) {
  return request.put(`/admin/roles/${id}`, data)
}

export function deleteRole(id: string) {
  return request.delete(`/admin/roles/${id}`)
}

export function getPermissionTree() {
  return request.get<Permission[]>('/admin/permissions/tree')
}

export function getAdminUserList(params: RoleQuery) {
  return request.get<{ list: AdminUser[], total: number }>('/admin/users/admins', params)
}

export function assignUserRoles(userId: string, roleIds: string[]) {
  return request.put(`/admin/users/${userId}/roles`, { roleIds })
}
