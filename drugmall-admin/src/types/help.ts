export interface HelpCenter {
  id: string
  title: string
  category: string
  content: string
  sort: number
  status: 'active' | 'disabled'
  createTime: string
  updateTime: string
}

export interface HelpCenterQuery {
  pageNum: number
  pageSize: number
  title?: string
  category?: string
  status?: string
}
