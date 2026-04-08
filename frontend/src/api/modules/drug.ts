import request, { http } from '../request'
import type { Drug, DrugCategory, DrugSearchParams, DrugListResult, DrugDetail, DrugReview, DrugFAQ } from '@/types'

// 获取药品列表
export function getDrugList(params: DrugSearchParams) {
  return http.get<DrugListResult>('/drugs', params)
}

// 获取药品详情
export function getDrugDetail(id: string) {
  return http.get<DrugDetail>(`/drugs/${id}`)
}

// 获取药品分类列表
export function getDrugCategories() {
  return http.get<DrugCategory[]>('/drugs/categories')
}

// 获取热门药品
export function getHotDrugs(limit: number = 10) {
  return http.get<Drug[]>('/drugs/hot', { limit })
}

// 获取新品药品
export function getNewDrugs(limit: number = 10) {
  return http.get<Drug[]>('/drugs/new', { limit })
}

// 获取推荐药品
export function getRecommendedDrugs(drugId?: string, limit: number = 6) {
  return http.get<Drug[]>('/drugs/recommended', { drugId, limit })
}

// 获取相关药品
export function getRelatedDrugs(drugId: string, limit: number = 6) {
  return http.get<Drug[]>('/drugs/related', { drugId, limit })
}

// 获取药品评价列表
export function getDrugReviews(drugId: string, params?: { page?: number; size?: number }) {
  return http.get<{ list: DrugReview[]; total: number }>(`/drugs/${drugId}/reviews`, params)
}

// 获取药品FAQ
export function getDrugFAQs(drugId: string) {
  return http.get<DrugFAQ[]>(`/drugs/${drugId}/faqs`)
}

// 搜索建议
export function getSearchSuggestions(keyword: string) {
  return http.get<string[]>('/drugs/search/suggestions', { keyword })
}

// 热门搜索
export function getHotSearches(limit: number = 10) {
  return http.get<{ keyword: string; heat: number }[]>('/drugs/search/hot', { limit })
}

// 浏览历史
export function getBrowseHistory(page?: number, size?: number) {
  return http.get<{ list: Drug[]; total: number }>('/user/browse-history', { page, size })
}

// 添加到浏览历史
export function addBrowseHistory(drugId: string) {
  return http.post('/user/browse-history', { drugId })
}

// 清空浏览历史
export function clearBrowseHistory() {
  return http.delete('/user/browse-history')
}
