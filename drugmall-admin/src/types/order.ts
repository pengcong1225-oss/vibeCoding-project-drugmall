export interface Order {
  id: string
  orderNo: string
  userId: string
  userName: string
  amount: number
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled' | 'refunding' | 'refunded'
  paymentMethod: string
  createTime: string
  items: Array<{
    drugId: string
    drugName: string
    quantity: number
    price: number
  }>
}

export interface OrderQueryParams {
  pageNum: number
  pageSize: number
  orderNo?: string
  status?: string
  startDate?: string
  endDate?: string
}

export interface Refund {
  id: string
  refundNo: string
  orderNo: string
  userId: string
  userName: string
  amount: number
  reason: string
  images: string[]
  status: 'pending' | 'approved' | 'rejected' | 'processing' | 'completed'
  type: 'refund_only' | 'return_and_refund'
  createTime: string
  auditTime?: string
  auditor?: string
  rejectReason?: string
  trackingNo?: string
}

export interface AbnormalOrder {
  id: string
  orderNo: string
  userId: string
  userName: string
  amount: number
  abnormalType: 'timeout_pay' | 'timeout_ship' | 'cancel' | 'fraud' | 'other'
  status: 'pending' | 'processing' | 'resolved'
  description: string
  createTime: string
  handler?: string
  handleTime?: string
  handleResult?: string
}

export interface RefundQuery {
  pageNum: number
  pageSize: number
  refundNo?: string
  orderNo?: string
  status?: string
  type?: string
  startDate?: string
  endDate?: string
}

export interface AbnormalOrderQuery {
  pageNum: number
  pageSize: number
  orderNo?: string
  abnormalType?: string
  status?: string
  startDate?: string
  endDate?: string
}
