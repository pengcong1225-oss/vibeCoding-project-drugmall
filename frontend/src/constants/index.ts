export enum ResponseCode {
  SUCCESS = 200,
  SUCCESS_ALT = 0,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  REQUEST_TIMEOUT = 408,
  SERVER_ERROR = 500,
  BAD_GATEWAY = 502,
  SERVICE_UNAVAILABLE = 503,
  GATEWAY_TIMEOUT = 504
}

export enum OrderStatus {
  PENDING = 'pending',
  PAID = 'paid',
  SHIPPED = 'shipped',
  DELIVERED = 'delivered',
  COMPLETED = 'completed',
  CANCELLED = 'cancelled',
  REFUNDING = 'refunding',
  REFUNDED = 'refunded'
}

export const ORDER_PROGRESS_STEPS = {
  [OrderStatus.PENDING]: ['pending'],
  [OrderStatus.PAID]: ['pending', 'paid'],
  [OrderStatus.SHIPPED]: ['pending', 'paid', 'shipped'],
  [OrderStatus.DELIVERED]: ['pending', 'paid', 'shipped', 'delivered'],
  [OrderStatus.COMPLETED]: ['pending', 'paid', 'shipped', 'delivered', 'completed']
}

export const CANCELLED_ORDER_STATUSES = [
  OrderStatus.CANCELLED,
  OrderStatus.REFUNDING,
  OrderStatus.REFUNDED
]

export const SHIPPING_ORDER_STATUSES = [
  OrderStatus.SHIPPED,
  OrderStatus.DELIVERED,
  OrderStatus.COMPLETED
]

export enum PrescriptionStatus {
  PENDING = 'pending',
  APPROVED = 'approved',
  REJECTED = 'rejected',
  EXPIRED = 'expired'
}

export enum ConsultationStatus {
  PENDING = 'pending',
  PROCESSING = 'processing',
  COMPLETED = 'completed',
  CLOSED = 'closed'
}

export enum PayType {
  WECHAT = 'wechat',
  ALIPAY = 'alipay',
  BALANCE = 'balance'
}

export enum ReviewStatus {
  PENDING = 'pending',
  COMPLETED = 'completed'
}

export enum StepStatus {
  COMPLETED = 'completed',
  CURRENT = 'current',
  PENDING = 'pending'
}
