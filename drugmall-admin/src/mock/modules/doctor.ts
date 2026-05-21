/**
 * 医生管理Mock数据
 */
import Mock from 'mockjs'

const doctorNames = ['张伟', '李芳', '王强', '刘敏', '陈磊', '杨雪', '赵刚', '黄丽', '周明', '吴静', '郑凯', '孙悦', '马超', '朱婷', '胡军']
const departments = ['内科', '外科', '儿科', '妇产科', '皮肤科', '中医科', '眼科', '耳鼻喉科', '骨科', '心血管科']
const titles = ['主任医师', '副主任医师', '主治医师', '住院医师']
const hospitals = ['北京协和医院', '北京大学第一医院', '北京朝阳医院', '北京同仁医院', '北京积水潭医院', '北京阜外医院']
const diagnosisNames = ['上呼吸道感染', '急性支气管炎', '高血压', '糖尿病', '胃炎', '过敏性鼻炎', '湿疹', '颈椎病', '腰椎间盘突出', '冠心病']
const symptomNames = ['头痛发热', '咳嗽咳痰', '胸闷气短', '腹痛腹泻', '皮肤瘙痒', '关节疼痛', '头晕乏力', '失眠多梦']

export function setupDoctorMock() {
  // 医生列表
  Mock.mock(/\/api\/admin\/doctors$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 128
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      const status = Mock.Random.pick([0, 1, 1, 1, 1, 2, 3])
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        name: doctorNames[Mock.Random.integer(0, doctorNames.length - 1)],
        phone: Mock.mock('@phone'),
        avatar: Mock.Random.image('80x80', '#409EFF', '', 'png', 'doctor'),
        gender: Mock.Random.integer(0, 1),
        age: Mock.Random.integer(28, 65),
        departmentId: Mock.Random.integer(1, departments.length),
        departmentName: departments[Mock.Random.integer(0, departments.length - 1)],
        title: titles[Mock.Random.integer(0, titles.length - 1)],
        hospital: hospitals[Mock.Random.integer(0, hospitals.length - 1)],
        rating: Mock.Random.float(3, 5, 1, 1),
        serviceCount: Mock.Random.integer(10, 500),
        responseTime: Mock.Random.integer(1, 30),
        status,
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

  // 医生详情
  Mock.mock(/\/api\/admin\/doctors\/\d+$/, 'get', () => {
    const recentConsultations = Array.from({ length: 3 }, (_, i) => ({
      id: i + 1,
      consultationNo: 'C' + Mock.mock('@string("number", 10)'),
      patientName: Mock.Random.cname(),
      type: Mock.Random.pick(['图文问诊', '视频问诊', '电话问诊']),
      status: Mock.Random.pick(['completed', 'pending']),
      statusText: Mock.Random.pick(['已完成', '进行中']),
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }))

    const recentPrescriptions = Array.from({ length: 3 }, (_, i) => ({
      id: i + 1,
      prescriptionNo: 'RX' + Mock.mock('@string("number", 10)'),
      diagnosis: diagnosisNames[Mock.Random.integer(0, diagnosisNames.length - 1)],
      drugCount: Mock.Random.integer(1, 5),
      totalAmount: Mock.Random.float(20, 300, 0, 2),
      status: Mock.Random.pick(['approved', 'pending']),
      statusText: Mock.Random.pick(['审核通过', '待审核']),
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }))

    const recentReviews = Array.from({ length: 3 }, (_, i) => ({
      id: i + 1,
      doctorId: 1,
      doctorName: doctorNames[0],
      userId: Mock.Random.integer(1000, 9999),
      patientNickname: '用户***' + Mock.mock('@string("number", 3)'),
      consultationId: Mock.Random.integer(1, 100),
      consultationType: Mock.Random.pick(['图文问诊', '视频问诊']),
      rating: Mock.Random.integer(3, 5),
      content: Mock.Random.cparagraph(1),
      reply: Mock.Random.boolean() ? '感谢您的评价，祝您早日康复！' : undefined,
      replyTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      status: 1,
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }))

    const recentSchedules = Array.from({ length: 5 }, (_, i) => {
      const d = new Date()
      d.setDate(d.getDate() + i)
      return {
        id: i + 1,
        doctorId: 1,
        date: `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`,
        morningEnabled: Mock.Random.boolean(),
        morningMax: Mock.Random.integer(5, 20),
        afternoonEnabled: Mock.Random.boolean(),
        afternoonMax: Mock.Random.integer(5, 20),
        eveningEnabled: Mock.Random.boolean(),
        eveningMax: Mock.Random.integer(5, 15),
        consultationTypes: Mock.Random.pick([['text'], ['text', 'video'], ['text', 'phone'], ['text', 'video', 'phone']]),
        status: Mock.Random.pick([1, 1, 1, 0])
      }
    })

    return {
      code: 200,
      message: '成功',
      data: {
        id: Mock.Random.id(),
        name: doctorNames[Mock.Random.integer(0, doctorNames.length - 1)],
        phone: Mock.mock('@phone'),
        avatar: Mock.Random.image('120x120', '#409EFF', '', 'png', 'doctor'),
        gender: Mock.Random.integer(0, 1),
        age: Mock.Random.integer(28, 65),
        departmentId: 1,
        departmentName: departments[Mock.Random.integer(0, departments.length - 1)],
        title: titles[Mock.Random.integer(0, titles.length - 1)],
        hospital: hospitals[Mock.Random.integer(0, hospitals.length - 1)],
        rating: Mock.Random.float(3.5, 5, 1, 1),
        serviceCount: Mock.Random.integer(100, 800),
        responseTime: Mock.Random.integer(2, 20),
        status: 1,
        joinTime: Mock.Random.date('yyyy-MM-dd'),
        certificateNo: '医师证' + Mock.mock('@string("number", 12)'),
        licenseNo: '执业证' + Mock.mock('@string("number", 12)'),
        practiceScope: '内科专业',
        workYears: Mock.Random.integer(3, 30),
        introduction: Mock.Random.cparagraph(2),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        auditMaterials: {
          idCardFront: Mock.Random.image('200x120', '#F56C6C', '', 'png', '身份证正面'),
          idCardBack: Mock.Random.image('200x120', '#F56C6C', '', 'png', '身份证反面'),
          certificate: Mock.Random.image('200x120', '#67C23A', '', 'png', '资格证'),
          license: Mock.Random.image('200x120', '#409EFF', '', 'png', '执业证'),
          titleCertificate: Mock.Random.image('200x120', '#E6A23C', '', 'png', '职称证'),
          workProof: Mock.Random.image('200x120', '#909399', '', 'png', '在职证明'),
          avatar: Mock.Random.image('120x120', '#409EFF', '', 'png', '医生头像')
        },
        stats: {
          totalConsultations: Mock.Random.integer(200, 2000),
          avgRating: Mock.Random.float(4, 5, 1, 1),
          avgResponseTime: Mock.Random.integer(2, 15),
          monthIncome: Mock.Random.float(5000, 30000, 0, 2),
          positiveRate: Mock.Random.float(85, 100, 1, 1)
        },
        recentConsultations: recentConsultations,
        recentPrescriptions: recentPrescriptions,
        recentReviews: recentReviews,
        recentSchedules: recentSchedules
      }
    }
  })

  // 入驻审核列表
  Mock.mock(/\/api\/admin\/doctors\/audit/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 18
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        doctorId: Mock.Random.integer(1000, 9999),
        name: doctorNames[Mock.Random.integer(0, doctorNames.length - 1)],
        phone: Mock.mock('@phone'),
        departmentId: Mock.Random.integer(1, departments.length),
        departmentName: departments[Mock.Random.integer(0, departments.length - 1)],
        title: titles[Mock.Random.integer(0, titles.length - 1)],
        hospital: hospitals[Mock.Random.integer(0, hospitals.length - 1)],
        status: Mock.Random.pick([0, 0, 1, 2, 3]),
        submitTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        auditOpinion: '',
        auditTime: '',
        auditorName: ''
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  // 审核详情
  Mock.mock(/\/api\/admin\/doctors\/audit\/\d+/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      id: Mock.Random.id(),
      doctorId: Mock.Random.integer(1000, 9999),
      name: Mock.Random.cname(),
      gender: Mock.Random.integer(0, 1),
      age: Mock.Random.integer(28, 65),
      phone: Mock.mock('@phone'),
      email: Mock.mock('@email'),
      idCard: Mock.mock('@id'),
      departmentId: 1,
      departmentName: departments[Mock.Random.integer(0, departments.length - 1)],
      title: titles[Mock.Random.integer(0, titles.length - 1)],
      hospital: hospitals[Mock.Random.integer(0, hospitals.length - 1)],
      certificateNo: '医师证' + Mock.mock('@string("number", 12)'),
      licenseNo: '执业证' + Mock.mock('@string("number", 12)'),
      practiceScope: '内科专业',
      workYears: Mock.Random.integer(3, 30),
      introduction: Mock.Random.cparagraph(2),
      status: 0,
      submitTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      auditMaterials: {
        idCardFront: Mock.Random.image('200x120', '#F56C6C', '', 'png', '身份证正面'),
        idCardBack: Mock.Random.image('200x120', '#F56C6C', '', 'png', '身份证反面'),
        certificate: Mock.Random.image('200x120', '#67C23A', '', 'png', '资格证'),
        license: Mock.Random.image('200x120', '#409EFF', '', 'png', '执业证'),
        titleCertificate: Mock.Random.image('200x120', '#E6A23C', '', 'png', '职称证'),
        workProof: Mock.Random.image('200x120', '#909399', '', 'png', '在职证明'),
        avatar: Mock.Random.image('120x120', '#409EFF', '', 'png', '医生头像')
      }
    }
  }))

  // 审核操作
  Mock.mock(/\/api\/admin\/doctors\/audit\/\d+$/, 'put', () => ({
    code: 200,
    message: '审核完成',
    data: null
  }))

  // 审核记录
  Mock.mock(/\/api\/admin\/doctors\/audit\/\d+\/logs/, 'get', () => ({
    code: 200,
    message: '成功',
    data: [
      { id: 1, auditorName: '系统管理员', auditTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'), result: '通过', opinion: '资质材料齐全，审核通过' }
    ]
  }))

  // 排班列表
  Mock.mock(/\/api\/admin\/doctors\/\d+\/schedule/, 'get', () => {
    const schedules = Array.from({ length: 7 }, (_, i) => {
      const d = new Date()
      d.setDate(d.getDate() + i)
      return {
        id: i + 1,
        doctorId: 1,
        date: `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`,
        morningEnabled: Mock.Random.boolean(),
        morningMax: Mock.Random.integer(5, 20),
        afternoonEnabled: Mock.Random.boolean(),
        afternoonMax: Mock.Random.integer(5, 20),
        eveningEnabled: Mock.Random.boolean(),
        eveningMax: Mock.Random.integer(5, 15),
        consultationTypes: Mock.Random.pick([['text'], ['text', 'video'], ['text', 'phone'], ['text', 'video', 'phone']]),
        status: Mock.Random.pick([1, 1, 0])
      }
    })
    return {
      code: 200,
      message: '成功',
      data: schedules
    }
  })

  // 创建排班
  Mock.mock(/\/api\/admin\/doctors\/\d+\/schedule$/, 'post', () => ({ code: 200, message: '创建成功', data: null }))

  // 更新排班
  Mock.mock(/\/api\/admin\/doctors\/\d+\/schedule\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))

  // 删除排班
  Mock.mock(/\/api\/admin\/doctors\/\d+\/schedule\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))

  // 批量排班
  Mock.mock(/\/api\/admin\/doctors\/\d+\/schedule\/batch/, 'post', () => ({ code: 200, message: '批量创建成功', data: null }))

  // 停诊
  Mock.mock(/\/api\/admin\/doctors\/\d+\/schedule\/suspend/, 'post', () => ({ code: 200, message: '停诊设置成功', data: null }))

  // 科室列表
  Mock.mock(/\/api\/admin\/departments/, 'get', () => {
    const depts = departments.map((name, i) => ({
      id: i + 1,
      name,
      parentId: 0,
      icon: '',
      doctorCount: Mock.Random.integer(5, 30),
      sortOrder: i + 1,
      status: 1,
      description: `${name}科室描述`,
      children: i < 2 ? [
        { id: (i + 1) * 10 + 1, name: `${name}一病区`, parentId: i + 1, doctorCount: Mock.Random.integer(2, 10), sortOrder: 1, status: 1 },
        { id: (i + 1) * 10 + 2, name: `${name}二病区`, parentId: i + 1, doctorCount: Mock.Random.integer(2, 10), sortOrder: 2, status: 1 }
      ] : []
    }))
    return {
      code: 200,
      message: '成功',
      data: depts
    }
  })

  // 创建科室
  Mock.mock(/\/api\/admin\/departments$/, 'post', () => ({ code: 200, message: '创建成功', data: null }))

  // 更新科室
  Mock.mock(/\/api\/admin\/departments\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))

  // 删除科室
  Mock.mock(/\/api\/admin\/departments\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))

  // 评价列表
  Mock.mock(/\/api\/admin\/doctors\/reviews/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 200
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        doctorId: Mock.Random.integer(1, 50),
        doctorName: doctorNames[Mock.Random.integer(0, doctorNames.length - 1)],
        userId: Mock.Random.integer(1000, 9999),
        patientNickname: '用户***' + Mock.mock('@string("number", 3)'),
        consultationId: Mock.Random.integer(1, 500),
        consultationType: Mock.Random.pick(['图文问诊', '视频问诊', '电话问诊']),
        rating: Mock.Random.integer(1, 5),
        content: Mock.Random.cparagraph(1),
        reply: Mock.Random.boolean() ? '感谢您的评价！' : undefined,
        replyTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        status: Mock.Random.integer(0, 2),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  // 评价回复
  Mock.mock(/\/api\/admin\/doctors\/reviews\/\d+\/reply/, 'post', () => ({ code: 200, message: '回复成功', data: null }))

  // 隐藏评价
  Mock.mock(/\/api\/admin\/doctors\/reviews\/\d+\/hide/, 'put', () => ({ code: 200, message: '隐藏成功', data: null }))
}
