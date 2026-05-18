import request, { http } from '../request'
import type { Drug, DrugSearchParams, DrugListResult, SearchSuggestion, HotSearch } from '@/types'

const SEARCH_HISTORY_KEY = 'search_history'
const MAX_HISTORY_ITEMS = 10

export function searchDrugs(params: DrugSearchParams) {
  return http.get<DrugListResult>('/search/drugs', params)
}

export function getSearchSuggestions(keyword: string) {
  return http.get<SearchSuggestion[]>('/search/suggestions', { keyword })
}

export function getHotSearches(limit: number = 10) {
  return http.get<HotSearch[]>('/search/hot', { limit })
}

export function getSearchFilters() {
  return http.get<{
    categories: { id: string; name: string }[]
    brands: { id: string; name: string }[]
    priceRanges: { min: number; max: number; label: string }[]
  }>('/search/filters')
}

export function getSearchHistoryFromServer(userId: number) {
  return http.get<string[]>('/search/history', { userId })
}

export function deleteSearchHistoryFromServer(userId: number, keyword: string) {
  return http.delete('/search/history', { userId, keyword })
}

export function clearSearchHistoryFromServer(userId: number) {
  return http.delete('/search/history/all', { userId })
}

export function getSearchHistory(): string[] {
  try {
    const data = localStorage.getItem(SEARCH_HISTORY_KEY)
    return data ? JSON.parse(data) : []
  } catch {
    return []
  }
}

export function addSearchHistory(keyword: string): void {
  try {
    const history = getSearchHistory()
    const filtered = history.filter(item => item !== keyword)
    filtered.unshift(keyword)
    const limited = filtered.slice(0, MAX_HISTORY_ITEMS)
    localStorage.setItem(SEARCH_HISTORY_KEY, JSON.stringify(limited))
  } catch (error) {
    console.error('添加搜索历史失败:', error)
  }
}

export function deleteSearchHistory(keyword: string): void {
  try {
    const history = getSearchHistory()
    const filtered = history.filter(item => item !== keyword)
    localStorage.setItem(SEARCH_HISTORY_KEY, JSON.stringify(filtered))
  } catch (error) {
    console.error('删除搜索历史失败:', error)
  }
}

export function clearSearchHistory(): void {
  try {
    localStorage.removeItem(SEARCH_HISTORY_KEY)
  } catch (error) {
    console.error('清空搜索历史失败:', error)
  }
}
