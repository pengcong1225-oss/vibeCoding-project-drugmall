import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
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
        path: '/dashboard/realtime',
        name: 'DashboardRealtime',
        component: () => import('@/views/dashboard/realtime.vue'),
        meta: { title: '实时看板', hidden: true }
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
          },
          {
            path: '/user/detail/:id',
            name: 'UserDetail',
            component: () => import('@/views/user/detail.vue'),
            meta: { title: '用户详情', hidden: true }
          },
          {
            path: '/user/auth',
            name: 'UserAuth',
            component: () => import('@/views/user/auth.vue'),
            meta: { title: '实名认证' }
          },
          {
            path: '/user/patient',
            name: 'UserPatient',
            component: () => import('@/views/user/patient.vue'),
            meta: { title: '就诊人管理' }
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
          },
          {
            path: '/drug/edit',
            name: 'DrugEdit',
            component: () => import('@/views/drug/edit.vue'),
            meta: { title: '药品编辑', hidden: true }
          },
          {
            path: '/drug/audit',
            name: 'DrugAudit',
            component: () => import('@/views/drug/audit.vue'),
            meta: { title: '药品审核' }
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
          },
          {
            path: '/order/refund',
            name: 'OrderRefund',
            component: () => import('@/views/order/refund.vue'),
            meta: { title: '退款管理' }
          },
          {
            path: '/order/abnormal',
            name: 'OrderAbnormal',
            component: () => import('@/views/order/abnormal.vue'),
            meta: { title: '异常订单' }
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
          },
          {
            path: '/finance/withdrawal',
            name: 'FinanceWithdrawal',
            component: () => import('@/views/finance/withdrawal.vue'),
            meta: { title: '提现管理' }
          },
          {
            path: '/finance/settlement',
            name: 'FinanceSettlement',
            component: () => import('@/views/finance/settlement.vue'),
            meta: { title: '医生结算' }
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
          },
          {
            path: '/content/article',
            name: 'ContentArticle',
            component: () => import('@/views/content/article.vue'),
            meta: { title: '资讯管理' }
          },
          {
            path: '/content/help',
            name: 'ContentHelp',
            component: () => import('@/views/content/help.vue'),
            meta: { title: '帮助中心' }
          }
        ]
      },
      // 运营管理模块
      {
        path: '/operation',
        name: 'Operation',
        meta: { title: '运营管理', icon: 'Monitor' },
        children: [
          {
            path: '/operation/feedback',
            name: 'OperationFeedback',
            component: () => import('@/views/operation/feedback.vue'),
            meta: { title: '意见反馈' }
          },
          {
            path: '/operation/complaint',
            name: 'OperationComplaint',
            component: () => import('@/views/operation/complaint.vue'),
            meta: { title: '投诉管理' }
          },
          {
            path: '/operation/symptom',
            name: 'OperationSymptom',
            component: () => import('@/views/operation/symptom.vue'),
            meta: { title: '症状题库' }
          },
          {
            path: '/operation/ai-config',
            name: 'OperationAIConfig',
            component: () => import('@/views/operation/ai-config.vue'),
            meta: { title: 'AI配置' }
          }
        ]
      },
      // 首页配置模块
      {
        path: '/home-config',
        name: 'HomeConfig',
        meta: { title: '首页配置', icon: 'HomeFilled' },
        children: [
          {
            path: '/home-config/global',
            name: 'HomeConfigGlobal',
            component: () => import('@/views/home-config/global.vue'),
            meta: { title: '全局设置' }
          },
          {
            path: '/home-config/tabs',
            name: 'HomeConfigTabs',
            component: () => import('@/views/home-config/tab.vue'),
            meta: { title: 'Tab管理' }
          },
          {
            path: '/home-config/sections',
            name: 'HomeConfigSections',
            component: () => import('@/views/home-config/section.vue'),
            meta: { title: '模块管理' }
          },
          {
            path: '/home-config/banners',
            name: 'HomeConfigBanners',
            component: () => import('@/views/home-config/banner.vue'),
            meta: { title: '轮播图管理' }
          },
          {
            path: '/home-config/kingkong',
            name: 'HomeConfigKingKong',
            component: () => import('@/views/home-config/kingkong.vue'),
            meta: { title: '金刚位管理' }
          },
          {
            path: '/home-config/adslots',
            name: 'HomeConfigAdSlots',
            component: () => import('@/views/home-config/adslot.vue'),
            meta: { title: '广告位管理' }
          },
          {
            path: '/home-config/release',
            name: 'HomeConfigRelease',
            component: () => import('@/views/home-config/release.vue'),
            meta: { title: '发布管理' }
          }
        ]
      },
      // 医生管理模块
      {
        path: '/doctor',
        name: 'Doctor',
        meta: { title: '医生管理', icon: 'UserFilled' },
        children: [
          {
            path: '/doctor/list',
            name: 'DoctorList',
            component: () => import('@/views/doctor/list.vue'),
            meta: { title: '医生列表' }
          },
          {
            path: '/doctor/detail/:id',
            name: 'DoctorDetail',
            component: () => import('@/views/doctor/detail.vue'),
            meta: { title: '医生详情', hidden: true }
          },
          {
            path: '/doctor/audit',
            name: 'DoctorAudit',
            component: () => import('@/views/doctor/audit.vue'),
            meta: { title: '入驻审核' }
          },
          {
            path: '/doctor/schedule/:id',
            name: 'DoctorSchedule',
            component: () => import('@/views/doctor/schedule.vue'),
            meta: { title: '排班管理', hidden: true }
          },
          {
            path: '/doctor/department',
            name: 'DoctorDepartment',
            component: () => import('@/views/doctor/department.vue'),
            meta: { title: '科室管理' }
          }
        ]
      },
      // 问诊管理模块
      {
        path: '/consultation',
        name: 'Consultation',
        meta: { title: '问诊管理', icon: 'ChatDotRound' },
        children: [
          {
            path: '/consultation/list',
            name: 'ConsultationList',
            component: () => import('@/views/consultation/list.vue'),
            meta: { title: '问诊列表' }
          },
          {
            path: '/consultation/detail/:id',
            name: 'ConsultationDetail',
            component: () => import('@/views/consultation/detail.vue'),
            meta: { title: '问诊详情', hidden: true }
          },
          {
            path: '/consultation/assign',
            name: 'ConsultationAssign',
            component: () => import('@/views/consultation/assign.vue'),
            meta: { title: '问诊分配' }
          },
          {
            path: '/consultation/exception',
            name: 'ConsultationException',
            component: () => import('@/views/consultation/exception.vue'),
            meta: { title: '异常问诊' }
          },
          {
            path: '/consultation/stats',
            name: 'ConsultationStats',
            component: () => import('@/views/consultation/stats.vue'),
            meta: { title: '问诊统计' }
          }
        ]
      },
      // 处方管理模块
      {
        path: '/prescription',
        name: 'Prescription',
        meta: { title: '处方管理', icon: 'Tickets' },
        children: [
          {
            path: '/prescription/list',
            name: 'PrescriptionList',
            component: () => import('@/views/prescription/list.vue'),
            meta: { title: '处方列表' }
          },
          {
            path: '/prescription/audit/:id',
            name: 'PrescriptionAudit',
            component: () => import('@/views/prescription/audit.vue'),
            meta: { title: '处方审核', hidden: true }
          },
          {
            path: '/prescription/detail/:id',
            name: 'PrescriptionDetail',
            component: () => import('@/views/prescription/detail.vue'),
            meta: { title: '处方详情', hidden: true }
          },
          {
            path: '/prescription/template',
            name: 'PrescriptionTemplate',
            component: () => import('@/views/prescription/template.vue'),
            meta: { title: '处方模板' }
          },
          {
            path: '/prescription/stats',
            name: 'PrescriptionStats',
            component: () => import('@/views/prescription/stats.vue'),
            meta: { title: '处方统计' }
          }
        ]
      },
      // 门店管理模块
      {
        path: '/store',
        name: 'Store',
        meta: { title: '门店管理', icon: 'Shop' },
        children: [
          {
            path: '/store/list',
            name: 'StoreList',
            component: () => import('@/views/store/list.vue'),
            meta: { title: '门店列表' }
          },
          {
            path: '/store/detail/:id',
            name: 'StoreDetail',
            component: () => import('@/views/store/detail.vue'),
            meta: { title: '门店详情', hidden: true }
          },
          {
            path: '/store/audit',
            name: 'StoreAudit',
            component: () => import('@/views/store/audit.vue'),
            meta: { title: '门店审核' }
          },
          {
            path: '/store/drugs/:id',
            name: 'StoreDrugs',
            component: () => import('@/views/store/drugs.vue'),
            meta: { title: '门店药品', hidden: true }
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
          },
          {
            path: '/settings/payment',
            name: 'SettingsPayment',
            component: () => import('@/views/settings/payment.vue'),
            meta: { title: '支付配置' }
          },
          {
            path: '/settings/permission',
            name: 'SettingsPermission',
            component: () => import('@/views/settings/permission.vue'),
            meta: { title: '权限管理' }
          },
          {
            path: '/settings/log',
            name: 'SettingsLog',
            component: () => import('@/views/settings/log.vue'),
            meta: { title: '操作日志' }
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
router.beforeEach(async (to, _from, next) => {
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
      const info = await userStore.getUserInfo()
      if (!info) {
        next(`/login?redirect=${to.path}`)
        return
      }
    } catch {
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
