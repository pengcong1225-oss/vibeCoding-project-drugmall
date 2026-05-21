export interface OperationLog {
  id: string
  userId: string
  username: string
  module: string
  action: string
  method: string
  url: string
  params: string
  result: string
  ip: string
  userAgent: string
  duration: number
  status: 'success' | 'failure'
  errorMsg?: string
  createTime: string
}

export interface LogQuery {
  pageNum: number
  pageSize: number
  username?: string
  module?: string
  action?: string
  status?: string
  startDate?: string
  endDate?: string
  ip?: string
}
