/**
 * 首页配置管理Mock数据
 */
import Mock from 'mockjs'

const tabNames = ['首页推荐', '药品专区', '健康养生', '医疗器械', '保健营养']
const sectionTypes = ['banner', 'kingkong', 'ad_slot', 'topic', 'product_flow', 'service_grid', 'notice', 'coupon']
const jumpTypes = ['url', 'route', 'miniapp', 'none']

export function setupHomeConfigMock() {
  // 全局配置
  Mock.mock(/\/api\/admin\/home\/config$/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      globalConfig: {
        pageTitle: 'DrugMall',
        bgColor: '#f5f5f5',
        moduleGap: 12,
        borderRadius: 8,
        primaryColor: '#409EFF',
        secondaryColor: '#67C23A',
        fontSizeSmall: 12,
        fontSizeMedium: 14,
        fontSizeLarge: 16
      },
      tabs: tabNames.map((name, i) => ({
        id: i + 1,
        tabId: `tab_${i + 1}`,
        name,
        icon: Mock.Random.image('40x40', '#409EFF', '', 'png', 'icon'),
        primaryColor: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399'][i],
        bgColor: '#ffffff',
        sectionIds: Array.from({ length: Mock.Random.integer(2, 5) }, () => Mock.Random.integer(1, 10)),
        sortOrder: i + 1,
        status: 1
      })),
      sections: Array.from({ length: 10 }, (_, i) => ({
        id: i + 1,
        name: `${sectionTypes[i % sectionTypes.length]}模块${i + 1}`,
        subtitle: '',
        sectionType: sectionTypes[i % sectionTypes.length],
        layout: 'vertical',
        bgColor: '#ffffff',
        borderRadius: 8,
        tabIds: [1],
        sortOrder: i + 1,
        status: 1,
        config: {},
        content: {}
      }))
    }
  }))

  Mock.mock(/\/api\/admin\/home\/config$/, 'put', () => ({ code: 200, message: '保存成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/config\/preview/, 'get', () => ({ code: 200, message: '成功', data: {} }))

  // Tab管理
  Mock.mock(/\/api\/admin\/home\/tabs$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = tabNames.length
    const list = tabNames.slice((pageNum - 1) * pageSize, pageNum * pageSize).map((name, i) => ({
      id: (pageNum - 1) * pageSize + i + 1,
      tabId: `tab_${(pageNum - 1) * pageSize + i + 1}`,
      name,
      icon: Mock.Random.image('40x40', '#409EFF', '', 'png', 'icon'),
      activeIcon: Mock.Random.image('40x40', '#67C23A', '', 'png', 'icon'),
      primaryColor: '#409EFF',
      bgColor: '#ffffff',
      sectionIds: [1, 2, 3],
      sortOrder: (pageNum - 1) * pageSize + i + 1,
      status: 1,
      sectionCount: 3
    }))
    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  Mock.mock(/\/api\/admin\/home\/tabs$/, 'post', () => ({ code: 200, message: '创建成功', data: Mock.Random.integer(100, 999) }))
  Mock.mock(/\/api\/admin\/home\/tabs\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/tabs\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/tabs\/sort/, 'put', () => ({ code: 200, message: '排序更新成功', data: null }))

  // 模块管理
  Mock.mock(/\/api\/admin\/home\/sections$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 20
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        name: `${sectionTypes[(i + pageNum) % sectionTypes.length]}模块${(pageNum - 1) * pageSize + i + 1}`,
        subtitle: Mock.Random.ctitle(3, 8),
        sectionType: sectionTypes[(i + pageNum) % sectionTypes.length],
        layout: Mock.Random.pick(['vertical', 'horizontal', 'grid']),
        bgColor: '#ffffff',
        borderRadius: 8,
        marginTop: 0,
        marginBottom: 0,
        marginLeft: 0,
        marginRight: 0,
        tabIds: [1],
        sortOrder: (pageNum - 1) * pageSize + i + 1,
        status: 1,
        config: {},
        content: {}
      })
    }

    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  Mock.mock(/\/api\/admin\/home\/sections$/, 'post', () => ({ code: 200, message: '创建成功', data: Mock.Random.integer(100, 999) }))
  Mock.mock(/\/api\/admin\/home\/sections\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/sections\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/sections\/\d+\/copy/, 'post', () => ({ code: 200, message: '复制成功', data: Mock.Random.integer(100, 999) }))

  // 轮播图管理
  Mock.mock(/\/api\/admin\/home\/banners$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 8
    const titles = ['新春健康大促', '处方药特惠专区', '保健品满减', '医疗器械新品', '慢病管理计划']
    const list = titles.slice((pageNum - 1) * pageSize, pageNum * pageSize).map((title, i) => ({
      id: (pageNum - 1) * pageSize + i + 1,
      title,
      image: Mock.Random.image('600x250', '#409EFF', '', 'png', 'banner'),
      sectionId: 1,
      sectionName: '轮播图模块',
      jumpType: jumpTypes[Mock.Random.integer(0, 3)],
      jumpUrl: 'https://example.com',
      startTime: Mock.Random.date('yyyy-MM-dd'),
      endTime: Mock.Random.date('yyyy-MM-dd'),
      sortOrder: (pageNum - 1) * pageSize + i + 1,
      status: 1
    }))
    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  Mock.mock(/\/api\/admin\/home\/banners$/, 'post', () => ({ code: 200, message: '创建成功', data: Mock.Random.integer(100, 999) }))
  Mock.mock(/\/api\/admin\/home\/banners\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/banners\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/banners\/sort/, 'put', () => ({ code: 200, message: '排序更新成功', data: null }))

  // 金刚位管理
  Mock.mock(/\/api\/admin\/home\/kingkongs$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 12
    const names = ['处方购药', 'OTC专区', '保健品', '医疗器械', '名医问诊', '用药咨询', '健康档案', '慢病管理', '会员中心', '积分商城', '新手 guide', '客服中心']
    const list = names.slice((pageNum - 1) * pageSize, pageNum * pageSize).map((name, i) => ({
      id: (pageNum - 1) * pageSize + i + 1,
      name,
      icon: Mock.Random.image('60x60', '#409EFF', '', 'png', 'icon'),
      iconType: Mock.Random.pick(['image', 'icon']),
      sectionId: 2,
      sectionName: '金刚位模块',
      jumpType: jumpTypes[Mock.Random.integer(0, 3)],
      jumpUrl: 'https://example.com',
      badge: Mock.Random.boolean() ? 'HOT' : undefined,
      badgeColor: Mock.Random.boolean() ? '#F56C6C' : undefined,
      sortOrder: (pageNum - 1) * pageSize + i + 1,
      status: 1
    }))
    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  Mock.mock(/\/api\/admin\/home\/kingkongs$/, 'post', () => ({ code: 200, message: '创建成功', data: Mock.Random.integer(100, 999) }))
  Mock.mock(/\/api\/admin\/home\/kingkongs\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/kingkongs\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))

  // 广告位管理
  Mock.mock(/\/api\/admin\/home\/ads$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 6
    const names = ['顶部通栏广告', '中部双栏广告', '底部单栏广告', '侧边栏广告', '悬浮广告', '弹窗广告']
    const list = names.slice((pageNum - 1) * pageSize, pageNum * pageSize).map((name, i) => ({
      id: (pageNum - 1) * pageSize + i + 1,
      name,
      image: Mock.Random.image('300x150', '#67C23A', '', 'png', 'ad'),
      sectionId: 3,
      sectionName: '广告位模块',
      layout: Mock.Random.pick(['single', 'double', 'triple', 'grid']),
      jumpType: jumpTypes[Mock.Random.integer(0, 3)],
      jumpUrl: 'https://example.com',
      width: 300,
      height: 150,
      startTime: Mock.Random.date('yyyy-MM-dd'),
      endTime: Mock.Random.date('yyyy-MM-dd'),
      sortOrder: (pageNum - 1) * pageSize + i + 1,
      status: 1
    }))
    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  Mock.mock(/\/api\/admin\/home\/ads$/, 'post', () => ({ code: 200, message: '创建成功', data: Mock.Random.integer(100, 999) }))
  Mock.mock(/\/api\/admin\/home\/ads\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/ads\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))

  // 专题管理
  Mock.mock(/\/api\/admin\/home\/topics$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 5
    const titles = ['春季过敏专题', '慢病管理专题', '家庭常备药专题', '儿童用药专题', '养生保健专题']
    const list = titles.slice((pageNum - 1) * pageSize, pageNum * pageSize).map((title, i) => ({
      id: (pageNum - 1) * pageSize + i + 1,
      title,
      subtitle: '精选专题推荐',
      sectionId: 4,
      sectionName: '专题模块',
      layout: Mock.Random.pick(['card', 'list', 'grid']),
      showTitle: true,
      showMore: true,
      moreUrl: '/topics',
      itemCount: Mock.Random.integer(4, 10),
      status: 1
    }))
    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  Mock.mock(/\/api\/admin\/home\/topics$/, 'post', () => ({ code: 200, message: '创建成功', data: Mock.Random.integer(100, 999) }))
  Mock.mock(/\/api\/admin\/home\/topics\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/topics\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))

  // 专题项
  Mock.mock(/\/api\/admin\/home\/topics\/\d+\/items/, 'get', () => {
    const items = Array.from({ length: 6 }, (_, i) => ({
      id: i + 1,
      topicId: 1,
      title: Mock.Random.ctitle(4, 10),
      subtitle: Mock.Random.ctitle(3, 8),
      image: Mock.Random.image('200x150', '#409EFF', '', 'png', 'topic'),
      tag: Mock.Random.pick(['热门', '新品', '特惠']),
      tagColor: '#F56C6C',
      jumpType: jumpTypes[Mock.Random.integer(0, 3)],
      jumpUrl: 'https://example.com',
      sortOrder: i + 1
    }))
    return { code: 200, message: '成功', data: items }
  })

  Mock.mock(/\/api\/admin\/home\/topics\/\d+\/items$/, 'post', () => ({ code: 200, message: '创建成功', data: Mock.Random.integer(100, 999) }))
  Mock.mock(/\/api\/admin\/home\/topics\/\d+\/items\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/topics\/\d+\/items\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))

  // 发布管理
  Mock.mock(/\/api\/admin\/home\/releases$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 10
    const list = []
    const statuses = ['draft', 'published', 'published', 'published', 'rollbacked']

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      const status = statuses[Mock.Random.integer(0, statuses.length - 1)]
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        version: `v1.${pageNum + i}.0`,
        description: Mock.Random.cparagraph(1),
        status,
        createdBy: Mock.Random.cname(),
        createdTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        publishedTime: status === 'published' ? Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') : undefined
      })
    }

    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  Mock.mock(/\/api\/admin\/home\/releases$/, 'post', () => ({ code: 200, message: '发布成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/releases\/\d+\/rollback/, 'post', () => ({ code: 200, message: '回滚成功', data: null }))
  Mock.mock(/\/api\/admin\/home\/releases\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))

  Mock.mock(/\/api\/admin\/home\/releases\/summary/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      publishedCount: Mock.Random.integer(5, 15),
      draftCount: Mock.Random.integer(1, 5),
      lastPublishedVersion: 'v1.5.0',
      lastPublishedTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      totalChanges: Mock.Random.integer(5, 30)
    }
  }))
}
