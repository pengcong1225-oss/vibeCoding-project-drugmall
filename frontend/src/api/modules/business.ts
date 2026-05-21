import request from '@/api/request'

export interface DepartmentData {
  code: string
  name: string
  icon: string
  tag: string
  tagType: string
  sortOrder: number
}

export interface DepartmentConfigData {
  departmentCode: string
  price: number
  originalPrice: number
  subsidy: number
  symptoms: string
  responseTime: number
  answerTime: number
  example: string
  quickSymptoms: string[]
}

export interface DepartmentTagData {
  code: string
  label: string
  sortOrder: number
}

export interface DictDataItem {
  label: string
  value: string
  sortOrder: number
  isDefault: number
}

export interface PaymentMethodData {
  code: string
  name: string
  description: string
  icon: string
  sortOrder: number
}

export interface ServiceShortcutData {
  name: string
  subtitle: string
  doctorAvatar: string
  sortOrder: number
}

export interface ConsultationStepData {
  step: number
  name: string
  description: string
  sortOrder: number
}

export const businessApi = {
  getDepartments: () => {
    return request.get<DepartmentData[]>('/business/departments')
  },

  getDepartmentConfig: (code: string) => {
    return request.get<DepartmentConfigData>(`/business/departments/${code}/config`)
  },

  getDepartmentTags: () => {
    return request.get<DepartmentTagData[]>('/business/department-tags')
  },

  getDictData: (typeCode: string) => {
    return request.get<DictDataItem[]>(`/business/dict/${typeCode}`)
  },

  getPaymentMethods: () => {
    return request.get<PaymentMethodData[]>('/business/payment-methods')
  },

  getServiceShortcuts: () => {
    return request.get<ServiceShortcutData[]>('/business/service-shortcuts')
  },

  getConsultationSteps: () => {
    return request.get<ConsultationStepData[]>('/business/consultation-steps')
  }
}
