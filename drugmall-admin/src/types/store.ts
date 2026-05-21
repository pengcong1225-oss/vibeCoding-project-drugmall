// 门店管理模块类型定义

// 门店信息
export interface StoreInfo {
  id: number
  name: string
  address: string
  contact: string
  phone: string
  province?: string
  city?: string
  district?: string
  latitude?: number
  longitude?: number
  licenseNo: string
  licenseStatus: number
  status: number
  joinTime: string
  createTime: string
}

// 门店详情
export interface StoreDetail extends StoreInfo {
  businessHours?: string
  deliveryRange?: number
  deliveryFee?: number
  minOrderAmount?: number
  storeImages?: string[]
  introduction?: string
  auditMaterials: StoreAuditMaterials
  stats: StoreStats
  recentDrugs?: StoreDrugItem[]
}

// 门店审核材料
export interface StoreAuditMaterials {
  businessLicense: string
  drugLicense: string
  gspCert: string
  legalIdFront: string
  legalIdBack: string
  storePhoto?: string
}

// 门店统计数据
export interface StoreStats {
  totalOrders: number
  totalSales: number
  monthOrders: number
  monthSales: number
  drugCount: number
  rating: number
}

// 门店审核
export interface StoreAudit {
  id: number
  storeId: number
  storeName: string
  contact: string
  phone: string
  status: number
  submitTime: string
  auditOpinion?: string
  auditTime?: string
  auditorName?: string
}

// 门店药品
export interface StoreDrugItem {
  id: number
  storeId: number
  drugId: number
  drugName: string
  specification: string
  manufacturer: string
  stock: number
  price: number
  status: number
  createTime: string
}

// 查询参数
export interface StoreQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  city?: string
  licenseStatus?: number
  status?: number
  startTime?: string
  endTime?: string
}

export interface AuditQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  status?: number
  startTime?: string
  endTime?: string
}

export interface StoreDrugQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  status?: number
}
