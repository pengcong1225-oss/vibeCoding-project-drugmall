export const API_CONFIG = {
  TIMEOUT: 30000,
  AI_TIMEOUT: 60000,
  CONTENT_TYPE: 'application/json'
} as const

export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 10,
  DRUG_RECOMMEND_SIZE: 6,
  HOT_SEARCH_SIZE: 10,
  STORE_DRUG_SIZE: 10
} as const

export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_INFO: 'userInfo'
} as const

export const CACHE_DURATION = {
  SHORT: 5 * 60 * 1000,
  MEDIUM: 30 * 60 * 1000,
  LONG: 24 * 60 * 60 * 1000
} as const
