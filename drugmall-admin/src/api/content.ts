import { request } from '@/utils/request'
import type { Banner, Article, ArticleCategory, ArticleStats, Notice, NoticeStats } from '@/types/content'

// ========== Banner ==========
export const getBannerList = (): Promise<Banner[]> => {
  return request.get('/admin/content/banners')
}

export const createBanner = (data: Partial<Banner>): Promise<{ id: string }> => {
  return request.post('/admin/content/banners', data)
}

export const updateBanner = (id: string, data: Partial<Banner>): Promise<void> => {
  return request.put(`/admin/content/banners/${id}`, data)
}

export const deleteBanner = (id: string): Promise<void> => {
  return request.delete(`/admin/content/banners/${id}`)
}

export const updateBannerStatus = (id: string, status: number): Promise<void> => {
  return request.patch(`/admin/content/banners/${id}/status`, { status })
}

export const updateBannerSort = (data: { id: string; sort: number }[]): Promise<void> => {
  return request.put('/admin/content/banners/sort', data)
}

// ========== Article ==========
export const getArticleList = (params: { pageNum: number; pageSize: number; categoryId?: string; keyword?: string; status?: number }): Promise<{ list: Article[]; total: number; pageNum: number; pageSize: number; pages: number }> => {
  return request.get('/admin/content/articles', params)
}

export const getArticleDetail = (id: string): Promise<Article> => {
  return request.get(`/admin/content/articles/${id}`)
}

export const createArticle = (data: Partial<Article>): Promise<{ id: string }> => {
  return request.post('/admin/content/articles', data)
}

export const updateArticle = (id: string, data: Partial<Article>): Promise<void> => {
  return request.put(`/admin/content/articles/${id}`, data)
}

export const deleteArticle = (id: string): Promise<void> => {
  return request.delete(`/admin/content/articles/${id}`)
}

export const getArticleCategories = (): Promise<ArticleCategory[]> => {
  return request.get('/admin/content/articles/categories')
}

export const getArticleStats = (): Promise<ArticleStats> => {
  return request.get('/admin/content/articles/stats')
}

// ========== Notice ==========
export const getNoticeList = (params: { pageNum: number; pageSize: number; type?: string; keyword?: string }): Promise<{ list: Notice[]; total: number; pageNum: number; pageSize: number; pages: number }> => {
  return request.get('/admin/content/notices', params)
}

export const createNotice = (data: Partial<Notice>): Promise<{ id: string }> => {
  return request.post('/admin/content/notices', data)
}

export const updateNotice = (id: string, data: Partial<Notice>): Promise<void> => {
  return request.put(`/admin/content/notices/${id}`, data)
}

export const deleteNotice = (id: string): Promise<void> => {
  return request.delete(`/admin/content/notices/${id}`)
}

export const toggleNoticeTop = (id: string, isTop: number): Promise<void> => {
  return request.patch(`/admin/content/notices/${id}/top`, { isTop })
}

export const getNoticeStats = (): Promise<NoticeStats> => {
  return request.get('/admin/content/notices/stats')
}
