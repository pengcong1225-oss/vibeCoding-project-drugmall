// 购物车相关类型定义

// 购物车商品项
export interface CartItem {
  id: string
  drugId: string
  name: string
  specification: string
  manufacturer: string
  price: number
  originalPrice?: number
  quantity: number
  image: string
  disease?: string
  usage?: string
  isRx: boolean
  isSelected?: boolean
  stock?: number
  warningStock?: number
  categoryId?: string
  categoryName?: string
  tags?: string[]
}

// 购物车药店分组（支持多药店时）
export interface CartStoreGroup {
  storeId: string
  storeName: string
  items: CartItem[]
  isSelected: boolean
  totalAmount: number
  totalQuantity: number
  hasRxItem: boolean
}

// 购物车统计
export interface CartStats {
  totalCount: number
  totalQuantity: number
  selectedCount: number
  selectedQuantity: number
  totalAmount: number
  selectedAmount: number
  originalAmount: number
  discountAmount: number
  hasRxItem: boolean
}

// 添加到购物车参数
export interface AddToCartParams {
  drugId: string
  quantity: number
  disease?: string
  usage?: string
}

// 更新购物车参数
export interface UpdateCartParams {
  itemId: string
  quantity?: number
  isSelected?: boolean
  disease?: string
  usage?: string
}

// 购物车验证结果
export interface CartValidationResult {
  valid: boolean
  invalidItems: {
    itemId: string
    reason: 'stock_insufficient' | 'price_changed' | 'offline' | 'rx_requirement'
    message: string
  }[]
  changedItems: {
    itemId: string
    oldPrice: number
    newPrice: number
    oldStock: number
    newStock: number
  }[]
}

// 购物车促销信息
export interface CartPromotion {
  type: 'discount' | 'gift' | 'reduce' | 'shipping_free'
  title: string
  description: string
  condition: string
  benefit: string
  matched: boolean
}

// 购物车推荐
export interface CartRecommendation {
  type: 'frequently_bought' | 'similar' | 'combo'
  title: string
  items: {
    drugId: string
    name: string
    image: string
    price: number
    originalPrice?: number
  }[]
}

// 购物车结算信息
export interface CartCheckoutInfo {
  items: CartItem[]
  totalAmount: number
  deliveryFee: number
  discountAmount: number
  payableAmount: number
  availableCoupons: {
    id: string
    name: string
    value: number
    minAmount: number
  }[]
  unavailableCoupons: {
    id: string
    name: string
    reason: string
  }[]
  defaultAddress?: {
    id: string
    name: string
    phone: string
    fullAddress: string
  }
}
