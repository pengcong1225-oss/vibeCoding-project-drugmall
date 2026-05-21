/**
 * 门店管理Mock数据
 */
import Mock from 'mockjs'

const storeNames = ['国大药房北京朝阳店', '同仁堂王府井店', '大参林广州天河店', '老百姓大药房长沙店', '益丰大药房武汉店', '一心堂昆明店', '海王星辰深圳南山店', '健之佳成都武侯店']
const cities = ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '长沙', '昆明', '南京']
const drugNames = ['阿莫西林胶囊', '布洛芬缓释胶囊', '复方氨酚烷胺片', '氯雷他定片', '奥美拉唑肠溶胶囊']

export function setupStoreMock() {
  // 门店列表
  Mock.mock(/\/api\/admin\/stores$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 86
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        name: storeNames[Mock.Random.integer(0, storeNames.length - 1)],
        address: cities[Mock.Random.integer(0, cities.length - 1)] + Mock.Random.cparagraph(1),
        contact: Mock.Random.cname(),
        phone: Mock.mock('@phone'),
        province: '某省',
        city: cities[Mock.Random.integer(0, cities.length - 1)],
        district: '某区',
        licenseNo: '药监许' + Mock.mock('@string("number", 10)'),
        licenseStatus: Mock.Random.pick([0, 1, 2, 2, 2, 3]),
        status: Mock.Random.pick([0, 1, 1, 1, 2, 3]),
        joinTime: Mock.Random.date('yyyy-MM-dd'),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  // 门店详情
  Mock.mock(/\/api\/admin\/stores\/\d+$/, 'get', () => {
    const recentDrugs = Array.from({ length: 5 }, (_, i) => ({
      id: i + 1,
      storeId: 1,
      drugId: Mock.Random.integer(1, 100),
      drugName: drugNames[Mock.Random.integer(0, drugNames.length - 1)],
      specification: '12粒/盒',
      manufacturer: '某制药厂',
      stock: Mock.Random.integer(0, 500),
      price: Mock.Random.float(5, 100, 0, 2),
      status: Mock.Random.integer(0, 1),
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }))

    return {
      code: 200,
      message: '成功',
      data: {
        id: Mock.Random.integer(1, 100),
        name: storeNames[0],
        address: '北京市朝阳区xxx路xxx号',
        contact: Mock.Random.cname(),
        phone: Mock.mock('@phone'),
        province: '北京市',
        city: '北京市',
        district: '朝阳区',
        latitude: 39.9,
        longitude: 116.4,
        licenseNo: '药监许' + Mock.mock('@string("number", 10)'),
        licenseStatus: 2,
        status: 1,
        businessHours: '08:00-22:00',
        deliveryRange: 5,
        deliveryFee: 5,
        minOrderAmount: 29,
        storeImages: [Mock.Random.image('200x150', '#409EFF', '', 'png', '门店1'), Mock.Random.image('200x150', '#67C23A', '', 'png', '门店2')],
        introduction: Mock.Random.cparagraph(2),
        auditMaterials: {
          businessLicense: Mock.Random.image('200x120', '#409EFF', '', 'png', '营业执照'),
          drugLicense: Mock.Random.image('200x120', '#67C23A', '', 'png', '药品经营许可证'),
          gspCert: Mock.Random.image('200x120', '#E6A23C', '', 'png', 'GSP认证'),
          legalIdFront: Mock.Random.image('200x120', '#F56C6C', '', 'png', '法人身份证正面'),
          legalIdBack: Mock.Random.image('200x120', '#F56C6C', '', 'png', '法人身份证反面'),
          storePhoto: Mock.Random.image('200x150', '#909399', '', 'png', '门店照片')
        },
        stats: {
          totalOrders: Mock.Random.integer(100, 2000),
          totalSales: Mock.Random.float(50000, 500000, 0, 2),
          monthOrders: Mock.Random.integer(20, 200),
          monthSales: Mock.Random.float(10000, 100000, 0, 2),
          drugCount: Mock.Random.integer(50, 300),
          rating: Mock.Random.float(3.5, 5, 1, 1)
        },
        recentDrugs,
        joinTime: Mock.Random.date('yyyy-MM-dd'),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      }
    }
  })

  // 门店审核列表
  Mock.mock(/\/api\/admin\/stores\/audit/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 12
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        storeId: Mock.Random.integer(100, 999),
        storeName: storeNames[Mock.Random.integer(0, storeNames.length - 1)],
        contact: Mock.Random.cname(),
        phone: Mock.mock('@phone'),
        status: Mock.Random.pick([0, 0, 1, 2]),
        submitTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        auditOpinion: '',
        auditTime: '',
        auditorName: ''
      })
    }

    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  // 审核操作
  Mock.mock(/\/api\/admin\/stores\/\d+\/audit/, 'put', () => ({ code: 200, message: '审核完成', data: null }))

  // 门店药品列表
  Mock.mock(/\/api\/admin\/stores\/\d+\/drugs/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 50
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        storeId: 1,
        drugId: Mock.Random.integer(1, 100),
        drugName: drugNames[Mock.Random.integer(0, drugNames.length - 1)],
        specification: '12粒/盒',
        manufacturer: '某制药厂',
        stock: Mock.Random.integer(0, 500),
        price: Mock.Random.float(5, 100, 0, 2),
        status: Mock.Random.pick([0, 1, 1]),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  Mock.mock(/\/api\/admin\/stores\/\d+\/drugs$/, 'post', () => ({ code: 200, message: '添加成功', data: null }))
  Mock.mock(/\/api\/admin\/stores\/\d+\/drugs\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/stores\/\d+\/drugs\/\d+/, 'delete', () => ({ code: 200, message: '移除成功', data: null }))
}
