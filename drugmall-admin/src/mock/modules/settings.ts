/**
 * 系统设置Mock数据
 */
import Mock from 'mockjs'

export function setupSettingsMock() {
  // 基本设置
  Mock.mock(/\/api\/admin\/settings\/basic/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      siteName: 'DrugMall药品电商平台',
      siteLogo: Mock.Random.image('120x40', '#409EFF', '', 'png', 'logo'),
      siteDesc: '专业药品电商平台，为用户提供安全、便捷的在线购药服务',
      contactPhone: '400-888-8888',
      contactEmail: 'service@drugmall.com',
      customerServicePhone: '400-999-9999',
      address: '北京市朝阳区xxx路xxx号',
      icpNo: '京ICP备12345678号',
      businessLicense: '91110105XXXXXXXXXX',
      drugLicense: '京AA123456',
      gspCert: 'GSP12345678',
      copyright: 'DrugMall 版权所有'
    }
  }))

  Mock.mock(/\/api\/admin\/settings\/basic/, 'put', () => ({
    code: 200,
    message: '更新成功',
    data: null
  }))

  // 支付设置
  Mock.mock(/\/api\/admin\/settings\/payment/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      wechatPayEnabled: true,
      wechatPayMchId: '1234567890',
      wechatPayAppId: 'wx1234567890abcdef',
      alipayEnabled: true,
      alipayAppId: '2021001234567890',
      bankPayEnabled: false
    }
  }))

  Mock.mock(/\/api\/admin\/settings\/payment/, 'put', () => ({
    code: 200,
    message: '更新成功',
    data: null
  }))

  // 配送设置
  Mock.mock(/\/api\/admin\/settings\/delivery/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      freeShippingThreshold: 99,
      defaultShippingFee: 10,
      deliveryCompanies: ['顺丰速运', '中通快递', '圆通速递', '韵达快递', '申通快递'],
      defaultDeliveryCompany: '顺丰速运'
    }
  }))

  Mock.mock(/\/api\/admin\/settings\/delivery/, 'put', () => ({
    code: 200,
    message: '更新成功',
    data: null
  }))
}
