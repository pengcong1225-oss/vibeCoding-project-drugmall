/**
 * 问诊管理Mock数据
 */
import Mock from 'mockjs'

const doctorNames = ['张伟', '李芳', '王强', '刘敏', '陈磊']
const departments = ['内科', '外科', '儿科', '妇产科', '皮肤科']
const symptoms = ['头痛发热两天', '咳嗽咳痰一周', '胸闷气短', '腹痛腹泻', '皮肤瘙痒起疹', '关节疼痛', '头晕乏力', '失眠多梦']

export function setupConsultationMock() {
  // 问诊列表
  Mock.mock(/\/api\/admin\/consultations$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 356
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      const status = Mock.Random.pick([0, 0, 1, 2, 3, 3, 3, 4, 5, 6])
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        consultationNo: 'C' + Mock.mock('@string("number", 12)'),
        patientNickname: '患者***' + Mock.mock('@string("number", 3)'),
        patientPhone: Mock.mock('@phone'),
        doctorId: Mock.Random.integer(1, 50),
        doctorName: doctorNames[Mock.Random.integer(0, doctorNames.length - 1)],
        departmentId: Mock.Random.integer(1, departments.length),
        departmentName: departments[Mock.Random.integer(0, departments.length - 1)],
        type: Mock.Random.pick(['text', 'video', 'phone', 'ai']),
        symptom: symptoms[Mock.Random.integer(0, symptoms.length - 1)],
        fee: Mock.Random.float(9.9, 199, 0, 2),
        status,
        responseTime: Mock.Random.integer(1, 60),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        endTime: status >= 3 ? Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') : undefined
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  // 问诊详情
  Mock.mock(/\/api\/admin\/consultations\/\d+$/, 'get', () => {
    const operationLogs = [
      { id: 1, operatorName: '系统', action: '问诊创建', detail: '患者发起问诊', createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') },
      { id: 2, operatorName: '系统', action: '医生接诊', detail: '医生开始问诊', createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') },
      { id: 3, operatorName: '系统', action: '问诊完成', detail: '医生结束问诊', createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') }
    ]

    return {
      code: 200,
      message: '成功',
      data: {
        id: Mock.Random.integer(1, 999),
        consultationNo: 'C' + Mock.mock('@string("number", 12)'),
        patientNickname: '患者***' + Mock.mock('@string("number", 3)'),
        patientGender: Mock.Random.integer(0, 1),
        patientAge: Mock.Random.integer(1, 80),
        patientPhone: Mock.mock('@phone'),
        patientId: Mock.Random.integer(1000, 9999),
        doctorId: Mock.Random.integer(1, 50),
        doctorName: doctorNames[0],
        departmentId: 1,
        departmentName: departments[0],
        type: 'text',
        symptom: symptoms[Mock.Random.integer(0, symptoms.length - 1)],
        allergyHistory: '无',
        medicationHistory: '无',
        fee: Mock.Random.float(9.9, 199, 0, 2),
        status: 3,
        images: [Mock.Random.image('200x150', '#409EFF', '', 'png'), Mock.Random.image('200x150', '#67C23A', '', 'png')],
        prescriptionIds: [Mock.Random.integer(1, 100)],
        payAmount: Mock.Random.float(9.9, 199, 0, 2),
        payType: '微信支付',
        payTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        payStatus: 1,
        operationLogs,
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        endTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      }
    }
  })

  // 问诊消息
  Mock.mock(/\/api\/admin\/consultations\/\d+\/messages/, 'get', () => {
    const messages = [
      { id: 1, consultationId: 1, senderType: 'system' as const, senderId: 0, senderName: '系统', content: '问诊开始', messageType: 'system' as const, createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') },
      { id: 2, consultationId: 1, senderType: 'patient' as const, senderId: 1001, senderName: '患者', content: '医生您好，我最近头痛发热，已经两天了。', messageType: 'text' as const, createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') },
      { id: 3, consultationId: 1, senderType: 'doctor' as const, senderId: 1, senderName: doctorNames[0], content: '您好，请问除了头痛发热还有其他症状吗？比如咳嗽、喉咙痛？', messageType: 'text' as const, createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') },
      { id: 4, consultationId: 1, senderType: 'patient' as const, senderId: 1001, senderName: '患者', content: '有轻微咳嗽，喉咙也有点不舒服。', messageType: 'text' as const, createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') },
      { id: 5, consultationId: 1, senderType: 'doctor' as const, senderId: 1, senderName: doctorNames[0], content: '根据您的描述，可能是上呼吸道感染。我给您开一个处方，请按照医嘱用药。', messageType: 'text' as const, createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') },
      { id: 6, consultationId: 1, senderType: 'doctor' as const, senderId: 1, senderName: doctorNames[0], content: '已开具处方', messageType: 'prescription' as const, prescriptionId: Mock.Random.integer(1, 100), createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') }
    ]
    return { code: 200, message: '成功', data: messages }
  })

  // 问诊分配 - 可分配医生
  Mock.mock(/\/api\/admin\/consultations\/\d+\/available-doctors/, 'get', () => {
    const doctors = Array.from({ length: 8 }, (_, i) => ({
      id: i + 1,
      name: doctorNames[Mock.Random.integer(0, doctorNames.length - 1)],
      departmentName: departments[Mock.Random.integer(0, departments.length - 1)],
      title: Mock.Random.pick(titles),
      rating: Mock.Random.float(3.5, 5, 1, 1),
      currentConsultations: Mock.Random.integer(0, 5),
      maxConsultations: 10,
      onlineStatus: Mock.Random.boolean()
    }))
    return { code: 200, message: '成功', data: doctors }
  })

  // 问诊分配
  Mock.mock(/\/api\/admin\/consultations\/\d+\/assign/, 'put', () => ({ code: 200, message: '分配成功', data: null }))

  // 问诊分配规则
  Mock.mock(/\/api\/admin\/consultations\/assign-rules/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      autoAssignEnabled: true,
      maxWaitTime: 5,
      assignStrategy: 'round_robin',
      priorityDepartments: [1, 2]
    }
  }))

  Mock.mock(/\/api\/admin\/consultations\/assign-rules/, 'put', () => ({ code: 200, message: '更新成功', data: null }))

  // 问诊统计
  Mock.mock(/\/api\/admin\/consultations\/stats$/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      totalConsultations: Mock.Random.integer(200, 500),
      todayConsultations: Mock.Random.integer(10, 50),
      completedConsultations: Mock.Random.integer(150, 400),
      avgResponseTime: Mock.Random.integer(2, 15),
      completionRate: Mock.Random.float(80, 98, 0, 1),
      totalRevenue: Mock.Random.float(10000, 80000, 0, 2)
    }
  }))

  // 问诊趋势
  Mock.mock(/\/api\/admin\/consultations\/stats\/trend/, 'get', () => {
    const dates = []
    const data = []
    for (let i = 6; i >= 0; i--) {
      const d = new Date()
      d.setDate(d.getDate() - i)
      dates.push(`${d.getMonth() + 1}-${d.getDate()}`)
      data.push(Mock.Random.integer(20, 80))
    }
    return { code: 200, message: '成功', data: { dates, data } }
  })

  // 科室排名
  Mock.mock(/\/api\/admin\/consultations\/stats\/departments/, 'get', () => {
    return {
      code: 200,
      message: '成功',
      data: departments.map((name, i) => ({
        departmentName: name,
        consultationCount: Mock.Random.integer(20, 100),
        avgRating: Mock.Random.float(3.5, 5, 1, 1)
      }))
    }
  })

  // 医生排名
  Mock.mock(/\/api\/admin\/consultations\/stats\/doctors/, 'get', () => ({
    code: 200,
    message: '成功',
    data: doctorNames.map((name) => ({
      doctorName: name,
      consultationCount: Mock.Random.integer(10, 80),
      avgRating: Mock.Random.float(3.5, 5, 1, 1)
    }))
  }))

  // 时段分布
  Mock.mock(/\/api\/admin\/consultations\/stats\/hourly/, 'get', () => {
    const data = Array.from({ length: 24 }, (_, i) => ({
      hour: `${i}:00`,
      count: i >= 8 && i <= 22 ? Mock.Random.integer(5, 30) : Mock.Random.integer(0, 5)
    }))
    return { code: 200, message: '成功', data }
  })

  // 异常问诊列表
  Mock.mock(/\/api\/admin\/consultations\/exceptions/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 25
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        consultationId: Mock.Random.integer(1, 500),
        consultationNo: 'C' + Mock.mock('@string("number", 12)'),
        exceptionType: Mock.Random.pick(['timeout_no_response', 'timeout_no_reply', 'complaint', 'refund', 'abnormal_cancel']),
        patientNickname: '患者***' + Mock.mock('@string("number", 3)'),
        doctorName: doctorNames[Mock.Random.integer(0, doctorNames.length - 1)],
        description: Mock.Random.cparagraph(1),
        status: Mock.Random.pick(['pending', 'processing', 'handled']),
        handlerName: '',
        handleMethod: '',
        handleNote: '',
        compensationAmount: undefined,
        handledAt: '',
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  // 异常详情
  Mock.mock(/\/api\/admin\/consultations\/exceptions\/\d+/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      id: Mock.Random.integer(1, 100),
      consultationId: Mock.Random.integer(1, 500),
      consultationNo: 'C' + Mock.mock('@string("number", 12)'),
      exceptionType: 'complaint',
      patientNickname: '患者***123',
      doctorName: '张伟',
      description: Mock.Random.cparagraph(2),
      status: 'pending',
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }))

  // 异常处理
  Mock.mock(/\/api\/admin\/consultations\/exceptions\/\d+\/handle/, 'put', () => ({ code: 200, message: '处理成功', data: null }))

  // 异常统计
  Mock.mock(/\/api\/admin\/consultations\/exceptions\/stats/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      totalExceptions: Mock.Random.integer(10, 50),
      pendingExceptions: Mock.Random.integer(2, 15),
      processedExceptions: Mock.Random.integer(5, 30),
      complaintRate: Mock.Random.float(0.5, 5, 0, 1),
      refundRate: Mock.Random.float(1, 8, 0, 1)
    }
  }))

  // 取消问诊
  Mock.mock(/\/api\/admin\/consultations\/\d+\/cancel/, 'put', () => ({ code: 200, message: '取消成功', data: null }))

  // 退款
  Mock.mock(/\/api\/admin\/consultations\/\d+\/refund/, 'post', () => ({ code: 200, message: '退款申请已提交', data: null }))
}

const titles = ['主任医师', '副主任医师', '主治医师', '住院医师']
