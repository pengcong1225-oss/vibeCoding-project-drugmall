// 药品相关类型定义

// 药品信息
export interface Drug {
  id: string
  name: string
  specification: string
  manufacturer: string
  price: number
  originalPrice?: number
  image: string
  images?: string[]
  isRx: boolean
  categoryId: string
  categoryName?: string
  disease?: string
  usage?: string
  dosage?: string
  adverseReactions?: string
  contraindications?: string
  precautions?: string
  storage?: string
  approvalNumber?: string
  barCode?: string
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
