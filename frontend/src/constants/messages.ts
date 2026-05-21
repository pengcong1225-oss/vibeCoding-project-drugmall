export const messages = {
  common: {
    success: '成功',
    error: '错误',
    loading: '加载中...',
    noData: '暂无数据',
    confirm: '确认',
    cancel: '取消',
    delete: '删除',
    edit: '编辑',
    save: '保存',
    submit: '提交',
    back: '返回',
    more: '更多',
    developIng: '功能开发中',
    serverBusy: '服务器繁忙，请稍后重试',
    networkError: '网络错误，请检查网络连接',
    operationFailed: '操作失败',
    requestFailed: '请求失败',
    requestCancelled: '请求已取消',
    requestTimeout: '请求超时，请稍后重试'
  },
  auth: {
    loginExpired: '登录已过期，请重新登录',
    noPermission: '没有权限执行此操作',
    resourceNotFound: '请求的资源不存在',
    loginSuccess: '登录成功',
    loginFailed: '登录失败，请重试',
    sendCodeSuccess: '验证码已发送',
    sendCodeFailed: '验证码发送失败，请重试',
    invalidPhone: '请输入正确的手机号',
    wechatLoginDevelopIng: '微信登录功能开发中'
  },
  cart: {
    empty: '购物车为空',
    emptyAddFirst: '购物车为空，请先添加商品',
    cleared: '购物车已清空'
  },
  order: {
    submitSuccess: '订单提交成功',
    submitFailed: '订单提交失败，请重试',
    addressIncomplete: '请填写完整的地址信息',
    addressDeleted: '地址已删除',
    addressAdded: '地址添加成功',
    addressAddFailed: '添加地址失败',
    selectAddress: '请选择收货地址'
  },
  prescription: {
    needConsultation: '处方药需要先进行在线问诊开方',
    patientListFailed: '获取用药人列表失败',
    drugInfoFailed: '获取药品信息失败',
    selectPatient: '请选择用药人',
    selectSymptom: '请选择疾病症状',
    agreeConsent: '请阅读并同意知情同意书',
    applySuccess: '申请提交成功'
  },
  consultation: {
    doctorListFailed: '获取医生列表失败',
    recordDevelopIng: '咨询记录功能开发中',
    sendFailed: '发送失败，请重试',
    prescriptionDetail: '查看处方详情'
  },
  category: {
    listFailed: '获取分类列表失败，请稍后重试',
    drugListFailed: '获取药品列表失败，请稍后重试'
  },
  home: {
    scanDevelopIng: '扫码功能开发中',
    voiceNotSupport: '您的浏览器不支持语音搜索，请使用文字搜索',
    voiceHint: '请说出您要搜索的药品、症状或品牌',
    voiceFailed: '语音识别失败，请重试',
    allergyReportDevelopIng: '过敏报告功能开发中'
  },
  network: {
    restored: '网络已恢复',
    disconnected: '网络已断开，请检查网络连接'
  },
  store: {
    moreDevelopIng: '更多功能开发中',
    askDoctorDevelopIng: '问医生功能开发中',
    qualificationDevelopIng: '商家资质功能开发中',
    reviewDevelopIng: '评价功能开发中'
  },
  drug: {
    shareDevelopIng: '分享功能开发中'
  }
} as const

export type MessageKey = keyof typeof messages | `${keyof typeof messages}.${string}`

export const t = (key: MessageKey): string => {
  const keys = key.split('.')
  let result: any = messages
  for (const k of keys) {
    if (result && typeof result === 'object' && k in result) {
      result = result[k]
    } else {
      return key
    }
  }
  return typeof result === 'string' ? result : key
}
