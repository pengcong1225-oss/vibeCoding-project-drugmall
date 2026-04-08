import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { noAuth: true }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/index.vue'),
    meta: { title: '工作台', tabbar: true }
  },
  {
    path: '/consultation',
    name: 'Consultation',
    component: () => import('@/views/consultation/index.vue'),
    meta: { title: '问诊管理', tabbar: true }
  },
  {
    path: '/consultation/chat/:id',
    name: 'Chat',
    component: () => import('@/views/consultation/chat.vue'),
    meta: { title: '问诊会话' }
  },
  {
    path: '/consultation/detail/:id',
    name: 'ConsultationDetail',
    component: () => import('@/views/consultation/detail.vue'),
    meta: { title: '问诊详情' }
  },
  {
    path: '/prescription',
    name: 'Prescription',
    component: () => import('@/views/prescription/index.vue'),
    meta: { title: '处方管理' }
  },
  {
    path: '/prescription/create',
    name: 'CreatePrescription',
    component: () => import('@/views/prescription/create.vue'),
    meta: { title: '开具处方' }
  },
  {
    path: '/prescription/detail/:id',
    name: 'PrescriptionDetail',
    component: () => import('@/views/prescription/detail.vue'),
    meta: { title: '处方详情' }
  },
  {
    path: '/patients',
    name: 'Patients',
    component: () => import('@/views/patients/index.vue'),
    meta: { title: '患者管理', tabbar: true }
  },
  {
    path: '/patients/detail/:id',
    name: 'PatientDetail',
    component: () => import('@/views/patients/detail.vue'),
    meta: { title: '患者详情' }
  },
  {
    path: '/patients/records/:id',
    name: 'MedicalRecords',
    component: () => import('@/views/patients/records.vue'),
    meta: { title: '就诊记录' }
  },
  {
    path: '/income',
    name: 'Income',
    component: () => import('@/views/income/index.vue'),
    meta: { title: '我的收入' }
  },
  {
    path: '/income/detail',
    name: 'IncomeDetail',
    component: () => import('@/views/income/detail.vue'),
    meta: { title: '收入明细' }
  },
  {
    path: '/income/withdraw',
    name: 'Withdraw',
    component: () => import('@/views/income/withdraw.vue'),
    meta: { title: '提现申请' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/index.vue'),
    meta: { title: '我的', tabbar: true }
  },
  {
    path: '/profile/edit',
    name: 'EditProfile',
    component: () => import('@/views/profile/edit.vue'),
    meta: { title: '编辑资料' }
  },
  {
    path: '/profile/schedule',
    name: 'Schedule',
    component: () => import('@/views/profile/schedule.vue'),
    meta: { title: '我的排班' }
  },
  {
    path: '/profile/settings',
    name: 'Settings',
    component: () => import('@/views/profile/settings.vue'),
    meta: { title: '设置' }
  },
  {
    path: '/profile/license',
    name: 'License',
    component: () => import('@/views/profile/license.vue'),
    meta: { title: '执业信息' }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - DrugMall医生端`
  }
  
  // 检查登录状态
  const token = localStorage.getItem('token')
  
  if (to.meta.noAuth) {
    // 不需要登录的页面
    if (token && to.name === 'Login') {
      // 已登录用户访问登录页，跳转到首页
      next('/home')
    } else {
      next()
    }
  } else {
    // 需要登录的页面
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
