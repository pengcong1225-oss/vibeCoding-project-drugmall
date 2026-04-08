// 通用类型定义

// 分页请求参数
export interface PageParams {
  page?: number
  size?: number
  sort?: string
  order?: 'asc' | 'desc'
}

// 分页响应数据
export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  size: number
  pages: number
}

// 通用响应结构
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 键值对
export interface KeyValuePair {
  key: string
  value: string
  label?: string
}

// 字典项
export interface DictItem {
  code: string
  name: string
  value: string | number
  sort?: number
  remark?: string
}

// 文件信息
export interface FileInfo {
  id: string
  name: string
  url: string
  type: string
  size: number
  createTime: string
}

// 地址信息
export interface AddressInfo {
  id: string
  name: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: boolean
  tag?: string
}

// 列表查询参数
export interface ListQueryParams {
  keyword?: string
  categoryId?: string
  startTime?: string
  endTime?: string
  status?: number | string
  [key: string]: unknown
}
