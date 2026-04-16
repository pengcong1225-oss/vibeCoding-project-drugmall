import request, { http } from '../request'
import type { Drug, DrugSearchParams, DrugListResult, SearchSuggestion, HotSearch } from '@/types'

const SEARCH_HISTORY_KEY = 'search_history'
const MAX_HISTORY_ITEMS = 10

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

// 获取搜索历史（本地存储版本）
export function getSearchHistory(): string[] {
  try {
    const data = localStorage.getItem(SEARCH_HISTORY_KEY)
    return data ? JSON.parse(data) : []
  } catch {
    return []
  }
}

// 添加搜索历史（本地存储版本）
export function addSearchHistory(keyword: string): void {
  try {
    const history = getSearchHistory()
    // 移除重复项
    const filtered = history.filter(item => item !== keyword)
    // 添加到开头
    filtered.unshift(keyword)
    // 限制数量
    const limited = filtered.slice(0, MAX_HISTORY_ITEMS)
    localStorage.setItem(SEARCH_HISTORY_KEY, JSON.stringify(limited))
  } catch (error) {
    console.error('添加搜索历史失败:', error)
  }
}

// 删除搜索历史（本地存储版本）
export function deleteSearchHistory(keyword: string): void {
  try {
    const history = getSearchHistory()
    const filtered = history.filter(item => item !== keyword)
    localStorage.setItem(SEARCH_HISTORY_KEY, JSON.stringify(filtered))
  } catch (error) {
    console.error('删除搜索历史失败:', error)
  }
}

// 清空搜索历史（本地存储版本）
export function clearSearchHistory(): void {
  try {
    localStorage.removeItem(SEARCH_HISTORY_KEY)
  } catch (error) {
    console.error('清空搜索历史失败:', error)
  }
}

// 获取筛选条件
export function getSearchFilters() {
  return http.get<{
    categories: { id: string; name: string }[]
    brands: { id: string; name: string }[]
    priceRanges: { min: number; max: number; label: string }[]
  }>('/search/filters')
}
