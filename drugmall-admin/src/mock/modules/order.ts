/**
 * 订单管理Mock数据
 */
import Mock from 'mockjs'

const orderStatuses = [
  { code: 0, label: '待付款', type: 'warning' },
  { code: 1, label: '待发货', type: 'primary' },
  { code: 2, label: '待收货', type: 'info' },
  { code: 3, label: '已完成', type: 'success' },
  { code: 4, label: '已取消', type: 'danger' },
  { code: 5, label: '退款中', type: 'warning' },
  { code: 6, label: '已退款', type: 'info' }
]

const productNames = ['阿莫西林胶囊', '布洛芬缓释胶囊', '复方氨酚烷胺片', '氯雷他定片', '奥美拉唑肠溶胶囊']

export function setupOrderMock() {
  // 订单列表
  Mock.mock(/\/api\/admin\/orders$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 520
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      const statusIdx = Mock.Random.integer(0, 4)
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        orderNo: 'ORD' + Mock.mock('@string("number", 14)'),
        userId: Mock.Random.integer(1000, 9999),
        userNickname: Mock.Random.cname(),
        userPhone: Mock.mock('@phone'),
        totalAmount: Mock.Random.float(20, 500, 0, 2),
        payAmount: Mock.Random.float(20, 500, 0, 2),
        payType: Mock.Random.pick(['微信支付', '支付宝', '银行卡']),
        payStatus: Mock.Random.integer(0, 1),
        payTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        status: statusIdx,
        statusText: orderStatuses[statusIdx].label,
        deliveryCompany: Mock.Random.pick(['顺丰速运', '中通快递', '圆通速递', '韵达快递']),
        deliveryNo: Mock.mock('@string("upper", 2)') + Mock.mock('@string("number", 12)'),
        deliveryTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        isRx: Mock.Random.boolean(),
        prescriptionNo: Mock.Random.boolean() ? 'RX' + Mock.mock('@string("number", 10)') : null,
        itemCount: Mock.Random.integer(1, 5),
        productNames: productNames.slice(0, Mock.Random.integer(1, 3)),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  // 订单详情
  Mock.mock(/\/api\/admin\/orders\/\d+/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      id: Mock.Random.id(),
      orderNo: 'ORD' + Mock.mock('@string("number", 14)'),
      userId: Mock.Random.integer(1000, 9999),
      userNickname: Mock.Random.cname(),
      userPhone: Mock.mock('@phone'),
      totalAmount: Mock.Random.float(20, 500, 0, 2),
      payAmount: Mock.Random.float(20, 500, 0, 2),
      freight: Mock.Random.float(0, 15, 0, 2),
      discountAmount: Mock.Random.float(0, 50, 0, 2),
      payType: Mock.Random.pick(['微信支付', '支付宝', '银行卡']),
      payStatus: 1,
      payTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      status: 3,
      statusText: '已完成',
      deliveryCompany: '顺丰速运',
      deliveryNo: 'SF' + Mock.mock('@string("number", 12)'),
      deliveryTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      receiveTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      isRx: true,
      prescriptionNo: 'RX' + Mock.mock('@string("number", 10)'),
      receiverName: Mock.Random.cname(),
      receiverPhone: Mock.mock('@phone'),
      receiverProvince: '北京市',
      receiverCity: '北京市',
      receiverDistrict: '朝阳区',
      receiverAddress: Mock.Random.cparagraph(1),
      items: [
        { id: 1, productId: 1, productName: productNames[0], specification: '12粒/盒', price: Mock.Random.float(10, 50, 0, 2), quantity: Mock.Random.integer(1, 3), amount: Mock.Random.float(10, 150, 0, 2), image: Mock.Random.image('60x60', '#67C23A', '', 'png') }
      ],
      operationLogs: [
        { id: 1, action: '订单创建', detail: '用户提交订单', operatorName: '系统', createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') },
        { id: 2, action: '订单支付', detail: '用户完成支付', operatorName: '系统', createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') },
        { id: 3, action: '订单发货', detail: '商家已发货', operatorName: '管理员', createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') },
        { id: 4, action: '订单完成', detail: '用户确认收货', operatorName: '系统', createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') }
      ],
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }))
}
