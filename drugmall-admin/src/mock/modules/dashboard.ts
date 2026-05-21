/**
 * 仪表盘Mock数据
 */
import Mock from 'mockjs'

export function setupDashboardMock() {
  // 仪表盘概览
  Mock.mock(/\/api\/admin\/dashboard\/overview/, 'get', () => {
    return {
      code: 200,
      message: '成功',
      data: {
        cards: [
          { title: '今日GMV', value: '¥' + Mock.Random.float(10000, 80000, 0, 2), change: '+12.5%', trend: 'up', icon: 'Money', color: '#52c41a' },
          { title: '今日订单', value: Mock.Random.integer(50, 300), change: '+8.3%', trend: 'up', icon: 'ShoppingCart', color: '#1890ff' },
          { title: '新增用户', value: Mock.Random.integer(20, 100), change: '-2.1%', trend: 'down', icon: 'User', color: '#722ed1' },
          { title: '问诊量', value: Mock.Random.integer(30, 150), change: '+15.6%', trend: 'up', icon: 'ChatDotRound', color: '#fa541c' }
        ],
        categoryData: [
          { name: '处方药', value: Mock.Random.integer(30, 50), color: '#52c41a' },
          { name: 'OTC药品', value: Mock.Random.integer(20, 35), color: '#1890ff' },
          { name: '保健品', value: Mock.Random.integer(10, 25), color: '#faad14' },
          { name: '医疗器械', value: Mock.Random.integer(5, 15), color: '#722ed1' },
          { name: '其他', value: Mock.Random.integer(3, 10), color: '#13c2c2' }
        ]
      }
    }
  })

  // GMV趋势
  Mock.mock(/\/api\/admin\/dashboard\/gmv-trend/, 'get', () => {
    const dates = []
    const gmv = []
    const orders = []
    const count = 7
    for (let i = count - 1; i >= 0; i--) {
      const d = new Date()
      d.setDate(d.getDate() - i)
      dates.push(`${d.getMonth() + 1}-${d.getDate()}`)
      gmv.push(Number(Mock.Random.float(10000, 80000, 2, 2)))
      orders.push(Mock.Random.integer(30, 200))
    }
    return {
      code: 200,
      message: '成功',
      data: { dates, gmv, orders }
    }
  })

  // 订单来源
  Mock.mock(/\/api\/admin\/dashboard\/order-source/, 'get', () => {
    return {
      code: 200,
      message: '成功',
      data: [
        { name: 'APP', value: Mock.Random.integer(300, 600) },
        { name: '小程序', value: Mock.Random.integer(200, 500) },
        { name: 'H5', value: Mock.Random.integer(100, 300) },
        { name: 'PC端', value: Mock.Random.integer(50, 150) }
      ]
    }
  })

  // 兼容旧路径
  Mock.mock(/\/api\/admin\/dashboard\/stats/, 'get', () => ({
    code: 200, message: '成功',
    data: {
      totalUsers: Mock.Random.integer(5000, 15000),
      todayUsers: Mock.Random.integer(100, 500),
      totalProducts: Mock.Random.integer(200, 800),
      newProducts: Mock.Random.integer(5, 30),
      todayOrders: Mock.Random.integer(50, 200),
      totalOrders: Mock.Random.integer(10000, 50000),
      todaySales: Mock.Random.float(5000, 50000, 0, 2),
      totalSales: Mock.Random.float(500000, 5000000, 0, 2)
    }
  }))

  Mock.mock(/\/api\/admin\/dashboard\/sales-trend/, 'get', () => {
    const dates: string[] = []
    const salesData: number[] = []
    const orderData: number[] = []
    for (let i = 6; i >= 0; i--) {
      const d = new Date()
      d.setDate(d.getDate() - i)
      dates.push(`${d.getMonth() + 1}-${d.getDate()}`)
      salesData.push(Number(Mock.Random.float(10000, 80000, 0, 2)))
      orderData.push(Mock.Random.integer(50, 300))
    }
    return { code: 200, message: '成功', data: { dates, salesData, orderData } }
  })

  Mock.mock(/\/api\/admin\/dashboard\/category-sales/, 'get', () => ({
    code: 200, message: '成功',
    data: [
      { name: '处方药', value: Mock.Random.integer(300, 500) },
      { name: 'OTC药品', value: Mock.Random.integer(200, 400) },
      { name: '保健品', value: Mock.Random.integer(100, 300) },
      { name: '医疗器械', value: Mock.Random.integer(50, 200) }
    ]
  }))
}
