export const DISEASE_TAGS = [
  { id: '1', name: '感冒发热' },
  { id: '2', name: '咳嗽咽痛' },
  { id: '3', name: '头痛头晕' },
  { id: '4', name: '消化不良' },
  { id: '5', name: '腹泻腹痛' },
  { id: '6', name: '皮肤过敏' },
  { id: '7', name: '失眠多梦' },
  { id: '8', name: '高血压' },
  { id: '9', name: '糖尿病' },
  { id: '10', name: '冠心病' }
]

export const DEFAULT_DOCTOR_ID = 'DOC001'
export const DEFAULT_DOCTOR_NAME = '在线医生'

export const RELATIONSHIP_OPTIONS = [
  { label: '本人', value: 'self' },
  { label: '父母', value: 'parent' },
  { label: '子女', value: 'child' },
  { label: '配偶', value: 'spouse' },
  { label: '其他', value: 'other' }
]

export const GENDER_OPTIONS = [
  { label: '男', value: 'male' },
  { label: '女', value: 'female' }
]

export const SERVICE_TYPES = {
  TEXT_IMAGE: '图文咨询',
  PHONE: '电话咨询',
  VIDEO: '视频咨询'
}

export const EMPTY_TEXT_MAP = {
  all: '暂无咨询记录',
  in_progress: '暂无进行中的咨询',
  completed: '暂无已完成的咨询',
  pending_review: '暂无待评价的咨询'
}

export const CONSULTATION_STATUS_TEXT_MAP: Record<string, string> = {
  pending: '待支付',
  waiting: '等待接诊',
  in_progress: '进行中',
  completed: '已完成',
  cancelled: '已取消',
  refunded: '已退款'
}

export const CONSULTATION_STATUS_CLASS_MAP: Record<string, string> = {
  pending: 'pending',
  waiting: 'waiting',
  in_progress: 'in-progress',
  completed: 'completed',
  cancelled: 'cancelled',
  refunded: 'refunded'
}

export const PAYMENT_METHODS = [
  { value: 'wechat' as const, label: '微信支付', desc: '推荐使用', icon: 'wechat' },
  { value: 'alipay' as const, label: '支付宝', desc: '', icon: 'alipay' },
  { value: 'balance' as const, label: '余额支付', desc: '', icon: 'balance' },
  { value: 'bankcard' as const, label: '银行卡', desc: '支持储蓄卡/信用卡', icon: 'bankcard' }
]

export const DOCTOR_TITLES = [
  { value: 'chief', label: '主任医师' },
  { value: 'associate', label: '副主任医师' },
  { value: 'attending', label: '主治医师' },
  { value: 'resident', label: '住院医师' }
]

export const HOSPITAL_LEVELS = [
  { value: 'all', label: '全部' },
  { value: '3a', label: '三甲医院' },
  { value: '2a', label: '二甲医院' },
  { value: '1a', label: '一甲医院' }
]

export const DOCTOR_SORT_OPTIONS = [
  { value: 'default', label: '综合排序' },
  { value: 'rating', label: '好评优先' },
  { value: 'consult', label: '接诊量优先' },
  { value: 'price', label: '价格从低到高' }
]

export const DOCTOR_SPECIALTY_OPTIONS = [
  { value: 'all', label: '全部擅长' },
  { value: 'allergy', label: '过敏性疾病' },
  { value: 'skin', label: '皮肤疾病' },
  { value: 'tcm', label: '中医调理' },
  { value: 'chronic', label: '慢性病管理' },
  { value: 'child', label: '儿科疾病' },
  { value: 'psychology', label: '心理咨询' }
]

export const DOCTOR_FEATURE_OPTIONS = [
  { value: 'prescription', label: '可开处方' },
  { value: 'online', label: '在线医生' },
  { value: 'quick', label: '秒问医生' },
  { value: 'cheap', label: '低价咨询' }
]

export const DEPARTMENTS = [
  { code: 'bone', name: '骨科', icon: 'bone', tag: '', tagType: 'info' },
  { code: 'neurology', name: '神经内科', icon: 'brain', tag: '', tagType: 'info' },
  { code: 'general', name: '全科', icon: 'firstAid', tag: '', tagType: 'info' },
  { code: 'tcm', name: '中医科', icon: 'herb', tag: '', tagType: 'info' },
  { code: 'surgery', name: '普外科', icon: 'scissor', tag: '', tagType: 'info' },
  { code: 'andrology', name: '男科门诊', icon: 'male', tag: '', tagType: 'info' },
  { code: 'cardiology', name: '心血管内科', icon: 'heart', tag: '', tagType: 'info' },
  { code: 'endocrine', name: '内分泌科', icon: 'stomach', tag: '', tagType: 'info' },
  { code: 'tcm-spleen', name: '中医脾胃病', icon: 'herb', tag: '', tagType: 'info' },
  { code: 'tcm-male', name: '中医男科', icon: 'male', tag: '补肾', tagType: 'supplement' },
  { code: 'tcm-sleep', name: '中医失眠科', icon: 'moon', tag: '', tagType: 'info' },
  { code: 'tcm-female', name: '中医妇科', icon: 'female', tag: '', tagType: 'info' },
  { code: 'weight', name: '减重门诊', icon: 'scale', tag: '', tagType: 'info' },
  { code: 'sleep', name: '睡眠中心', icon: 'moon', tag: '9.9元起', tagType: 'price' },
  { code: 'dermatology', name: '皮肤科', icon: 'skin', tag: '瘙痒', tagType: 'hot' },
  { code: 'respiratory', name: '呼吸内科', icon: 'lung', tag: '', tagType: 'info' },
  { code: 'pediatrics', name: '儿科', icon: 'child', tag: '发热', tagType: 'fever' },
  { code: 'gastroenterology', name: '消化内科', icon: 'stomach', tag: '', tagType: 'info' },
  { code: 'gynecology', name: '妇产科', icon: 'female', tag: '', tagType: 'info' },
  { code: 'ent', name: '耳鼻喉科', icon: 'ear', tag: '', tagType: 'info' },
  { code: 'urology', name: '泌尿外科', icon: 'kidney', tag: '', tagType: 'info' },
  { code: 'dental', name: '口腔科', icon: 'tooth', tag: '', tagType: 'info' },
  { code: 'ophthalmology', name: '眼科', icon: 'eye', tag: '', tagType: 'info' },
  { code: 'psychology', name: '心理咨询', icon: 'brain', tag: '19.9元', tagType: 'price' }
]

export const DEPARTMENT_TAGS = [
  { value: 'all', label: '全部' },
  { value: 'dermatology', label: '皮肤科' },
  { value: 'respiratory', label: '呼吸内科' },
  { value: 'pediatrics', label: '儿科' },
  { value: 'gastroenterology', label: '消化内科' }
]

export const SERVICE_SHORTCUTS = [
  { id: 1, name: '用药咨询', subtitle: '安全用药', image: '', doctorAvatar: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=100&h=100&fit=crop&crop=face' },
  { id: 2, name: '抓中药', subtitle: '养生茶饮', image: '', doctorAvatar: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=100&h=100&fit=crop&crop=face' },
  { id: 3, name: '心理咨询', subtitle: '19.9元', image: '', doctorAvatar: 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=100&h=100&fit=crop&crop=face' },
  { id: 4, name: '电话医生', subtitle: '9.9元起', image: '', doctorAvatar: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=100&h=100&fit=crop&crop=face' }
]

export const CONSULTATION_STEPS = [
  { step: 1, name: '导诊助手' },
  { step: 2, name: '支付诊费' },
  { step: 3, name: '医生接诊' },
  { step: 4, name: '问诊咨询' }
]

export const DEPARTMENT_INFO_MAP: Record<string, any> = {
  bone: {
    name: '骨科',
    price: 4.9,
    originalPrice: 19.9,
    subsidy: 15,
    symptoms: '关节炎、颈椎病、腰椎间盘突出、腰肌劳损、肩周炎、骨折后康复、骨质增生、腱鞘炎、骨质疏松、运动损伤、检查单解读',
    responseTime: 9,
    answerTime: 30,
    example: '腰痛伴随腿发麻，1周，用膏药外贴，效果不明显。',
    quickSymptoms: ['颈椎病', '腰痛', '腰椎间盘突出', '关节炎', '腰肌劳损', '关节痛', '关节扭伤', '外伤', '肩周炎', '骨折']
  },
  neurology: {
    name: '神经内科',
    price: 7.9,
    originalPrice: 19.9,
    subsidy: 12,
    symptoms: '失眠、头痛、头晕、焦虑、抑郁状态、神经痛、脑血管病、帕金森病、认知功能障碍、面肌痉挛、面神经炎、检查单解读',
    responseTime: 9,
    answerTime: 30,
    example: '失眠，1个月，未使用过药物，未线下就诊。',
    quickSymptoms: ['头痛头晕', '失眠', '易醒', '偏头痛', '焦虑障碍', '多梦', '眩晕', '周围神经病', '头晕目眩', '记忆力减退']
  },
  dermatology: {
    name: '皮肤科',
    price: 19.9,
    originalPrice: 39.9,
    subsidy: 20,
    symptoms: '湿疹、荨麻疹、痤疮、银屑病、白癜风、皮炎、皮肤瘙痒、脱发、色斑、痘痘、过敏',
    responseTime: 5,
    answerTime: 15,
    example: '面部起红疹，瘙痒3天，未使用过药物。',
    quickSymptoms: ['湿疹', '荨麻疹', '痤疮', '皮肤瘙痒', '过敏', '脱发', '痘痘', '色斑', '皮炎', '银屑病']
  },
  respiratory: {
    name: '呼吸内科',
    price: 9.9,
    originalPrice: 29.9,
    subsidy: 20,
    symptoms: '咳嗽、咳痰、气喘、胸闷、胸痛、发热、感冒、支气管炎、肺炎、哮喘、慢阻肺',
    responseTime: 8,
    answerTime: 20,
    example: '半夜咽痒咳嗽，持续1周，喝水可缓解，未用过药。',
    quickSymptoms: ['咳嗽', '咳痰', '气喘', '胸闷', '发热', '感冒', '咽痛', '流鼻涕', '打喷嚏', '胸痛']
  },
  pediatrics: {
    name: '儿科',
    price: 9.9,
    originalPrice: 29.9,
    subsidy: 20,
    symptoms: '小儿发热、咳嗽、腹泻、呕吐、湿疹、厌食、夜啼、多动症、发育迟缓、疫苗接种咨询',
    responseTime: 6,
    answerTime: 15,
    example: '宝宝发热38.5度，持续2天，精神尚可，未用药。',
    quickSymptoms: ['发热', '咳嗽', '腹泻', '呕吐', '湿疹', '厌食', '夜啼', '流鼻涕', '皮疹', '腹痛']
  },
  gynecology: {
    name: '妇产科',
    price: 19.9,
    originalPrice: 49.9,
    subsidy: 30,
    symptoms: '月经不调、痛经、白带异常、阴道炎、盆腔炎、子宫肌瘤、卵巢囊肿、备孕咨询、孕期检查',
    responseTime: 10,
    answerTime: 25,
    example: '月经推迟10天，伴有腹痛，未做过检查。',
    quickSymptoms: ['月经不调', '痛经', '白带异常', '腹痛', '备孕咨询', '孕期检查', '阴道炎', '盆腔炎', '子宫肌瘤', '卵巢囊肿']
  },
  gastroenterology: {
    name: '消化内科',
    price: 9.9,
    originalPrice: 29.9,
    subsidy: 20,
    symptoms: '胃痛、胃胀、反酸、恶心、呕吐、腹泻、便秘、消化不良、胃炎、胃溃疡、肠炎',
    responseTime: 8,
    answerTime: 20,
    example: '胃痛伴反酸，持续3天，饭后加重，未用药。',
    quickSymptoms: ['胃痛', '胃胀', '反酸', '恶心', '腹泻', '便秘', '消化不良', '呕吐', '腹胀', '食欲差']
  },
  psychology: {
    name: '心理咨询',
    price: 19.9,
    originalPrice: 199,
    subsidy: 179,
    symptoms: '焦虑、抑郁、失眠、情绪低落、压力大、人际关系困扰、职场压力、婚姻家庭问题、自我成长',
    responseTime: 5,
    answerTime: 10,
    example: '最近情绪低落，失眠2周，工作压力大。',
    quickSymptoms: ['焦虑', '抑郁', '失眠', '情绪低落', '压力大', '人际关系', '职场压力', '婚姻问题', '自我成长', '注意力不集中']
  },
  tcm: {
    name: '中医科',
    price: 14.9,
    originalPrice: 39.9,
    subsidy: 25,
    symptoms: '体质调理、脾胃虚弱、气血不足、失眠多梦、腰膝酸软、月经不调、慢性疲劳、亚健康调理',
    responseTime: 12,
    answerTime: 30,
    example: '脾胃虚弱，食欲不振，乏力2个月，未系统调理。',
    quickSymptoms: ['脾胃虚弱', '气血不足', '失眠多梦', '腰膝酸软', '乏力', '食欲不振', '便秘', '怕冷', '盗汗', '口干']
  },
  general: {
    name: '全科',
    price: 4.9,
    originalPrice: 19.9,
    subsidy: 15,
    symptoms: '常见病、多发病、慢性病管理、健康咨询、体检报告解读、用药咨询、疫苗接种',
    responseTime: 5,
    answerTime: 15,
    example: '体检发现血压偏高，140/90，无症状，未用药。',
    quickSymptoms: ['高血压', '高血糖', '高血脂', '体检咨询', '用药咨询', '健康管理', '疫苗接种', '慢性病', '亚健康', '疲劳']
  }
}

export const DOCTORS_MAP: Record<string, any[]> = {
  bone: [
    { id: 'B001', name: '张建华', title: '主任医师', hospital: '北京协和医院', department: '骨科', specialty: '颈椎病、腰椎间盘突出、关节置换', price: 29.9 },
    { id: 'B002', name: '李明', title: '副主任医师', hospital: '北京大学第三医院', department: '骨科', specialty: '运动损伤、骨折、骨质疏松', price: 19.9 }
  ],
  neurology: [
    { id: 'N001', name: '王芳', title: '主任医师', hospital: '宣武医院', department: '神经内科', specialty: '失眠、头痛、脑血管病', price: 39.9 },
    { id: 'N002', name: '刘强', title: '主治医师', hospital: '天坛医院', department: '神经内科', specialty: '帕金森病、面神经炎', price: 19.9 }
  ],
  respiratory: [
    { id: 'R001', name: '邓健楠', title: '主治医师', hospital: '首都医科大学附属北京朝阳医院', department: '呼吸内科', specialty: '肺炎、哮喘、肺结核、支气管肺癌', price: 39.9 },
    { id: 'R002', name: '陈晓', title: '副主任医师', hospital: '中日友好医院', department: '呼吸内科', specialty: '慢阻肺、呼吸衰竭', price: 29.9 }
  ],
  dermatology: [
    { id: 'D001', name: '赵美丽', title: '主任医师', hospital: '北京空军总医院', department: '皮肤科', specialty: '湿疹、银屑病、痤疮', price: 39.9 }
  ],
  pediatrics: [
    { id: 'P001', name: '孙丽', title: '主任医师', hospital: '北京儿童医院', department: '儿科', specialty: '小儿发热、咳嗽、腹泻', price: 29.9 }
  ],
  general: [
    { id: 'G001', name: '周医生', title: '主治医师', hospital: '互联网医院', department: '全科', specialty: '常见病、多发病、慢性病管理', price: 9.9 },
    { id: 'G002', name: '吴医生', title: '副主任医师', hospital: '互联网医院', department: '全科', specialty: '健康咨询、体检报告解读', price: 19.9 }
  ]
}
