// 模拟药品数据
export const mockDrugs = [
  {
    id: '1',
    name: '阿莫西林胶囊',
    specification: '0.25g*24粒',
    manufacturer: '华北制药',
    price: 12.50,
    originalPrice: 18.00,
    image: 'https://via.placeholder.com/200x200/00b578/ffffff?text=阿莫西林',
    isRx: true,
    category: '抗生素',
    disease: '呼吸道感染',
    usage: '口服，成人一次0.5g，每6-8小时1次',
    stock: 100,
    sales: 999
  },
  {
    id: '2',
    name: '布洛芬缓释胶囊',
    specification: '0.3g*20粒',
    manufacturer: '中美史克',
    price: 15.80,
    originalPrice: 22.00,
    image: 'https://via.placeholder.com/200x200/ff9500/ffffff?text=布洛芬',
    isRx: false,
    category: '解热镇痛',
    disease: '头痛、牙痛',
    usage: '口服，成人一次1粒，一日2次',
    stock: 200,
    sales: 1999
  },
  {
    id: '3',
    name: '感冒灵颗粒',
    specification: '10g*9袋',
    manufacturer: '999药业',
    price: 18.50,
    originalPrice: 25.00,
    image: 'https://via.placeholder.com/200x200/ff4d4f/ffffff?text=感冒灵',
    isRx: false,
    category: '感冒药',
    disease: '感冒',
    usage: '开水冲服，一次10克，一日3次',
    stock: 150,
    sales: 2999
  },
  {
    id: '4',
    name: '阿奇霉素片',
    specification: '0.25g*6片',
    manufacturer: '辉瑞制药',
    price: 28.00,
    originalPrice: 38.00,
    image: 'https://via.placeholder.com/200x200/1890ff/ffffff?text=阿奇霉素',
    isRx: true,
    category: '抗生素',
    disease: '支原体感染',
    usage: '口服，成人一次0.5g，一日1次',
    stock: 80,
    sales: 599
  },
  {
    id: '5',
    name: '蒙脱石散',
    specification: '3g*10袋',
    manufacturer: '博福-益普生',
    price: 22.50,
    originalPrice: 30.00,
    image: 'https://via.placeholder.com/200x200/52c41a/ffffff?text=蒙脱石散',
    isRx: false,
    category: '消化系统',
    disease: '腹泻',
    usage: '口服，成人一次1袋，一日3次',
    stock: 120,
    sales: 1299
  },
  {
    id: '6',
    name: '维生素C片',
    specification: '100mg*100片',
    manufacturer: '养生堂',
    price: 19.90,
    originalPrice: 28.00,
    image: 'https://via.placeholder.com/200x200/fa8c16/ffffff?text=维生素C',
    isRx: false,
    category: '维生素',
    disease: '维生素缺乏',
    usage: '口服，一次1-2片，一日3次',
    stock: 200,
    sales: 3999
  }
]

// 模拟分类数据
export const mockCategories = [
  { id: '1', name: '感冒发烧', icon: 'https://via.placeholder.com/60x60/ff4d4f/ffffff?text=感冒' },
  { id: '2', name: '呼吸系统', icon: 'https://via.placeholder.com/60x60/1890ff/ffffff?text=呼吸' },
  { id: '3', name: '消化系统', icon: 'https://via.placeholder.com/60x60/52c41a/ffffff?text=消化' },
  { id: '4', name: '皮肤用药', icon: 'https://via.placeholder.com/60x60/fa8c16/ffffff?text=皮肤' },
  { id: '5', name: '维生素钙', icon: 'https://via.placeholder.com/60x60/eb2f96/ffffff?text=维生素' },
  { id: '6', name: '解热镇痛', icon: 'https://via.placeholder.com/60x60/722ed1/ffffff?text=镇痛' },
  { id: '7', name: '五官用药', icon: 'https://via.placeholder.com/60x60/13c2c2/ffffff?text=五官' },
  { id: '8', name: '医疗器械', icon: 'https://via.placeholder.com/60x60/cf1322/ffffff?text=器械' }
]

// 模拟问诊数据
export const mockInquiryData = {
  symptoms: [
    '头痛',
    '发烧',
    '咳嗽',
    '流鼻涕',
    '咽痛',
    '腹痛',
    '腹泻',
    '恶心',
    '皮疹',
    '失眠'
  ],
  doctors: [
    {
      id: '1',
      name: '李医生',
      title: '主治医师',
      department: '内科',
      specialty: '呼吸道感染、消化系统疾病',
      avatar: 'https://via.placeholder.com/100x100/1890ff/ffffff?text=李',
      online: true,
      waitTime: 5,
      rating: 4.9,
      consultCount: 5234
    },
    {
      id: '2',
      name: '王医生',
      title: '副主任医师',
      department: '全科',
      specialty: '常见病、慢性病管理',
      avatar: 'https://via.placeholder.com/100x100/52c41a/ffffff?text=王',
      online: true,
      waitTime: 3,
      rating: 4.8,
      consultCount: 8921
    },
    {
      id: '3',
      name: '张医生',
      title: '主治医师',
      department: '儿科',
      specialty: '儿童常见病、儿童保健',
      avatar: 'https://via.placeholder.com/100x100/fa8c16/ffffff?text=张',
      online: false,
      waitTime: 0,
      rating: 4.9,
      consultCount: 6754
    }
  ]
}

// 模拟订单数据
export const mockOrders = [
  {
    id: 'ORD20241201001',
    status: 'completed',
    statusText: '已完成',
    drugs: [
      { name: '布洛芬缓释胶囊', specification: '0.3g*20粒', quantity: 1, price: 15.80 }
    ],
    totalAmount: 15.80,
    deliveryFee: 0,
    createTime: '2024-12-01 10:30:00',
    deliveryType: 'delivery'
  },
  {
    id: 'ORD20241202002',
    status: 'pending',
    statusText: '待支付',
    drugs: [
      { name: '感冒灵颗粒', specification: '10g*9袋', quantity: 2, price: 18.50 },
      { name: '维生素C片', specification: '100mg*100片', quantity: 1, price: 19.90 }
    ],
    totalAmount: 57.90,
    deliveryFee: 5.00,
    createTime: '2024-12-02 14:20:00',
    deliveryType: 'delivery'
  },
  {
    id: 'ORD20241203003',
    status: 'shipping',
    statusText: '配送中',
    drugs: [
      { name: '阿莫西林胶囊', specification: '0.25g*24粒', quantity: 1, price: 12.50 }
    ],
    totalAmount: 12.50,
    deliveryFee: 5.00,
    createTime: '2024-12-03 09:15:00',
    deliveryType: 'self-pickup'
  }
]
