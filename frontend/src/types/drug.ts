// 药品相关类型定义

// 药品信息
export interface Drug {
  id: string
  name: string
  genericName?: string
  brand?: string
  specification: string
  manufacturer: string
  price: number
  originalPrice?: number
  image: string
  imageColor?: string
  imageText?: string
  images?: string[]
  isRx: boolean
  isNationalEssential?: boolean
  categoryId: string
  categoryName?: string
  category?: string
  disease?: string
  usage?: string
  dosage?: string
  adverseReactions?: string
  contraindications?: string
  precautions?: string
  storage?: string
  validity?: string
  ingredients?: string
  appearance?: string
  drugInteractions?: string
  approvalNumber?: string
  barCode?: string
  medicalInsuranceCode?: string  // 医保编码
  traceabilityCode?: string      // 追溯码
  isLongPrescription?: boolean   // 是否长处方用药
  insuranceCategory?: string     // 医保类别: 甲类/乙类/丙类
  specifications?: DrugSpecification[]  // 规格列表
  stock: number
  sales: number
  rating?: number
  reviewCount?: number
  tags?: string[]
  description?: string
  detail?: string
  status: number
  createTime?: string
  updateTime?: string
}

// 药品分类
export interface DrugCategory {
  id: string
  name: string
  parentId?: string
  icon?: string
  image?: string
  sort: number
  level: number
  children?: DrugCategory[]
  description?: string
  status: number
}

// 药品搜索参数
export interface DrugSearchParams {
  keyword?: string
  categoryId?: string
  isRx?: boolean
  minPrice?: number
  maxPrice?: number
  sort?: 'default' | 'price_asc' | 'price_desc' | 'sales' | 'new'
  page?: number
  size?: number
}

// 药品列表响应
export interface DrugListResult {
  list: Drug[]
  total: number
  page: number
  size: number
}

// 药品详情
export interface DrugDetail extends Drug {
  relatedDrugs?: Drug[]
  recommendedDrugs?: Drug[]
  reviews?: DrugReview[]
  faqs?: DrugFAQ[]
}

// 药品评价
export interface DrugReview {
  id: string
  userId: string
  userName: string
  userAvatar?: string
  drugId: string
  orderId: string
  rating: number
  content: string
  images?: string[]
  tags?: string[]
  isAnonymous: boolean
  isRecommended: boolean
  helpfulCount: number
  createTime: string
  reply?: {
    content: string
    createTime: string
  }
}

// 药品FAQ
export interface DrugFAQ {
  id: string
  question: string
  answer: string
  sort: number
}

// 药品规格
export interface DrugSpecification {
  id: string | number
  specName: string
  specCode?: string
  price: number
  originalPrice?: number
  stock: number
  barCode?: string
  isDefault: boolean
}

// 药品品牌
export interface DrugBrand {
  id: string
  name: string
  logo?: string
  description?: string
  sort: number
  status: number
}

// 药品搜索建议
export interface SearchSuggestion {
  keyword: string
  type: 'drug' | 'disease' | 'symptom'
  count?: number
}

// 热门搜索
export interface HotSearch {
  keyword: string
  heat: number
  isNew?: boolean
  isHot?: boolean
}
