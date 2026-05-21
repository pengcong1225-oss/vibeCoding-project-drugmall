/**
 * 财务管理Mock数据
 */
import Mock from 'mockjs'

export function setupFinanceMock() {
  // 财务统计
  Mock.mock(/\/api\/admin\/finance\/stats/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      totalRevenue: Mock.Random.float(500000, 5000000, 0, 2),
      monthRevenue: Mock.Random.float(50000, 300000, 0, 2),
      todayRevenue: Mock.Random.float(5000, 50000, 0, 2),
      totalRefund: Mock.Random.float(10000, 100000, 0, 2),
      monthRefund: Mock.Random.float(2000, 20000, 0, 2),
      totalDoctorCommission: Mock.Random.float(50000, 200000, 0, 2),
      pendingSettlement: Mock.Random.float(10000, 50000, 0, 2),
      totalWithdrawal: Mock.Random.float(30000, 150000, 0, 2)
    }
  }))

  // 交易记录
  Mock.mock(/\/api\/admin\/finance\/transactions/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 890
    const list = []

    const types = ['订单收入', '退款', '医生佣金', '平台服务费', '提现']

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        transactionNo: 'TXN' + Mock.mock('@string("number", 14)'),
        type: types[Mock.Random.integer(0, types.length - 1)],
        relatedOrderNo: 'ORD' + Mock.mock('@string("number", 14)'),
        amount: Mock.Random.float(10, 500, 0, 2),
        status: Mock.Random.pick(['成功', '处理中', '失败']),
        description: Mock.Random.cparagraph(1),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  // 提现申请列表
  Mock.mock(/\/api\/admin\/finance\/withdrawals/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 68
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        withdrawalNo: 'W' + Mock.mock('@string("number", 10)'),
        applicantType: Mock.Random.pick(['医生', '药师', '门店']),
        applicantName: Mock.Random.cname(),
        amount: Mock.Random.float(100, 10000, 0, 2),
        bankName: Mock.Random.pick(['工商银行', '建设银行', '农业银行', '招商银行']),
        bankAccount: Mock.mock('@string("number", 19)'),
        accountName: Mock.Random.cname(),
        status: Mock.Random.pick(['待审核', '审核通过', '已打款', '已驳回']),
        auditOpinion: '',
        auditTime: '',
        auditorName: '',
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  // 审核提现
  Mock.mock(/\/api\/admin\/finance\/withdrawals\/\d+\/audit/, 'put', () => ({
    code: 200,
    message: '成功',
    data: null
  }))
}
