/**
 * 药品管理Mock数据
 */
import Mock from 'mockjs'

const drugNames = ['阿莫西林胶囊', '头孢克肟片', '布洛芬缓释胶囊', '复方氨酚烷胺片', '氯雷他定片', '奥美拉唑肠溶胶囊', '盐酸二甲双胍片', '阿托伐他汀钙片', '硝苯地平控释片', '蒙脱石散', '乳酸菌素片', '健胃消食片', '维生素C片', '葡萄糖酸钙口服液', '复方甘草片']
const manufacturers = ['哈药集团', '华北制药', '石药集团', '扬子江药业', '恒瑞医药', '白云山制药', '修正药业', '同仁堂', '云南白药', '三九药业']

export function setupDrugMock() {
  // 药品列表
  Mock.mock(/\/api\/admin\/products$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 328
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        productCode: 'DM' + Mock.mock('@string("number", 8)'),
        productName: drugNames[Mock.Random.integer(0, drugNames.length - 1)],
        categoryId: Mock.Random.integer(1, 5),
        categoryName: ['处方药', 'OTC-甲类', 'OTC-乙类', '保健品', '医疗器械'][Mock.Random.integer(0, 4)],
        mainImage: Mock.Random.image('100x100', '#67C23A', '', 'png', 'drug'),
        price: Mock.Random.float(5, 200, 0, 2),
        originalPrice: Mock.Random.float(10, 250, 0, 2),
        stock: Mock.Random.integer(0, 1000),
        warningStock: 10,
        isRx: Mock.Random.integer(0, 1),
        approvalNumber: '国药准字H' + Mock.mock('@string("number", 8)'),
        manufacturer: manufacturers[Mock.Random.integer(0, manufacturers.length - 1)],
        spec: Mock.Random.pick(['12粒/盒', '24片/盒', '100ml/瓶', '0.5g*6袋/盒', '20片/盒']),
        unit: '盒',
        expiryDate: Mock.Random.date('yyyy-MM-dd'),
        status: Mock.Random.integer(0, 1),
        sortOrder: Mock.Random.integer(0, 100),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        updateTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  // 药品详情
  Mock.mock(/\/api\/admin\/products\/\d+/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      id: Mock.Random.id(),
      productCode: 'DM' + Mock.mock('@string("number", 8)'),
      productName: drugNames[Mock.Random.integer(0, drugNames.length - 1)],
      categoryId: 1,
      categoryName: '处方药',
      brandId: 1,
      brandName: '品牌A',
      mainImage: Mock.Random.image('300x300', '#67C23A', '', 'png', 'drug'),
      images: [Mock.Random.image('300x300', '#67C23A', '', 'png'), Mock.Random.image('300x300', '#409EFF', '', 'png')],
      detail: '<p>药品详细说明</p>',
      price: Mock.Random.float(5, 200, 0, 2),
      originalPrice: Mock.Random.float(10, 250, 0, 2),
      costPrice: Mock.Random.float(3, 100, 0, 2),
      stock: Mock.Random.integer(0, 1000),
      warningStock: 10,
      isRx: Mock.Random.integer(0, 1),
      approvalNumber: '国药准字H' + Mock.mock('@string("number", 8)'),
      manufacturer: manufacturers[Mock.Random.integer(0, manufacturers.length - 1)],
      spec: Mock.Random.pick(['12粒/盒', '24片/盒', '100ml/瓶']),
      unit: '盒',
      expiryDate: Mock.Random.date('yyyy-MM-dd'),
      batchNumber: 'B' + Mock.mock('@string("number", 10)'),
      indications: '适用于感冒发热、头痛等症状',
      usage: '口服，一次1-2粒，一日3次',
      contraindications: '对本品过敏者禁用',
      status: 1,
      sortOrder: 0,
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      updateTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }))

  // 药品分类列表
  Mock.mock(/\/api\/admin\/categories$/, 'get', () => {
    const categories = [
      { id: 1, name: '处方药', parentId: 0, icon: '', sortOrder: 1, status: 1, drugCount: 120 },
      { id: 2, name: 'OTC药品', parentId: 0, icon: '', sortOrder: 2, status: 1, drugCount: 85 },
      { id: 3, name: '保健品', parentId: 0, icon: '', sortOrder: 3, status: 1, drugCount: 45 },
      { id: 4, name: '医疗器械', parentId: 0, icon: '', sortOrder: 4, status: 1, drugCount: 30 },
      { id: 5, name: '中药饮片', parentId: 0, icon: '', sortOrder: 5, status: 1, drugCount: 48 }
    ]
    return {
      code: 200,
      message: '成功',
      data: categories
    }
  })

  // 药品品牌列表
  Mock.mock(/\/api\/admin\/brands$/, 'get', () => {
    const brands = manufacturers.map((name, i) => ({
      id: i + 1,
      name,
      logo: Mock.Random.image('80x80', '#409EFF', '', 'png', 'logo'),
      description: `${name}品牌简介`,
      sortOrder: i + 1,
      status: 1,
      drugCount: Mock.Random.integer(5, 50),
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }))
    return {
      code: 200,
      message: '成功',
      data: { list: brands, total: brands.length, pageNum: 1, pageSize: 20 }
    }
  })

  // 创建药品
  Mock.mock(/\/api\/admin\/products/, 'post', () => ({
    code: 200,
    message: '创建成功',
    data: Mock.mock('@string("number", 8)')
  }))

  // 更新药品
  Mock.mock(/\/api\/admin\/products\/\d+/, 'put', () => ({
    code: 200,
    message: '更新成功',
    data: null
  }))

  // 删除药品
  Mock.mock(/\/api\/admin\/products\/\d+/, 'delete', () => ({
    code: 200,
    message: '删除成功',
    data: null
  }))
}
