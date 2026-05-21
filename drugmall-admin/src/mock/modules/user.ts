/**
 * 用户管理Mock数据
 */
import Mock from 'mockjs'

export function setupUserMock() {
  // 用户列表
  Mock.mock(/\/api\/admin\/users/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 156
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        nickname: Mock.Random.cname(),
        phone: /^1[3-9]\d{9}/.exec(Mock.mock('@string("number", 11)'))?.[0] || Mock.mock('@phone'),
        avatar: Mock.Random.image('100x100', '#409EFF', '', 'png', 'avatar'),
        gender: Mock.Random.integer(0, 1),
        realNameStatus: Mock.Random.integer(0, 2),
        status: Mock.Random.integer(0, 3),
        prescriptionEnabled: Mock.Random.boolean(),
        rxDrugPurchaseCount: Mock.Random.integer(0, 10),
        totalOrderCount: Mock.Random.integer(0, 50),
        totalAmount: Mock.Random.float(100, 10000, 0, 2),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        lastLoginTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  // 用户详情
  Mock.mock(/\/api\/admin\/users\/\d+/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      id: Mock.Random.id(),
      nickname: Mock.Random.cname(),
      phone: Mock.mock('@phone'),
      avatar: Mock.Random.image('100x100', '#409EFF', '', 'png', 'avatar'),
      gender: Mock.Random.integer(0, 1),
      age: Mock.Random.integer(18, 80),
      realNameStatus: Mock.Random.integer(0, 2),
      realName: Mock.Random.cname(),
      idCard: Mock.mock('@id'),
      status: Mock.Random.integer(0, 3),
      prescriptionEnabled: Mock.Random.boolean(),
      rxDrugPurchaseCount: Mock.Random.integer(0, 10),
      totalOrderCount: Mock.Random.integer(0, 50),
      totalAmount: Mock.Random.float(100, 10000, 0, 2),
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      lastLoginTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      recentOrders: [],
      recentPrescriptions: [],
      addresses: [
        { id: 1, name: Mock.Random.cname(), phone: Mock.mock('@phone'), province: '北京市', city: '北京市', district: '朝阳区', detail: Mock.Random.cparagraph(1) }
      ]
    }
  }))

  // 更新用户状态
  Mock.mock(/\/api\/admin\/users\/\d+\/status/, 'patch', () => ({
    code: 200,
    message: '成功',
    data: null
  }))

  // 实名认证审核
  Mock.mock(/\/api\/admin\/users\/\d+\/real-name-audit/, 'put', () => ({
    code: 200,
    message: '成功',
    data: null
  }))

  // 登录
  Mock.mock(/\/api\/admin\/auth\/login/, 'post', () => ({
    code: 200,
    message: '登录成功',
    data: {
      token: Mock.mock('@string("upper", 32)'),
      expiresIn: 86400,
      userInfo: {
        id: 1,
        username: 'admin',
        nickname: '系统管理员',
        avatar: Mock.Random.image('40x40', '#409EFF', '', 'png', 'admin'),
        roles: ['admin'],
        permissions: ['*:*:*']
      },
      roles: ['admin'],
      permissions: ['*:*:*']
    }
  }))

  // 获取用户信息
  Mock.mock(/\/api\/admin\/auth\/userinfo/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      id: '1',
      username: 'admin',
      nickname: '系统管理员',
      avatar: Mock.Random.image('40x40', '#409EFF', '', 'png', 'admin'),
      email: 'admin@drugmall.com',
      phone: '13800138000',
      status: 1,
      roles: ['admin'],
      permissions: ['*:*:*'],
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      lastLoginTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }))

  // 登出
  Mock.mock(/\/api\/admin\/auth\/logout/, 'post', () => ({
    code: 200,
    message: '登出成功',
    data: null
  }))

  // 实名认证列表
  Mock.mock(/\/api\/admin\/users\/auth\/list/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 35
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        userId: Mock.Random.integer(1000, 9999),
        nickname: Mock.Random.cname(),
        phone: Mock.mock('@phone'),
        realName: Mock.Random.cname(),
        idCard: Mock.mock('@id').replace(/^(.)(.*)(.{4})$/, '$1***********$3'),
        idCardFront: Mock.Random.image('200x120', '#F56C6C', '', 'png', '身份证正面'),
        idCardBack: Mock.Random.image('200x120', '#F56C6C', '', 'png', '身份证反面'),
        status: Mock.Random.pick([0, 1, 2]),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  // 认证统计
  Mock.mock(/\/api\/admin\/users\/auth\/stats/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      pending: Mock.Random.integer(5, 20),
      passed: Mock.Random.integer(100, 500),
      rejected: Mock.Random.integer(2, 15),
      total: Mock.Random.integer(120, 550)
    }
  }))

  // 审核认证
  Mock.mock(/\/api\/admin\/users\/auth\/\d+\/audit/, 'post', () => ({
    code: 200,
    message: '审核成功',
    data: null
  }))
}
