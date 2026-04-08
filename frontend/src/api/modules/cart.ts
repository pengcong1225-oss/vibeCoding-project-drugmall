import request, { http } from '../request'
import type { CartItem, CartStats, AddToCartParams, UpdateCartParams, CartValidationResult, CartCheckoutInfo } from '@/types'

// 获取购物车列表
export function getCartList() {
  return http.get<CartItem[]>('/cart')
}

// 添加商品到购物车
export function addToCart(data: AddToCartParams) {
  return http.post<CartItem>('/cart', data)
}

// 更新购物车商品
export function updateCartItem(itemId: string, data: Partial<UpdateCartParams>) {
  return http.put<CartItem>(`/cart/${itemId}`, data)
}

// 删除购物车商品
export function removeCartItem(itemId: string) {
  return http.delete(`/cart/${itemId}`)
}

// 批量删除购物车商品
export function batchRemoveCartItems(itemIds: string[]) {
  return http.post('/cart/batch-remove', { itemIds })
}

// 清空购物车
export function clearCart() {
  return http.delete('/cart')
}

// 获取购物车统计
export function getCartStats() {
  return http.get<CartStats>('/cart/stats')
}

// 选择/取消选择购物车商品
export function selectCartItem(itemId: string, selected: boolean) {
  return http.put(`/cart/${itemId}/select`, { selected })
}

// 全选/取消全选
export function selectAllCartItems(selected: boolean) {
  return http.put('/cart/select-all', { selected })
}

// 更新购物车商品数量
export function updateCartItemQuantity(itemId: string, quantity: number) {
  return http.put(`/cart/${itemId}/quantity`, { quantity })
}

// 验证购物车
export function validateCart() {
  return http.get<CartValidationResult>('/cart/validate')
}

// 获取购物车结算信息
export function getCheckoutInfo(cartItemIds?: string[]) {
  return http.get<CartCheckoutInfo>('/cart/checkout', { cartItemIds })
}

// 合并购物车（登录后）
export function mergeCart(localCartItems: CartItem[]) {
  return http.post<CartItem[]>('/cart/merge', { items: localCartItems })
}
