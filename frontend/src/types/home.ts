/**
 * 首页配置相关类型定义
 */

/** 首页完整配置 */
export interface HomePageConfig {
  pageConfig: PageConfig
  sections: HomeSection[]
}

/** 页面基础配置 */
export interface PageConfig {
  pageTitle: string
  version: string
  lastUpdated: string
}

/** 首页模块配置 */
export interface HomeSection {
  sectionId: string
  sectionType: SectionType
  title?: string
  subtitle?: string
  layout: 'vertical' | 'horizontal' | 'grid' | 'waterfall'
  visible: boolean
  sortOrder: number
  config: Record<string, any>
  components: HomeComponent[]
}

/** 模块类型枚举 */
export type SectionType =
  | 'search_bar'
  | 'tab_navigation'
  | 'promo_banner'
  | 'service_grid'
  | 'banner_subsidy'
  | 'doctor_banner'
  | 'nearby_pharmacy'
  | 'waterfall_layout'
  | 'doctor_department'
  | 'test_items'
  | 'chronic_category'
  | 'tcm_category'

/** 模块内组件 */
export interface HomeComponent {
  componentId: string
  componentType: string
  config: Record<string, any>
  data: any
  trackId: string
}

// ==================== 各模块数据类型 ====================

/** Banner 数据 */
export interface BannerData {
  title: string
  subtitle: string
  tag?: string
  bgGradient: string
  imageBg: string
  icon: string
  link: string
}

/** 服务项数据 */
export interface ServiceItemData {
  id: string
  name: string
  icon: string
  bgColor: string
}

/** 百亿补贴商品数据 */
export interface SubsidyProductData {
  id: string
  name: string
  price: number
  originalPrice?: number
  bgColor: string
}

/** 医生数据 */
export interface DoctorData {
  name: string
  bgColor: string
  icon: string
}

/** 药店数据 */
export interface PharmacyData {
  id: string
  name: string
  logoText: string
  logoColor: string
  rating: number
  monthlySales: number
  distance: number
  deliveryTime: number
  tags: Array<{ text: string; type: string }>
  products: Array<{ id: string; name: string; price: number; bgColor: string }>
}

/** 瀑布流商品/广告数据 */
export interface WaterfallItemData {
  type: 'ad' | 'product'
  id?: string
  title?: string
  subtitle?: string
  btnText?: string
  icon?: string
  bgGradient?: string
  name?: string
  specification?: string
  sales?: number
  price?: number
  deliveryTime?: number
  imageColor?: string
  imageText?: string
  isRx?: boolean
  discount?: number
}

/** 科室数据 */
export interface DepartmentData {
  id: string
  name: string
  icon: string
  bgColor: string
  tag?: string
}

/** 检测项目数据 */
export interface TestItemData {
  id: string
  name: string
  desc: string
  price: number
  time: string
  bgColor: string
  icon: string
}

/** 分类项数据 */
export interface CategoryItemData {
  id: string
  name: string
  icon: string
  bgColor: string
}

/** Tab 导航项 */
export interface TabNavItem {
  id: string
  name: string
  icon: string
}
