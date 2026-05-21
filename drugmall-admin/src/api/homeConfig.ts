import { request } from '@/utils/request'
import type {
  HomeGlobalConfig,
  HomeConfigVersion,
  TabConfig, TabQueryParams,
  SectionConfig, SectionQueryParams,
  BannerConfig, BannerQueryParams,
  KingKongConfig, KingKongQueryParams,
  AdSlotConfig, AdSlotQueryParams,
  ReleaseVersion, ReleaseQueryParams, ReleaseSummary
} from '@/types/homeConfig'

export function getHomeGlobalConfig() {
  return request<HomeGlobalConfig>({
    url: '/admin/home/config/global',
    method: 'get'
  })
}

export function saveHomeGlobalConfig(data: HomeGlobalConfig) {
  return request({
    url: '/admin/home/config/global',
    method: 'put',
    data
  })
}

export function getHomeConfigVersions() {
  return request<{ list: HomeConfigVersion[], total: number }>({
    url: '/admin/home/config/versions',
    method: 'get'
  })
}

export function publishHomeConfig(data: { version: string, changelog: string }) {
  return request({
    url: '/admin/home/config/publish',
    method: 'post',
    data
  })
}

export function rollbackHomeConfig(versionId: string) {
  return request({
    url: `/admin/home/config/rollback/${versionId}`,
    method: 'post'
  })
}

// Tab 配置
export function getTabList(params: TabQueryParams) {
  return request.get<{ list: TabConfig[], total: number }>('/admin/home/tabs', params)
}

export function createTab(data: Partial<TabConfig>) {
  return request.post('/admin/home/tabs', data)
}

export function updateTab(id: string, data: Partial<TabConfig>) {
  return request.put(`/admin/home/tabs/${id}`, data)
}

export function deleteTab(id: string) {
  return request.delete(`/admin/home/tabs/${id}`)
}

export function updateTabSort(data: { ids: string[] }) {
  return request.put('/admin/home/tabs/sort', data)
}

// 模块配置
export function getSectionList(params: SectionQueryParams) {
  return request.get<{ list: SectionConfig[], total: number }>('/admin/home/sections', params)
}

export function createSection(data: Partial<SectionConfig>) {
  return request.post('/admin/home/sections', data)
}

export function updateSection(id: string, data: Partial<SectionConfig>) {
  return request.put(`/admin/home/sections/${id}`, data)
}

export function deleteSection(id: string) {
  return request.delete(`/admin/home/sections/${id}`)
}

export function copySection(id: string) {
  return request.post(`/admin/home/sections/${id}/copy`)
}

// 轮播图配置
export function getBannerList(params: BannerQueryParams) {
  return request.get<{ list: BannerConfig[], total: number }>('/admin/home/banners', params)
}

export function createBanner(data: Partial<BannerConfig>) {
  return request.post('/admin/home/banners', data)
}

export function updateBanner(id: string, data: Partial<BannerConfig>) {
  return request.put(`/admin/home/banners/${id}`, data)
}

export function deleteBanner(id: string) {
  return request.delete(`/admin/home/banners/${id}`)
}

// 金刚位配置
export function getKingKongList(params: KingKongQueryParams) {
  return request.get<{ list: KingKongConfig[], total: number }>('/admin/home/kingkong', params)
}

export function createKingKong(data: Partial<KingKongConfig>) {
  return request.post('/admin/home/kingkong', data)
}

export function updateKingKong(id: string, data: Partial<KingKongConfig>) {
  return request.put(`/admin/home/kingkong/${id}`, data)
}

export function deleteKingKong(id: string) {
  return request.delete(`/admin/home/kingkong/${id}`)
}

// 广告位配置
export function getAdSlotList(params: AdSlotQueryParams) {
  return request.get<{ list: AdSlotConfig[], total: number }>('/admin/home/adslots', params)
}

export function createAdSlot(data: Partial<AdSlotConfig>) {
  return request.post('/admin/home/adslots', data)
}

export function updateAdSlot(id: string, data: Partial<AdSlotConfig>) {
  return request.put(`/admin/home/adslots/${id}`, data)
}

export function deleteAdSlot(id: string) {
  return request.delete(`/admin/home/adslots/${id}`)
}

// 发布管理
export function getReleaseList(params: ReleaseQueryParams) {
  return request.get<{ list: ReleaseVersion[], total: number }>('/admin/home/releases', params)
}

export function publishRelease(data: { version: string, changelog: string }) {
  return request.post('/admin/home/releases/publish', data)
}

export function rollbackRelease(versionId: string) {
  return request.post(`/admin/home/releases/${versionId}/rollback`)
}

export function deleteRelease(versionId: string) {
  return request.delete(`/admin/home/releases/${versionId}`)
}

export function getReleaseSummary() {
  return request.get<ReleaseSummary>('/admin/home/releases/summary')
}
