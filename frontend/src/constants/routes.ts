export const ROUTES = {
  HOME: '/home',
  LOGIN: '/login',
  SEARCH: '/search',
  CART: '/cart',
  CATEGORY: '/category',
  USER: '/user',
  SETTINGS: '/settings',
  MESSAGES: '/messages',
  ADDRESS: '/address',
  PATIENT: '/patient',
  PATIENT_ADD: '/patient/add',
  PATIENT_EDIT: '/patient/edit',
  PATIENT_LIST: '/patient/list',
  ORDER_LIST: '/order/list',
  ORDER_DETAIL: '/order/detail',
  ORDER_CONFIRM: '/order/confirm',
  ORDER_PAY: '/order/pay',
  INQUIRY: '/inquiry',
  INQUIRY_LIST: '/inquiry/list',
  INQUIRY_DETAIL: '/inquiry/detail',
  INQUIRY_PRE: '/inquiry/pre',
  INQUIRY_CHAT: '/inquiry/chat',
  INQUIRY_WAITING: '/inquiry/waiting',
  INQUIRY_PAY: '/inquiry/pay',
  INQUIRY_CHECKOUT: '/inquiry/checkout',
  INQUIRY_AI_TRIAGE: '/inquiry/ai-triage',
  INQUIRY_AI_ASSISTANT: '/inquiry/ai-assistant',
  INQUIRY_DEPARTMENT_TRIAGE: '/inquiry/department-triage',
  PRESCRIPTION: '/prescription',
  PRESCRIPTION_LIST: '/prescription/list',
  PRESCRIPTION_APPLY: '/prescription/apply',
  PRESCRIPTION_CONSULT: '/prescription/consult',
  PRESCRIPTION_SUCCESS: '/prescription/success',
  DOCTOR_DETAIL: '/doctor/detail',
  DRUG_DETAIL: '/drug/detail',
  DRUG: '/drug',
  STORE: '/store',
  STORE_DETAIL: '/store/detail',
  STORE_DRUG_DETAIL: '/store/drug-detail',
  AI_ASSISTANT: '/ai-assistant',
  FEEDBACK: '/feedback',
  HELP: '/help',
  SYMPTOM_TEST: '/symptom-test',
  TEST_SERVICE: '/test-service',
  TEST_HOME: '/test/home',
  COUPONS: '/coupons',
  PROMOTION_SLIMMING: '/promotion/slimming',
  CATEGORY_ALLERGY: '/category/allergy',
  ERROR_404: '/error/404'
} as const

export const getDrugDetailRoute = (id: string | number) => `${ROUTES.DRUG}/${id}` as const
export const getStoreRoute = (id: string | number) => `${ROUTES.STORE}/${id}` as const
export const getStoreDrugRoute = (storeId: string | number, drugId: string | number) => `${ROUTES.STORE}/${storeId}/drug/${drugId}` as const
export const getOrderDetailRoute = (id: string | number) => `${ROUTES.ORDER_DETAIL}/${id}` as const
export const getInquiryCheckoutRoute = (id: string | number) => `${ROUTES.INQUIRY_CHECKOUT}/${id}` as const
export const getInquiryPayRoute = (id: string | number) => `${ROUTES.INQUIRY_PAY}/${id}` as const
export const getInquiryPreRoute = (id: string | number) => `${ROUTES.INQUIRY_PRE}/${id}` as const
export const getInquiryWaitingRoute = (id: string | number) => `${ROUTES.INQUIRY_WAITING}/${id}` as const
export const getPrescriptionApplyRoute = (drugId?: string) => drugId ? `${ROUTES.PRESCRIPTION_APPLY}?drugId=${drugId}` : ROUTES.PRESCRIPTION_APPLY as const
