import { request } from '@/utils/request'
import type { 
  Product, 
  ProductQueryParams, 
  ProductListResult, 
  ProductFormData,
  Category,
  Brand 
} from '@/types/product'

// 药品管理
export const getProductList = (params: ProductQueryParams): Promise<ProductListResult> => {
  return request.get('/admin/products', params)
}

export const getProductDetail = (id: string): Promise<Product> => {
  return request.get(`/admin/products/${id}`)
}

export const createProduct = (data: ProductFormData): Promise<string> => {
  return request.post('/admin/products', data)
}

export const updateProduct = (id: string, data: ProductFormData): Promise<void> => {
  return request.put(`/admin/products/${id}`, data)
}

export const deleteProduct = (id: string): Promise<void> => {
  return request.delete(`/admin/products/${id}`)
}

export const updateProductStatus = (id: string, status: number): Promise<void> => {
  return request.patch(`/admin/products/${id}/status`, { status })
}

// 分类管理
export const getCategoryList = (): Promise<Category[]> => {
  return request.get('/admin/categories')
}

export const createCategory = (data: Partial<Category>): Promise<string> => {
  return request.post('/admin/categories', data)
}

export const updateCategory = (id: string, data: Partial<Category>): Promise<void> => {
  return request.put(`/admin/categories/${id}`, data)
}

export const deleteCategory = (id: string): Promise<void> => {
  return request.delete(`/admin/categories/${id}`)
}

// 品牌管理
export const getBrandList = (params?: { pageNum?: number, pageSize?: number, keyword?: string }): Promise<{ list: Brand[], total: number }> => {
  return request.get('/admin/brands', params)
}

export const createBrand = (data: Partial<Brand>): Promise<string> => {
  return request.post('/admin/brands', data)
}

export const updateBrand = (id: string, data: Partial<Brand>): Promise<void> => {
  return request.put(`/admin/brands/${id}`, data)
}

export const deleteBrand = (id: string): Promise<void> => {
  return request.delete(`/admin/brands/${id}`)
}
