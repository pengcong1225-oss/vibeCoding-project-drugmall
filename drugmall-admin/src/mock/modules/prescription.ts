/**
 * 处方管理Mock数据
 */
import Mock from 'mockjs'

const drugNames = ['阿莫西林胶囊', '布洛芬缓释胶囊', '复方氨酚烷胺片', '氯雷他定片', '奥美拉唑肠溶胶囊', '盐酸二甲双胍片', '硝苯地平控释片', '蒙脱石散']
const doctorNames = ['张伟', '李芳', '王强', '刘敏', '陈磊']
const diagnosisNames = ['上呼吸道感染', '急性支气管炎', '高血压', '糖尿病', '胃炎', '过敏性鼻炎', '湿疹']

export function setupPrescriptionMock() {
  // 处方列表
  Mock.mock(/\/api\/admin\/prescriptions$/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 520
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      const status = Mock.Random.pick([0, 0, 1, 1, 1, 2, 3, 4])
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        prescriptionNo: 'RX' + Mock.mock('@string("number", 12)'),
        patientName: Mock.Random.cname(),
        doctorId: Mock.Random.integer(1, 50),
        doctorName: doctorNames[Mock.Random.integer(0, doctorNames.length - 1)],
        diagnosis: diagnosisNames[Mock.Random.integer(0, diagnosisNames.length - 1)],
        drugCount: Mock.Random.integer(1, 6),
        totalAmount: Mock.Random.float(20, 500, 0, 2),
        type: Mock.Random.pick(['normal', 'emergency', 'chronic']),
        status,
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
        auditTime: status >= 1 ? Mock.Random.datetime('yyyy-MM-dd HH:mm:ss') : undefined
      })
    }

    return {
      code: 200,
      message: '成功',
      data: { list, total, pageNum, pageSize }
    }
  })

  // 处方详情
  Mock.mock(/\/api\/admin\/prescriptions\/\d+$/, 'get', () => {
    const items = Array.from({ length: Mock.Random.integer(2, 5) }, (_, i) => ({
      id: i + 1,
      prescriptionId: 1,
      drugId: Mock.Random.integer(1, 100),
      drugName: drugNames[Mock.Random.integer(0, drugNames.length - 1)],
      specification: Mock.Random.pick(['12粒/盒', '24片/盒', '100ml/瓶']),
      manufacturer: '某制药厂',
      usage: Mock.Random.pick(['口服', '外用', '含服']),
      dosage: Mock.Random.pick(['1-2粒', '1片', '10ml']),
      frequency: Mock.Random.pick(['一日三次', '一日两次', '一日一次']),
      duration: Mock.Random.integer(3, 14),
      quantity: Mock.Random.integer(1, 3),
      unitPrice: Mock.Random.float(5, 50, 0, 2),
      amount: Mock.Random.float(10, 200, 0, 2),
      notes: ''
    }))

    const totalAmount = items.reduce((sum, item) => sum + item.amount, 0)

    return {
      code: 200,
      message: '成功',
      data: {
        id: Mock.Random.integer(1, 999),
        prescriptionNo: 'RX' + Mock.mock('@string("number", 12)'),
        patientName: Mock.Random.cname(),
        patientGender: Mock.Random.integer(0, 1),
        patientAge: Mock.Random.integer(18, 70),
        doctorId: Mock.Random.integer(1, 50),
        doctorName: doctorNames[0],
        doctorDepartment: '内科',
        doctorTitle: '主任医师',
        doctorHospital: '北京协和医院',
        diagnosis: diagnosisNames[Mock.Random.integer(0, diagnosisNames.length - 1)],
        drugCount: items.length,
        totalAmount: parseFloat(totalAmount.toFixed(2)),
        type: 'normal',
        status: 0,
        allergyHistory: '无',
        medicationHistory: '无',
        consultationId: Mock.Random.integer(1, 100),
        items,
        orderId: Mock.Random.integer(1, 1000),
        orderNo: 'ORD' + Mock.mock('@string("number", 14)'),
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      }
    }
  })

  // 处方审核列表
  Mock.mock(/\/api\/admin\/prescriptions\/audit/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 24
    const list = []

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        prescriptionNo: 'RX' + Mock.mock('@string("number", 12)'),
        patientName: Mock.Random.cname(),
        doctorId: Mock.Random.integer(1, 50),
        doctorName: doctorNames[Mock.Random.integer(0, doctorNames.length - 1)],
        diagnosis: diagnosisNames[Mock.Random.integer(0, diagnosisNames.length - 1)],
        drugCount: Mock.Random.integer(1, 6),
        totalAmount: Mock.Random.float(20, 500, 0, 2),
        type: Mock.Random.pick(['normal', 'emergency', 'chronic']),
        status: 0,
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  // 审核详情
  Mock.mock(/\/api\/admin\/prescriptions\/audit\/\d+/, 'get', () => {
    const items = Array.from({ length: Mock.Random.integer(2, 4) }, (_, i) => ({
      id: i + 1,
      prescriptionId: 1,
      drugId: Mock.Random.integer(1, 100),
      drugName: drugNames[Mock.Random.integer(0, drugNames.length - 1)],
      specification: '12粒/盒',
      manufacturer: '某制药厂',
      usage: '口服',
      dosage: '1-2粒',
      frequency: '一日三次',
      duration: Mock.Random.integer(3, 14),
      quantity: Mock.Random.integer(1, 3),
      unitPrice: Mock.Random.float(5, 50, 0, 2),
      amount: Mock.Random.float(10, 200, 0, 2)
    }))

    return {
      code: 200,
      message: '成功',
      data: {
        id: Mock.Random.integer(1, 999),
        prescriptionNo: 'RX' + Mock.mock('@string("number", 12)'),
        patientName: Mock.Random.cname(),
        patientGender: Mock.Random.integer(0, 1),
        patientAge: Mock.Random.integer(18, 70),
        doctorName: doctorNames[0],
        doctorDepartment: '内科',
        doctorTitle: '主任医师',
        doctorHospital: '北京协和医院',
        diagnosis: diagnosisNames[0],
        drugCount: items.length,
        totalAmount: Mock.Random.float(20, 500, 0, 2),
        type: 'normal',
        status: 0,
        allergyHistory: '无',
        medicationHistory: '无',
        items,
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      }
    }
  })

  // 系统预审
  Mock.mock(/\/api\/admin\/prescriptions\/\d+\/pre-check/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      status: 'warn',
      items: [
        { checkType: '用法用量', severity: 'medium', description: '用量略高于常规推荐剂量', drugNames: ['阿莫西林胶囊'] },
        { checkType: '配伍禁忌', severity: 'low', description: '药品间无严重配伍禁忌', drugNames: [] }
      ]
    }
  }))

  // 审核记录
  Mock.mock(/\/api\/admin\/prescriptions\/\d+\/audit-logs/, 'get', () => ({
    code: 200,
    message: '成功',
    data: []
  }))

  // 审核操作
  Mock.mock(/\/api\/admin\/prescriptions\/audit\/\d+/, 'put', () => ({ code: 200, message: '审核完成', data: null }))

  // 取消处方
  Mock.mock(/\/api\/admin\/prescriptions\/\d+\/cancel/, 'put', () => ({ code: 200, message: '取消成功', data: null }))

  // 处方模板列表
  Mock.mock(/\/api\/admin\/prescriptions\/templates/, 'get', (options: any) => {
    const url = new URL(options.url, 'http://localhost')
    const pageNum = Number(url.searchParams.get('pageNum')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 10
    const total = 15
    const list = []

    const templateNames = ['感冒常用处方', '高血压慢病处方', '糖尿病常规处方', '胃炎治疗处方', '过敏症状处方']

    for (let i = 0; i < Math.min(pageSize, total - (pageNum - 1) * pageSize); i++) {
      list.push({
        id: (pageNum - 1) * pageSize + i + 1,
        name: templateNames[Mock.Random.integer(0, templateNames.length - 1)],
        doctorId: Mock.Random.integer(1, 50),
        doctorName: doctorNames[Mock.Random.integer(0, doctorNames.length - 1)],
        departmentId: 1,
        departmentName: '内科',
        diagnosis: diagnosisNames[Mock.Random.integer(0, diagnosisNames.length - 1)],
        drugCount: Mock.Random.integer(2, 5),
        useCount: Mock.Random.integer(5, 100),
        usageNotes: Mock.Random.cparagraph(1),
        precautions: Mock.Random.cparagraph(1),
        status: 1,
        createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
      })
    }

    return { code: 200, message: '成功', data: { list, total, pageNum, pageSize } }
  })

  Mock.mock(/\/api\/admin\/prescriptions\/templates$/, 'post', () => ({ code: 200, message: '创建成功', data: null }))
  Mock.mock(/\/api\/admin\/prescriptions\/templates\/\d+/, 'put', () => ({ code: 200, message: '更新成功', data: null }))
  Mock.mock(/\/api\/admin\/prescriptions\/templates\/\d+/, 'delete', () => ({ code: 200, message: '删除成功', data: null }))
  Mock.mock(/\/api\/admin\/prescriptions\/templates\/\d+\/copy/, 'post', () => ({ code: 200, message: '复制成功', data: null }))

  // 处方统计
  Mock.mock(/\/api\/admin\/prescriptions\/stats$/, 'get', () => ({
    code: 200,
    message: '成功',
    data: {
      totalPrescriptions: Mock.Random.integer(300, 800),
      todayPrescriptions: Mock.Random.integer(10, 50),
      approvedRate: Mock.Random.float(90, 99, 0, 1),
      avgAuditTime: Mock.Random.integer(5, 30),
      totalAmount: Mock.Random.float(50000, 300000, 0, 2)
    }
  }))

  Mock.mock(/\/api\/admin\/prescriptions\/stats\/trend/, 'get', () => {
    const dates = []
    const data = []
    for (let i = 6; i >= 0; i--) {
      const d = new Date()
      d.setDate(d.getDate() - i)
      dates.push(`${d.getMonth() + 1}-${d.getDate()}`)
      data.push(Mock.Random.integer(20, 60))
    }
    return { code: 200, message: '成功', data: { dates, data } }
  })

  Mock.mock(/\/api\/admin\/prescriptions\/stats\/departments/, 'get', () => ({
    code: 200,
    message: '成功',
    data: [
      { departmentName: '内科', prescriptionCount: Mock.Random.integer(50, 200) },
      { departmentName: '外科', prescriptionCount: Mock.Random.integer(20, 80) },
      { departmentName: '儿科', prescriptionCount: Mock.Random.integer(30, 100) }
    ]
  }))

  Mock.mock(/\/api\/admin\/prescriptions\/stats\/doctors/, 'get', () => ({
    code: 200,
    message: '成功',
    data: doctorNames.map((name) => ({
      doctorName: name,
      prescriptionCount: Mock.Random.integer(10, 80)
    }))
  }))

  Mock.mock(/\/api\/admin\/prescriptions\/stats\/drugs/, 'get', () => ({
    code: 200,
    message: '成功',
    data: drugNames.slice(0, 5).map((name) => ({
      drugName: name,
      useCount: Mock.Random.integer(20, 150)
    }))
  }))
}
