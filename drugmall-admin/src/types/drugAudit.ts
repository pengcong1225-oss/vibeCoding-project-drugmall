export interface DrugAudit {
  id: string
  drugId: string
  drugName: string
  specification: string
  manufacturer: string
  auditType: 'new' | 'modify'
  status: 'pending' | 'approved' | 'rejected'
  submitter: string
  submitTime: string
  auditor?: string
  auditTime?: string
  auditComment?: string
  changes?: Record<string, any>
}

export interface DrugAuditQuery {
  pageNum: number
  pageSize: number
  drugName?: string
  auditType?: string
  status?: string
  startDate?: string
  endDate?: string
}
