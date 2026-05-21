/**
 * 内容管理Mock数据
 */
import Mock from 'mockjs'

export function setupContentMock() {
  // 文章列表
  Mock.mock(/\/api\/admin\/content\/articles/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 88
    const list = []
    const titles = ['高血压用药指南', '糖尿病饮食建议', '感冒预防知识', '儿童用药注意事项', '抗生素使用误区', '维生素补充指南', '运动与关节健康', '孕期用药安全']

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        title: titles[Mock.Random.integer(0, titles.length - 1)],
        coverImage: Mock.Random.image('120x80', '#409EFF', '', 'png'),
        categoryId: Mock.Random.integer(1, 4),
        categoryName: Mock.Random.pick(['用药指南', '健康科普', '疾病百科', '营养保健']),
        author: Mock.Random.cname(),
        viewCount: Mock.Random.integer(100, 10000),
        status: Mock.Random.integer(0, 1),
        publishTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  // 文章详情/编辑
  Mock.mock(/\/api\/admin\/content\/articles\/\d+/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      id: Mock.Random.integer(1, 100),
      title: Mock.Random.ctitle(5, 15),
      coverImage: Mock.Random.image('300x200', '#409EFF', '', 'png'),
      content: '<p>文章内容...</p>',
      categoryId: 1,
      categoryName: '用药指南',
      author: Mock.Random.cname(),
      status: 1,
      viewCount: Mock.Random.integer(100, 10000),
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }))

  Mock.mock(/\/api\/admin\/content\/articles/, 'post', () => ({ code: 200, message: '创建成功', data: Mock.Random.integer(100, 999) }))
  Mock.mock(/\/api\/admin\/content\/articles\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/content\/articles\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))

  // Banner列表
  Mock.mock(/\/api\/admin\/content\/banners/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 12
    const list = []
    const titles = ['新品上线', '健康节大促', '处方药特惠', '保健品专区']

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        title: titles[Mock.Random.integer(0, titles.length - 1)],
        image: Mock.Random.image('600x200', '#409EFF', '', 'png', 'banner'),
        jumpUrl: 'https://example.com',
        sortOrder: Mock.Random.integer(0, 20),
        status: Mock.Random.integer(0, 1),
        startTime: Mock.Random.date('yyyy-MM-dd'),
        endTime: Mock.Random.date('yyyy-MM-dd'),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  Mock.mock(/\/api\/admin\/content\/banners/, 'post', () => ({ code: 200, message: '创建成功', data: Mock.Random.integer(1, 100) }))
  Mock.mock(/\/api\/admin\/content\/banners\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/content\/banners\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))

  // 通知列表
  Mock.mock(/\/api\/admin\/content\/notices/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 25
    const list = []
    const titles = ['系统维护通知', '活动规则更新', '隐私政策变更', '平台规则调整']

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        title: titles[Mock.Random.integer(0, titles.length - 1)],
        content: Mock.Random.cparagraph(3),
        type: Mock.Random.pick(['系统', '活动', '公告']),
        status: Mock.Random.integer(0, 1),
        publishTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  Mock.mock(/\/api\/admin\/content\/notices/, 'post', () => ({ code: 200, message: '创建成功', data: Mock.Random.integer(1, 100) }))
  Mock.mock(/\/api\/admin\/content\/notices\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/content\/notices\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))
}
