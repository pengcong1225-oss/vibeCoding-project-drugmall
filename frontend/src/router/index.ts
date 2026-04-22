import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页', keepAlive: true }
      },
      {
        path: '/category',
        name: 'Category',
        component: () => import('@/views/category/index.vue'),
        meta: { title: '分类', keepAlive: true }
      },
      {
        path: '/cart',
        name: 'Cart',
        component: () => import('@/views/cart/index.vue'),
        meta: { title: '购物车', keepAlive: true }
      },
      {
        path: '/user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '我的', keepAlive: true }
      }
    ]
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/search/index.vue'),
    meta: { title: '搜索' }
  },
  {
    path: '/drug/:id',
    name: 'DrugDetail',
    component: () => import('@/views/drug/detail.vue'),
    meta: { title: '药品详情' }
  },
  {
    path: '/order/confirm',
    name: 'OrderConfirm',
    component: () => import('@/views/order/confirm.vue'),
    meta: { title: '确认订单' }
  },
  {
    path: '/order/pay',
    name: 'OrderPay',
    component: () => import('@/views/order/pay.vue'),
    meta: { title: '支付' }
  },
  {
    path: '/order/list',
    name: 'OrderList',
    component: () => import('@/views/order/list.vue'),
    meta: { title: '我的订单' }
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('@/views/order/detail.vue'),
    meta: { title: '订单详情' }
  },
  {
    path: '/prescription',
    name: 'PrescriptionList',
    component: () => import('@/views/prescription/list.vue'),
    meta: { title: '我的处方' }
  },
  {
    path: '/prescription/apply',
    name: 'PrescriptionApply',
    component: () => import('@/views/prescription/apply.vue'),
    meta: { title: '补充处方信息' }
  },
  {
    path: '/prescription/consult',
    name: 'PrescriptionConsult',
    component: () => import('@/views/prescription/consult.vue'),
    meta: { title: '复诊开方' }
  },
  {
    path: '/prescription/success',
    name: 'PrescriptionSuccess',
    component: () => import('@/views/prescription/success.vue'),
    meta: { title: '处方开具成功' }
  },
  {
    path: '/inquiry',
    name: 'Inquiry',
    component: () => import('@/views/inquiry/index.vue'),
    meta: { title: '问诊' }
  },
  {
    path: '/inquiry/list',
    name: 'InquiryList',
    component: () => import('@/views/inquiry/list.vue'),
    meta: { title: '咨询记录' }
  },
  {
    path: '/inquiry/pre/:doctorId?',
    name: 'InquiryPre',
    component: () => import('@/views/inquiry/pre.vue'),
    meta: { title: '专家问诊' }
  },
  {
    path: '/inquiry/triage/:departmentCode',
    name: 'DepartmentTriage',
    component: () => import('@/views/inquiry/department-triage.vue'),
    meta: { title: '科室导诊台' }
  },
  {
    path: '/inquiry/pay/:consultationId',
    name: 'InquiryPay',
    component: () => import('@/views/inquiry/pay.vue'),
    meta: { title: '订单支付' }
  },
  {
    path: '/inquiry/checkout/:consultationId',
    name: 'InquiryCheckout',
    component: () => import('@/views/inquiry/checkout.vue'),
    meta: { title: '收银台' }
  },
  {
    path: '/inquiry/waiting/:consultationId',
    name: 'InquiryWaiting',
    component: () => import('@/views/inquiry/waiting.vue'),
    meta: { title: '等待接诊' }
  },
  {
    path: '/inquiry/chat',
    name: 'InquiryChat',
    component: () => import('@/views/inquiry/chat.vue'),
    meta: { title: '问诊会话' }
  },
  {
    path: '/doctor/:id',
    name: 'DoctorDetail',
    component: () => import('@/views/doctor/detail.vue'),
    meta: { title: '医生详情' }
  },
  {
    path: '/ai-assistant',
    name: 'AiAssistant',
    component: () => import('@/views/ai-assistant/index.vue'),
    meta: { title: '百姓健康管家' }
  },
  {
    path: '/inquiry/ai-triage',
    name: 'InquiryAiTriage',
    component: () => import('@/views/inquiry/ai-assistant.vue'),
    meta: { title: 'AI导诊助手' }
  },
  {
    path: '/symptom-test',
    name: 'SymptomTest',
    component: () => import('@/views/symptom-test/index.vue'),
    meta: { title: '症状自测' }
  },
  {
    path: '/test-service',
    name: 'TestService',
    component: () => import('@/views/test-service/index.vue'),
    meta: { title: '做检测', keepAlive: true }
  },
  {
    path: '/patient',
    name: 'PatientList',
    component: () => import('@/views/patient/index.vue'),
    meta: { title: '就诊人管理' }
  },
  {
    path: '/patient/add',
    name: 'PatientAdd',
    component: () => import('@/views/patient/add.vue'),
    meta: { title: '添加就诊人' }
  },
  {
    path: '/patient/edit',
    name: 'PatientEdit',
    component: () => import('@/views/patient/edit.vue'),
    meta: { title: '编辑就诊人' }
  },
  {
    path: '/address',
    name: 'AddressList',
    component: () => import('@/views/address/list.vue'),
    meta: { title: '收货地址' }
  },
  {
    path: '/store/:id',
    name: 'StoreDetail',
    component: () => import('@/views/store/detail.vue'),
    meta: { title: '药店详情' }
  },
  {
    path: '/store/:storeId/drug/:drugId',
    name: 'StoreDrugDetail',
    component: () => import('@/views/store/drug-detail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/help',
    name: 'Help',
    component: () => import('@/views/help/index.vue'),
    meta: { title: '帮助中心' }
  },
  {
    path: '/feedback',
    name: 'Feedback',
    component: () => import('@/views/feedback/index.vue'),
    meta: { title: '意见反馈' }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/settings/index.vue'),
    meta: { title: '设置' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 药康购` : '药康购 - 24小时送药上门'
  
  // 简单的登录验证
  const token = localStorage.getItem('token')
  
  // 需要登录的页面
  const needAuthRoutes = [
    'OrderConfirm',
    'OrderPay',
    'OrderList',
    'OrderDetail',
    'PrescriptionList',
    'InquiryList',
    'InquiryPre',
    'InquiryPay',
    'InquiryCheckout',
    'InquiryWaiting',
    'InquiryChat',
    'PatientList',
    'AddressList',
    'Settings'
  ]
  
  if (needAuthRoutes.includes(to.name as string) && !token) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else {
    next()
  }
})

export default router
