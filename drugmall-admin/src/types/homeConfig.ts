export interface HomeGlobalConfig {
  pageTitle: string
  backgroundColor: string
  theme: 'light' | 'dark' | 'custom'
  primaryColor: string
  fontSize: number
  showSearchBar: boolean
  showCategoryNav: boolean
  layout: 'grid' | 'list'
  customCSS: string
}

export interface HomeConfigVersion {
  id: string
  version: string
  status: 'draft' | 'published' | 'archived'
  publishTime?: string
  publisher?: string
  changelog: string
  createdAt: string
}

export interface TabConfig {
  id: string
  tabId: string
  name: string
  icon: string
  activeIcon: string
  primaryColor: string
  gradient: string
  bgColor: string
  headerBgImage: string
  sectionIds: string[]
  sortOrder: number
  status: 0 | 1
  createTime: string
  updateTime: string
}

export interface TabQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  status?: 0 | 1
}

export interface SectionConfig {
  id: string
  name: string
  subtitle: string
  sectionType: string
  layout: string
  bgColor: string
  borderRadius: number
  marginTop: number
  marginBottom: number
  tabIds: string[]
  sortOrder: number
  status: 0 | 1
  createTime: string
  updateTime: string
}

export interface SectionQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  sectionType?: string
  status?: 0 | 1
}

export interface BannerConfig {
  id: string
  title: string
  image: string
  sectionId?: string
  jumpType: 'none' | 'link' | 'page'
  jumpUrl: string
  startTime: string
  endTime: string
  sortOrder: number
  status: 0 | 1
  createTime: string
  updateTime: string
}

export interface BannerQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  sectionId?: string
  status?: 0 | 1
}

export interface KingKongConfig {
  id: string
  name: string
  icon: string
  iconType: 'image' | 'icon'
  sectionId?: string
  jumpType: 'none' | 'link' | 'page'
  jumpUrl: string
  badge: string
  badgeColor: string
  sortOrder: number
  status: 0 | 1
  createTime: string
  updateTime: string
}

export interface KingKongQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  sectionId?: string
  status?: 0 | 1
}

export interface AdSlotConfig {
  id: string
  name: string
  image: string
  sectionId?: string
  layout: 'single' | 'double' | 'grid'
  jumpType: 'none' | 'link' | 'page'
  jumpUrl: string
  width: number
  height: number
  startTime: string
  endTime: string
  sortOrder: number
  status: 0 | 1
  createTime: string
  updateTime: string
}

export interface AdSlotQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  sectionId?: string
  status?: 0 | 1
}

export interface ReleaseVersion {
  id: string
  version: string
  status: 'draft' | 'published' | 'archived'
  publishTime?: string
  publisher?: string
  changelog: string
  createdAt: string
  tabCount: number
  sectionCount: number
  bannerCount: number
  kingkongCount: number
  adslotCount: number
}

export interface ReleaseQueryParams {
  pageNum?: number
  pageSize?: number
  version?: string
  status?: string
}

export interface ReleaseSummary {
  currentVersion: string
  draftVersion: string
  lastPublishTime: string
  lastPublisher: string
  changeSummary: {
    tabs: number
    sections: number
    banners: number
    kingkongs: number
    adslots: number
  }
}
