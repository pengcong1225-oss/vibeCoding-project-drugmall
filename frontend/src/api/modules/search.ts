import request, { http } from '../request'
import type { Drug, DrugSearchParams, DrugListResult, SearchSuggestion, HotSearch } from '@/types'

// 搜索商品
export function searchDrugs(params: DrugSearchParams) {
  return http.get<DrugListResult>('/search/drugs', params)
}

// 获取搜索建议
export function getSearchSuggestions(keyword: string) {
  return http.get<SearchSuggestion[]>('/search/suggestions', { keyword })
}

// 获取热门搜索
export function getHotSearches(limit: number = 10) {
  return http.get<HotSearch[]>('/search/hot', { limit })
}

// 获取搜索历史
export function getSearchHistory() {
  return http.get<string[]>('/search/history')
}

// 添加搜索历史
export function addSearchHistory(keyword: string) {
  return http.post('/search/history', { keyword })
}

// 删除搜索历史
export function deleteSearchHistory(keyword: string) {
  return http.delete('/search/history', { keyword })
}

// 清空搜索历史
export function clearSearchHistory() {
  return http.delete('/search/history/all')
}

// 获取筛选条件
export function getSearchFilters() {
  return http.get<{
    categories: { id: string; name: string }[]
    brands: { id: string; name: string }[]
    priceRanges: { min: number; max: number; label: string }[]
  }>('/search/filters')
}
