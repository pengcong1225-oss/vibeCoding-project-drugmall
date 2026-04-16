export interface OverviewCard {
  title: string
  value: number | string
  change: string
  trend: 'up' | 'down'
  icon?: string
  color?: string
}

export interface CategoryStat {
  name: string
  value: number
  color: string
}

export interface DashboardOverview {
  cards: OverviewCard[]
  categoryData: CategoryStat[]
}

export interface GmvTrendData {
  dates: string[]
  gmv: number[]
  orders: number[]
}

export interface OrderSourceItem {
  name: string
  value: number
}
