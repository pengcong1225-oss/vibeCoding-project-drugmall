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
    path: '/inquiry',
    name: 'Inquiry',
    component: () => import('@/views/inquiry/index.vue'),
    meta: { title: '问诊' }
  },
  {
    path: '/inquiry/chat',
    name: 'InquiryChat',
    component: () => import('@/views/inquiry/chat.vue'),
    meta: { title: '问诊会话' }
  },
  {
    path: '/ai-assistant',
    name: 'AiAssistant',
    component: () => import('@/views/ai-assistant/index.vue'),
    meta: { title: 'AI助手' }
  },
  {
    path: '/patient',
    name: 'PatientList',
    component: () => import('@/views/patient/list.vue'),
    meta: { title: '就诊人管理' }
  },
  {
    path: '/address',
    name: 'AddressList',
    component: () => import('@/views/address/list.vue'),
    meta: { title: '收货地址' }
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
  const needAuth = !['Login', 'Home', 'Category', 'Search', 'DrugDetail'].includes(to.name as string)
  
  if (needAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
