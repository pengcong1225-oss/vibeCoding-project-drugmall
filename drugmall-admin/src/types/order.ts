// 订单相关类型定义

// 订单状态枚举
export enum OrderStatus {
  PENDING_PAYMENT = 0,      // 待付款
  PENDING_SHIPMENT = 1,     // 待发货
  PENDING_RECEIPT = 2,      // 待收货
  PENDING_REVIEW = 3,       // 待评价
  COMPLETED = 4,            // 已完成
  CANCELLED = -1,           // 已取消
  REFUNDING = -2,           // 退款中
  REFUNDED = -3             // 已退款
}

// 订单信息
export interface Order {
  id: string
  orderNo: string
  userId: string
  username?: string
  nickname?: string
  phone?: string
  totalAmount: number
  discountAmount: number
  freightAmount: number
  payAmount: number
  status: number
  statusName?: string
  payType?: number
  payTime?: string
  shipTime?: string
  receiveTime?: string
  remark?: string
  address?: OrderAddress
  items: OrderItem[]
  logistics?: LogisticsInfo
  createTime: string
  updateTime: string
}

// 订单地址
export interface OrderAddress {
  id: string
  orderId: string
  name: string
  phone: string
  province: string
  city: string
  district: string
  address: string
  zipCode?: string
}

// 订单商品项
export interface OrderItem {
  id: string
  orderId: string
  productId: string
  productName: string
  productImage: string
  spec: string
  price: number
  quantity: number
  totalAmount: number
  isRx: number
}

// 物流信息
export interface LogisticsInfo {
  id: string
  orderId: string
  company: string
  companyName?: string
  trackingNo: string
  status: number
  traces?: LogisticsTrace[]
  shipTime: string
  receiveTime?: string
}

// 物流轨迹
export interface LogisticsTrace {
  time: string
  status: string
  location?: string
}

// 订单查询参数
export interface OrderQueryParams {
  pageNum: number
  pageSize: number
  keyword?: string
  orderNo?: string
  username?: string
  phone?: string
  status?: number
  startTime?: string
  endTime?: string
  minAmount?: number
  maxAmount?: number
}

// 订单列表响应
export interface OrderListResult {
  list: Order[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}

// 发货表单数据
export interface ShipFormData {
  orderId: string
  company: string
  trackingNo: string
  remark?: string
}

// 退款处理表单
export interface RefundFormData {
  orderId: string
  agree: boolean
  amount?: number
  reason?: string
}
