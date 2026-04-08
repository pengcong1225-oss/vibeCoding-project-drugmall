// 药品相关类型定义

// 药品基础信息
export interface Product {
  id: string
  productCode: string
  productName: string
  categoryId: string
  categoryName?: string
  brandId: string
  brandName?: string
  mainImage: string
  images?: string[]
  detail: string
  price: number
  originalPrice?: number
  stock: number
  warningStock: number
  isRx: number // 0-非处方 1-处方
  approvalNumber: string
  manufacturer: string
  spec: string
  unit: string
  expiryDate?: string
  status: number // 0-下架 1-上架
  sortOrder: number
  salesCount?: number
  createTime: string
  updateTime: string
}

// 药品查询参数
export interface ProductQueryParams {
  pageNum: number
  pageSize: number
  keyword?: string
  categoryId?: string
  brandId?: string
  isRx?: number
  status?: number
  minPrice?: number
  maxPrice?: number
  startTime?: string
  endTime?: string
}

// 药品列表响应
export interface ProductListResult {
  list: Product[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}

// 药品表单数据（创建/编辑）
export interface ProductFormData {
  id?: string
  productCode?: string
  productName: string
  categoryId: string
  brandId?: string
  mainImage: string
  images: string[]
  detail: string
  price: number
  originalPrice?: number
  stock: number
  warningStock: number
  isRx: number
  approvalNumber: string
  manufacturer: string
  spec: string
  unit: string
  expiryDate?: string
  status: number
  sortOrder: number
}

// 分类信息
export interface Category {
  id: string
  name: string
  parentId: string
  level: number
  sortOrder: number
  icon?: string
  status: number
  createTime: string
  children?: Category[]
}

// 品牌信息
export interface Brand {
  id: string
  name: string
  logo?: string
  description?: string
  sortOrder: number
  status: number
  createTime: string
}
