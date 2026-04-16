export interface BasicConfig {
  siteName: string
  siteLogo: string
  siteIcon: string
  siteDescription: string
  siteKeywords: string
  servicePhone: string
  serviceEmail: string
  companyAddress: string
  workTime: string
  icp: string
  police: string
  business: string
  copyright: string
}

export interface PaymentConfig {
  wxEnabled: boolean
  wxMchId: string
  wxKey: string
  wxCertPath: string
  wxNotifyUrl: string
  aliEnabled: boolean
  aliAppId: string
  aliPrivateKey: string
  aliPublicKey: string
  aliNotifyUrl: string
  balanceEnabled: boolean
  feeRate: number
}
