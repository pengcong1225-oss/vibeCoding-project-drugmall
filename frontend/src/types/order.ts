// 订单相关类型定义

// 订单状态
export type OrderStatus = 
  | 'pending'           // 待支付
  | 'paid'              // 已支付
  | 'confirmed'         // 已确认
  | 'shipped'           // 已发货/配送中
  | 'delivered'         // 已送达
  | 'completed'         // 已完成
  | 'cancelled'         // 已取消
  | 'refunding'         // 退款中
  | 'refunded'          // 已退款

// 配送方式
export type DeliveryType = 'delivery' | 'self_pickup' | 'same_day'

// 支付方式
export type PayType = 'wechat' | 'alipay' | 'balance' | 'credit'

// 订单商品项
export interface OrderItem {
  id: string
  orderId: string
  drugId: string
  name: string
  specification: string
  manufacturer: string
  image: string
  price: number
  originalPrice?: number
  quantity: number
  isRx: boolean
  disease?: string
  usage?: string
  subtotal: number
  reviewStatus?: 'pending' | 'completed'
  reviewId?: string
}

// 订单信息
export interface Order {
  id: string
  orderNo: string
  userId: string
  status: OrderStatus
  statusText: string
  
  // 商品信息
  items: OrderItem[]
  totalQuantity: number
  
  // 金额信息
  totalAmount: number
  drugAmount: number
  deliveryFee: number
  discountAmount: number
  couponAmount: number
  payableAmount: number
  paidAmount: number
  
  // 配送信息
  deliveryType: DeliveryType
  receiverName?: string
  receiverPhone?: string
  receiverAddress?: string
  addressId?: string
  
  // 自提信息
  pickupStore?: {
    id: string
    name: string
    address: string
    phone: string
    businessHours: string
  }
  pickupCode?: string
  
  // 支付信息
  payType?: PayType
  payTime?: string
  
  // 物流信息
  logisticsNo?: string
  logisticsCompany?: string
  logisticsInfo?: LogisticsInfo[]
  
  // 时间信息
  createTime: string
  expireTime?: string
  confirmTime?: string
  deliveryTime?: string
  completeTime?: string
  cancelTime?: string
  
  // 其他
  remark?: string
  prescriptionId?: string
  needRxReview?: boolean
  rxReviewStatus?: 'pending' | 'approved' | 'rejected'
}

// 物流信息
export interface LogisticsInfo {
  time: string
  content: string
  status?: string
}

// 创建订单参数
export interface CreateOrderParams {
  cartItemIds?: string[]
  drugId?: string
  quantity?: number
  addressId: string
  deliveryType: DeliveryType
  remark?: string
  couponId?: string
  prescriptionId?: string
}

// 订单确认页数据
export interface OrderConfirmData {
  items: OrderItem[]
  totalAmount: number
  drugAmount: number
  deliveryFee: number
  discountAmount: number
  payableAmount: number
  address?: UserAddress
  coupons: UserCoupon[]
}

// 支付参数
export interface PayParams {
  orderId: string
  payType: PayType
  returnUrl?: string
}

// 支付结果
export interface PayResult {
  success: boolean
  orderId: string
  payType: PayType
  payTime: string
  transactionId?: string
}

// 订单查询参数
export interface OrderQueryParams {
  status?: OrderStatus | OrderStatus[]
  startTime?: string
  endTime?: string
  keyword?: string
  page?: number
  size?: number
}

// 订单统计
export interface OrderStats {
  totalCount: number
  pendingPayment: number
  pendingShipment: number
  pendingReceipt: number
  pendingReview: number
  afterSale: number
  totalAmount: number
}

// 退款申请参数
export interface RefundApplyParams {
  orderId: string
  itemIds?: string[]
  reason: string
  description?: string
  images?: string[]
  refundAmount?: number
}

// 退款信息
export interface RefundInfo {
  id: string
  orderId: string
  status: 'pending' | 'processing' | 'completed' | 'rejected'
  reason: string
  description?: string
  images?: string[]
  refundAmount: number
  actualRefundAmount?: number
  createTime: string
  completeTime?: string
  rejectReason?: string
}

// 从其他类型文件导入
interface UserAddress {
  id: string
  name: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: boolean
  tag?: string
}

interface UserCoupon {
  id: string
  name: string
  type: 'full_reduction' | 'discount' | 'cash'
  value: number
  minAmount: number
  startTime: string
  endTime: string
  status: 'unused' | 'used' | 'expired'
  description?: string
  scope?: string
}
