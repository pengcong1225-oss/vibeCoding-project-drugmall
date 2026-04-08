import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置 NProgress
NProgress.configure({ showSpinner: false })

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: '/user',
        name: 'User',
        meta: { title: '用户管理', icon: 'User' },
        children: [
          {
            path: '/user/list',
            name: 'UserList',
            component: () => import('@/views/user/index.vue'),
            meta: { title: '用户列表' }
          }
        ]
      },
      {
        path: '/drug',
        name: 'Drug',
        meta: { title: '药品管理', icon: 'FirstAidKit' },
        children: [
          {
            path: '/drug/list',
            name: 'DrugList',
            component: () => import('@/views/drug/index.vue'),
            meta: { title: '药品列表' }
          },
          {
            path: '/drug/category',
            name: 'DrugCategory',
            component: () => import('@/views/drug/category.vue'),
            meta: { title: '分类管理' }
          },
          {
            path: '/drug/brand',
            name: 'DrugBrand',
            component: () => import('@/views/drug/brand.vue'),
            meta: { title: '品牌管理' }
          }
        ]
      },
      {
        path: '/order',
        name: 'Order',
        meta: { title: '订单管理', icon: 'ShoppingCart' },
        children: [
          {
            path: '/order/list',
            name: 'OrderList',
            component: () => import('@/views/order/index.vue'),
            meta: { title: '订单列表' }
          }
        ]
      },
      {
        path: '/finance',
        name: 'Finance',
        meta: { title: '财务管理', icon: 'Wallet' },
        children: [
          {
            path: '/finance/statistics',
            name: 'FinanceStatistics',
            component: () => import('@/views/finance/statistics.vue'),
            meta: { title: '收入统计' }
          },
          {
            path: '/finance/transactions',
            name: 'FinanceTransactions',
            component: () => import('@/views/finance/transactions.vue'),
            meta: { title: '交易流水' }
          }
        ]
      },
      {
        path: '/content',
        name: 'Content',
        meta: { title: '内容管理', icon: 'Document' },
        children: [
          {
            path: '/content/banner',
            name: 'ContentBanner',
            component: () => import('@/views/content/banner.vue'),
            meta: { title: 'Banner管理' }
          },
          {
            path: '/content/notice',
            name: 'ContentNotice',
            component: () => import('@/views/content/notice.vue'),
            meta: { title: '公告管理' }
          }
        ]
      },
      {
        path: '/settings',
        name: 'Settings',
        meta: { title: '系统设置', icon: 'Setting' },
        children: [
          {
            path: '/settings/basic',
            name: 'SettingsBasic',
            component: () => import('@/views/settings/basic.vue'),
            meta: { title: '基础配置' }
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', hidden: true }
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
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  const userStore = useUserStore()
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - DrugMall管理后台` : 'DrugMall管理后台'
  
  // 白名单路由
  const whiteList = ['/login']
  
  if (whiteList.includes(to.path)) {
    next()
    return
  }
  
  // 检查是否登录
  const token = userStore.token
  if (!token) {
    next(`/login?redirect=${to.path}`)
    return
  }
  
  // 获取用户信息
  if (!userStore.userInfo) {
    try {
      await userStore.getUserInfo()
    } catch (error) {
      userStore.logout()
      next(`/login?redirect=${to.path}`)
      return
    }
  }
  
  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router
