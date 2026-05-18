export interface Banner {
  id: string
  title: string
  image: string
  link: string
  sort: number
  status: number
  clicks: number
  views: number
  position: string
  device: string
  createTime?: string
}

export interface Article {
  id: string
  title: string
  categoryId: string
  categoryName: string
  cover: string
  summary: string
  content?: string
  tags: string[]
  isRecommend: number
  isTop?: number
  status: number
  views: number
  publishTime: string | null
  createTime?: string
}

export interface ArticleInfo extends Article {}

export interface ArticleCategory {
  id: string
  name: string
  icon: string
  sort: number
  status: number
}

export interface ArticleStats {
  total: number
  published: number
  recommended: number
  draft: number
}

export interface Notice {
  id: string
  title: string
  type: string
  content: string
  isTop: number
  status: number
  views: number
  publishTime: string | null
  createTime?: string
}

export interface NoticeStats {
  total: number
  published: number
  top: number
  draft: number
}

export interface NoticeInfo extends Notice {}
