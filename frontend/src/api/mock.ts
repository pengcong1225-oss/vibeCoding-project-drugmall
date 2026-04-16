// 模拟药品数据 - 使用真实药品图片
export const mockDrugs = [
  {
    id: '1',
    name: '阿莫西林胶囊',
    specification: '0.25g*24粒',
    manufacturer: '华北制药',
    price: 12.50,
    originalPrice: 18.00,
    image: '',
    imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/184890/20/42278/35271/66277f3dF3a5d0317/9e18b4060f8d1c5c.jpg',
    imageColor: '#e8f5e9',
    imageText: '阿莫西林',
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
    image: '',
    imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/215038/7/32814/42511/64c0f6e7F4e9b8e0c/9e18b4060f8d1c5c.jpg',
    imageColor: '#fff3e0',
    imageText: '布洛芬',
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
    image: '',
    imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/142975/40/39966/54723/66277f3dF3a5d0317/9e18b4060f8d1c5c.jpg',
    imageColor: '#ffebee',
    imageText: '感冒灵',
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
    image: '',
    imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/220902/38/39211/38123/66277f3dF3a5d0317/9e18b4060f8d1c5c.jpg',
    imageColor: '#e3f2fd',
    imageText: '阿奇霉素',
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
    image: '',
    imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/238462/39/12747/42511/64c0f6e7F4e9b8e0c/9e18b4060f8d1c5c.jpg',
    imageColor: '#f3e5f5',
    imageText: '蒙脱石散',
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
    image: '',
    imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/238462/39/12747/42511/64c0f6e7F4e9b8e0c/9e18b4060f8d1c5c.jpg',
    imageColor: '#fff8e1',
    imageText: '维生素C',
    isRx: false,
    category: '维生素',
    disease: '维生素缺乏',
    usage: '口服，一次1-2片，一日3次',
    stock: 200,
    sales: 3999
  },
  {
    id: '7',
    name: '盐酸氨溴索口服液',
    specification: '100ml',
    manufacturer: '勃林格殷格翰',
    price: 35.00,
    originalPrice: 45.00,
    image: '',
    imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/215038/7/32814/42511/64c0f6e7F4e9b8e0c/9e18b4060f8d1c5c.jpg',
    imageColor: '#fce4ec',
    imageText: '氨溴索',
    isRx: false,
    category: '呼吸系统',
    disease: '咳嗽痰多',
    usage: '口服，成人一次10ml，一日3次',
    stock: 80,
    sales: 899
  },
  {
    id: '8',
    name: '奥美拉唑肠溶胶囊',
    specification: '20mg*14粒',
    manufacturer: '阿斯利康',
    price: 42.00,
    originalPrice: 55.00,
    image: '',
    imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/184890/20/42278/35271/66277f3dF3a5d0317/9e18b4060f8d1c5c.jpg',
    imageColor: '#e0f7fa',
    imageText: '奥美拉唑',
    isRx: true,
    category: '消化系统',
    disease: '胃溃疡',
    usage: '口服，成人一次1粒，一日1-2次',
    stock: 60,
    sales: 699
  }
]

// 模拟分类数据 - 使用写实风格图标（图片URL）
export const mockCategories = [
  { 
    id: '1', 
    name: '感冒发烧', 
    icon: '',
    iconUrl: 'https://img.icons8.com/color/96/thermometer.png',
    iconImg: 'https://img.icons8.com/color/96/thermometer.png',
    bgColor: 'linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%)'
  },
  { 
    id: '2', 
    name: '呼吸系统', 
    icon: '',
    iconUrl: 'https://img.icons8.com/color/96/lungs.png',
    iconImg: 'https://img.icons8.com/color/96/lungs.png',
    bgColor: 'linear-gradient(135deg, #4ECDC4 0%, #44A08D 100%)'
  },
  { 
    id: '3', 
    name: '消化系统', 
    icon: '',
    iconUrl: 'https://img.icons8.com/color/96/stomach.png',
    iconImg: 'https://img.icons8.com/color/96/stomach.png',
    bgColor: 'linear-gradient(135deg, #F093FB 0%, #F5576C 100%)'
  },
  { 
    id: '4', 
    name: '皮肤用药', 
    icon: '',
    iconUrl: 'https://img.icons8.com/color/96/cream.png',
    iconImg: 'https://img.icons8.com/color/96/cream.png',
    bgColor: 'linear-gradient(135deg, #4FACFE 0%, #00F2FE 100%)'
  },
  { 
    id: '5', 
    name: '维生素钙', 
    icon: '',
    iconUrl: 'https://img.icons8.com/color/96/vitamins.png',
    iconImg: 'https://img.icons8.com/color/96/vitamins.png',
    bgColor: 'linear-gradient(135deg, #43E97B 0%, #38F9D7 100%)'
  },
  { 
    id: '6', 
    name: '解热镇痛', 
    icon: '',
    iconUrl: 'https://img.icons8.com/color/96/pain.png',
    iconImg: 'https://img.icons8.com/color/96/pain.png',
    bgColor: 'linear-gradient(135deg, #FA709A 0%, #FEE140 100%)'
  },
  { 
    id: '7', 
    name: '五官用药', 
    icon: '',
    iconUrl: 'https://img.icons8.com/color/96/eye-dropper.png',
    iconImg: 'https://img.icons8.com/color/96/eye-dropper.png',
    bgColor: 'linear-gradient(135deg, #30CFD0 0%, #330867 100%)'
  },
  { 
    id: '8', 
    name: '医疗器械', 
    icon: '',
    iconUrl: 'https://img.icons8.com/color/96/medical-thermometer.png',
    iconImg: 'https://img.icons8.com/color/96/medical-thermometer.png',
    bgColor: 'linear-gradient(135deg, #A8EDEA 0%, #FED6E3 100%)'
  },
  { 
    id: '9', 
    name: '中药饮片', 
    icon: '',
    iconUrl: 'https://img.icons8.com/color/96/herbal-medicine.png',
    iconImg: 'https://img.icons8.com/color/96/herbal-medicine.png',
    bgColor: 'linear-gradient(135deg, #D299C2 0%, #FEF9D7 100%)'
  },
  { 
    id: '10', 
    name: '儿童用药', 
    icon: '',
    iconUrl: 'https://img.icons8.com/color/96/baby-bottle.png',
    iconImg: 'https://img.icons8.com/color/96/baby-bottle.png',
    bgColor: 'linear-gradient(135deg, #89F7FE 0%, #66A6FF 100%)'
  }
]

// 个人中心菜单图标 - 写实风格
export const userMenuIcons = {
  // 订单状态图标
  wallet: 'https://img.icons8.com/color/96/wallet.png',
  box: 'https://img.icons8.com/color/96/package.png',
  van: 'https://img.icons8.com/color/96/delivery.png',
  chatDotRound: 'https://img.icons8.com/color/96/comments.png',
  
  // 我的服务图标
  document: 'https://img.icons8.com/color/96/document.png',
  firstAidKit: 'https://img.icons8.com/color/96/first-aid-kit.png',
  user: 'https://img.icons8.com/color/96/user.png',
  location: 'https://img.icons8.com/color/96/marker.png',
  
  // 更多服务图标
  questionFilled: 'https://img.icons8.com/color/96/help.png',
  editPen: 'https://img.icons8.com/color/96/edit.png',
  setting: 'https://img.icons8.com/color/96/settings.png',
  
  // 就诊人管理图标
  creditCard: 'https://img.icons8.com/color/96/id-card.png',
  phone: 'https://img.icons8.com/color/96/phone.png',
  infoFilled: 'https://img.icons8.com/color/96/info.png',
  edit: 'https://img.icons8.com/color/96/edit.png',
  delete: 'https://img.icons8.com/color/96/delete.png',
  plus: 'https://img.icons8.com/color/96/plus.png',
  
  // 通用图标
  arrowLeft: 'https://img.icons8.com/color/96/back.png',
  arrowRight: 'https://img.icons8.com/color/96/forward.png',
  starFilled: 'https://img.icons8.com/color/96/star.png',
  check: 'https://img.icons8.com/color/96/checkmark.png'
}

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
      avatar: '',
      avatarUrl: 'https://img.icons8.com/color/96/doctor-male.png',
      avatarColor: '#1890ff',
      avatarText: '李',
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
      avatar: '',
      avatarUrl: 'https://img.icons8.com/color/96/doctor-female.png',
      avatarColor: '#52c41a',
      avatarText: '王',
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
      avatar: '',
      avatarUrl: 'https://img.icons8.com/color/96/nurse-female.png',
      avatarColor: '#fa8c16',
      avatarText: '张',
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
      { name: '布洛芬缓释胶囊', specification: '0.3g*20粒', quantity: 1, price: 15.80, imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/215038/7/32814/42511/64c0f6e7F4e9b8e0c/9e18b4060f8d1c5c.jpg' }
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
      { name: '感冒灵颗粒', specification: '10g*9袋', quantity: 2, price: 18.50, imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/142975/40/39966/54723/66277f3dF3a5d0317/9e18b4060f8d1c5c.jpg' },
      { name: '维生素C片', specification: '100mg*100片', quantity: 1, price: 19.90, imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/238462/39/12747/42511/64c0f6e7F4e9b8e0c/9e18b4060f8d1c5c.jpg' }
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
      { name: '阿莫西林胶囊', specification: '0.25g*24粒', quantity: 1, price: 12.50, imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/184890/20/42278/35271/66277f3dF3a5d0317/9e18b4060f8d1c5c.jpg' }
    ],
    totalAmount: 12.50,
    deliveryFee: 5.00,
    createTime: '2024-12-03 09:15:00',
    deliveryType: 'self-pickup'
  }
]

// 模拟问诊记录数据
export const mockInquiryList = [
  {
    id: '1',
    doctorId: '1',
    doctorName: '刘贞君',
    doctorTitle: '主治医师',
    doctorAvatar: 'https://img.icons8.com/color/96/doctor-female.png',
    hospital: '山东青岛中西医结合医院',
    department: '皮肤科',
    patientId: '1',
    patientName: '彭聪',
    patientGender: 'male',
    patientAge: 41,
    symptom: '头疼，持续3天',
    price: 19.9,
    status: 'in_progress',
    createTime: '2026-04-14 10:30:00',
    isReviewed: false
  },
  {
    id: '2',
    doctorId: '2',
    doctorName: '张晓明',
    doctorTitle: '副主任医师',
    doctorAvatar: 'https://img.icons8.com/color/96/doctor-male.png',
    hospital: '北京协和医院',
    department: '呼吸内科',
    patientId: '2',
    patientName: '李小红',
    patientGender: 'female',
    patientAge: 35,
    symptom: '咳嗽，喉咙痛',
    price: 29.9,
    status: 'completed',
    createTime: '2026-04-13 15:20:00',
    isReviewed: false
  },
  {
    id: '3',
    doctorId: '3',
    doctorName: '李雪梅',
    doctorTitle: '主任医师',
    doctorAvatar: 'https://img.icons8.com/color/96/nurse-female.png',
    hospital: '上海红房子妇产科医院',
    department: '妇产科',
    patientId: '1',
    patientName: '彭聪',
    patientGender: 'male',
    patientAge: 41,
    symptom: '皮肤过敏，红肿',
    price: 39.9,
    status: 'completed',
    createTime: '2026-04-10 09:00:00',
    isReviewed: true
  },
  {
    id: '4',
    doctorId: '1',
    doctorName: '刘贞君',
    doctorTitle: '主治医师',
    doctorAvatar: 'https://img.icons8.com/color/96/doctor-female.png',
    hospital: '山东青岛中西医结合医院',
    department: '皮肤科',
    patientId: '1',
    patientName: '彭聪',
    patientGender: 'male',
    patientAge: 41,
    symptom: '湿疹复发',
    price: 19.9,
    status: 'pending',
    createTime: '2026-04-14 11:17:42',
    isReviewed: false
  }
]

// 模拟就诊人数据
export const mockPatients = [
  {
    id: '1',
    name: '李**',
    gender: '女',
    age: 35,
    idCard: '110101********1234',
    phone: '138****8888',
    isDefault: true,
    avatar: 'https://img.icons8.com/color/96/user-female.png'
  },
  {
    id: '2',
    name: '张**',
    gender: '男',
    age: 8,
    idCard: '110101********5678',
    phone: '138****8888',
    isDefault: false,
    avatar: 'https://img.icons8.com/color/96/boy.png'
  }
]

// 首页配置mock数据
export const mockHomePageConfig = {
  pageConfig: {
    title: '药品商城',
    theme: 'default',
    showLocation: true,
    showCart: true
  },
  sections: [
    {
      sectionId: 'search_bar',
      sectionType: 'search_bar',
      title: '搜索栏',
      subtitle: '',
      visible: true,
      sortOrder: 1,
      config: {
        placeholder: '搜索药品、症状、品牌',
        showLocation: true,
        showScan: true
      },
      components: []
    },
    {
      sectionId: 'tab_navigation',
      sectionType: 'tab_navigation',
      title: 'Tab导航',
      subtitle: '',
      visible: true,
      sortOrder: 2,
      config: {
        activeTab: 'recommend'
      },
      components: [
        {
          componentId: 'tabs',
          componentType: 'tab_list',
          data: [
            { id: 'recommend', name: '推荐', icon: 'Star' },
            { id: 'doctor', name: '问医生', icon: 'User' },
            { id: 'test', name: '做检测', icon: 'FirstAidKit' },
            { id: 'chronic', name: '慢病关怀', icon: 'Calendar' },
            { id: 'tcm', name: '中医保健', icon: 'Coffee' }
          ]
        }
      ]
    },
    {
      sectionId: 'promo_banner',
      sectionType: 'promo_banner',
      title: '促销横幅',
      subtitle: '',
      visible: true,
      sortOrder: 3,
      config: {},
      components: [
        {
          componentId: 'promo_items',
          componentType: 'promo_list',
          data: {
            left: { title: '过敏报告', tag: '过敏报告', bgColor: '#FFE4E1' },
            center: { title: '先维盈全球首发', subtitle: '不瘦必赔', brand: '美团买药', bgColor: '#FFD100' },
            right: { title: '过敏好物', tag: '过敏好物', bgColor: '#FFF8DC' }
          }
        }
      ]
    },
    {
      sectionId: 'service_grid',
      sectionType: 'service_grid',
      title: '24h服务',
      subtitle: '全部服务',
      visible: true,
      sortOrder: 4,
      config: {},
      components: [
        {
          componentId: 'services',
          componentType: 'service_list',
          data: mockCategories.map(cat => ({
            id: cat.id,
            name: cat.name,
            icon: cat.icon,
            iconUrl: cat.iconUrl,
            iconImg: cat.iconImg,
            bgColor: cat.bgColor
          }))
        }
      ]
    },
    {
      sectionId: 'banner_subsidy',
      sectionType: 'banner_subsidy',
      title: 'Banner和补贴',
      subtitle: '',
      visible: true,
      sortOrder: 5,
      config: {},
      components: [
        {
          componentId: 'banner_carousel',
          componentType: 'banner_carousel',
          data: [
            {
              id: '1',
              title: '新人大礼包',
              subtitle: '注册领100元券',
              tag: '立即领取',
              icon: '',
              iconUrl: 'https://img.icons8.com/color/96/gift.png',
              bgGradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              imageBg: 'rgba(255,255,255,0.2)',
              link: '/promotion/newuser'
            },
            {
              id: '2',
              title: '慢病专区',
              subtitle: '高血压糖尿病用药',
              tag: '满99减20',
              icon: '',
              iconUrl: 'https://img.icons8.com/color/96/pill.png',
              bgGradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
              imageBg: 'rgba(255,255,255,0.2)',
              link: '/category/chronic'
            },
            {
              id: '3',
              title: '家庭常备',
              subtitle: '感冒退烧药',
              tag: '第二件半价',
              icon: '',
              iconUrl: 'https://img.icons8.com/color/96/home.png',
              bgGradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
              imageBg: 'rgba(255,255,255,0.2)',
              link: '/category/family'
            }
          ]
        },
        {
          componentId: 'subsidy_list',
          componentType: 'subsidy_list',
          data: [
            { id: '1', name: '阿莫西林胶囊', price: '12.5', originalPrice: '18.0', bgColor: '#e8f5e9', imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/184890/20/42278/35271/66277f3dF3a5d0317/9e18b4060f8d1c5c.jpg' },
            { id: '2', name: '布洛芬缓释胶囊', price: '15.8', originalPrice: '22.0', bgColor: '#fff3e0', imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/215038/7/32814/42511/64c0f6e7F4e9b8e0c/9e18b4060f8d1c5c.jpg' },
            { id: '3', name: '感冒灵颗粒', price: '18.5', originalPrice: '25.0', bgColor: '#ffebee', imageUrl: 'https://img30.360buyimg.com/n1/jfs/t1/142975/40/39966/54723/66277f3dF3a5d0317/9e18b4060f8d1c5c.jpg' }
          ]
        }
      ]
    },
    {
      sectionId: 'doctor_banner',
      sectionType: 'doctor_banner',
      title: '问医生',
      subtitle: '',
      visible: true,
      sortOrder: 6,
      config: {},
      components: mockInquiryData.doctors.map(doc => ({
        componentId: `doctor_${doc.id}`,
        componentType: 'doctor_card',
        data: doc
      }))
    },
    {
      sectionId: 'nearby_pharmacy',
      sectionType: 'nearby_pharmacy',
      title: '附近急送',
      subtitle: '',
      visible: true,
      sortOrder: 7,
      config: {
        deliveryTag: '平均30分钟'
      },
      components: [
        {
          componentId: 'pharmacy_list',
          componentType: 'pharmacy_list',
          data: {
            filters: ['附近药店', '成人用品', '医疗器械', '隐形眼镜', '营养保健'],
            activeFilter: '附近药店',
            pharmacies: [
              {
                id: '1',
                name: '同仁堂大药房',
                logoText: '同仁堂',
                logoColor: '#c41e3a',
                logoUrl: 'https://img.icons8.com/color/96/pharmacy-shop.png',
                rating: 4.9,
                monthlySales: 1200,
                distance: 0.8,
                deliveryTime: 25,
                tags: [
                  { text: '医保定点', type: 'primary' },
                  { text: '24小时', type: 'success' }
                ]
              },
              {
                id: '2',
                name: '海王星辰健康药房',
                logoText: '海王',
                logoColor: '#1890ff',
                logoUrl: 'https://img.icons8.com/color/96/pharmacy-shop.png',
                rating: 4.8,
                monthlySales: 856,
                distance: 1.2,
                deliveryTime: 32,
                tags: [
                  { text: '满29免配送费', type: 'warning' }
                ]
              },
              {
                id: '3',
                name: '老百姓大药房',
                logoText: '老百姓',
                logoColor: '#52c41a',
                logoUrl: 'https://img.icons8.com/color/96/pharmacy-shop.png',
                rating: 4.7,
                monthlySales: 2341,
                distance: 1.5,
                deliveryTime: 35,
                tags: [
                  { text: '品牌连锁', type: 'primary' }
                ]
              }
            ]
          }
        }
      ]
    },
    {
      sectionId: 'waterfall_layout',
      sectionType: 'waterfall_layout',
      title: '推荐商品',
      subtitle: '',
      visible: true,
      sortOrder: 8,
      config: {},
      components: [
        {
          componentId: 'waterfall_items',
          componentType: 'waterfall_list',
          data: [
            // 广告卡片
            {
              type: 'ad',
              title: '春季养生',
              subtitle: '滋补好时节',
              btnText: '去看看',
              icon: '',
              iconUrl: 'https://img.icons8.com/color/96/spring.png',
              bgGradient: 'linear-gradient(135deg, #f5af19 0%, #f12711 100%)',
              link: '/promotion/spring'
            },
            // 商品卡片
            ...mockDrugs.map(drug => ({
              type: 'product',
              id: drug.id,
              name: drug.name,
              specification: drug.specification,
              price: drug.price,
              sales: Math.floor(drug.sales / 1000),
              deliveryTime: 30,
              isRx: drug.isRx,
              discount: drug.originalPrice ? Math.round((1 - drug.price / drug.originalPrice) * 100) : 0,
              imageColor: drug.imageColor,
              imageText: drug.imageText,
              imageUrl: drug.imageUrl
            }))
          ]
        }
      ]
    },
    // 问医生Tab内容
    {
      sectionId: 'doctor_department',
      sectionType: 'doctor_department',
      title: '问医生',
      subtitle: '',
      visible: true,
      sortOrder: 8,
      config: {},
      components: []
    },
    // 做检测Tab内容
    {
      sectionId: 'test_items',
      sectionType: 'test_items',
      title: '做检测',
      subtitle: '',
      visible: true,
      sortOrder: 9,
      config: {},
      components: []
    },
    // 慢病关怀Tab内容
    {
      sectionId: 'chronic_category',
      sectionType: 'chronic_category',
      title: '慢病关怀',
      subtitle: '',
      visible: true,
      sortOrder: 10,
      config: {},
      components: []
    },
    // 中医保健Tab内容
    {
      sectionId: 'tcm_category',
      sectionType: 'tcm_category',
      title: '中医保健',
      subtitle: '',
      visible: true,
      sortOrder: 11,
      config: {},
      components: []
    }
  ]
}

// 个人中心mock数据
export const mockUserCenterData = {
  userInfo: {
    id: '1',
    nickname: '健康达人',
    phone: '138****8888',
    avatar: 'https://img.icons8.com/color/96/user.png',
    level: 'VIP会员',
    points: 2580
  },
  orderStats: {
    pendingPayment: 2,
    pendingShipment: 1,
    pendingReceipt: 3,
    pendingReview: 0
  },
  defaultPatient: {
    id: '1',
    name: '李**',
    gender: 'female',
    age: 35
  }
}
