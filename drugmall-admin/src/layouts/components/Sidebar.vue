<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SidebarItem from './SidebarItem.vue'

defineProps<{
  isCollapse: boolean
}>()

const route = useRoute()

// 当前激活的菜单
const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu as string
  }
  return path
})

// 完整菜单列表（与路由配置保持一致）
const menuList = [
  {
    path: '/dashboard',
    meta: { title: '仪表盘', icon: 'Odometer' }
  },
  {
    path: '/user',
    meta: { title: '用户管理', icon: 'User' },
    children: [
      { path: '/user/list', meta: { title: '用户列表' } },
      { path: '/user/auth', meta: { title: '实名认证' } },
      { path: '/user/patient', meta: { title: '就诊人管理' } }
    ]
  },
  {
    path: '/doctor',
    meta: { title: '医生管理', icon: 'UserFilled' },
    children: [
      { path: '/doctor/list', meta: { title: '医生列表' } },
      { path: '/doctor/audit', meta: { title: '入驻审核' } },
      { path: '/doctor/department', meta: { title: '科室管理' } }
    ]
  },
  {
    path: '/drug',
    meta: { title: '药品管理', icon: 'FirstAidKit' },
    children: [
      { path: '/drug/list', meta: { title: '药品列表' } },
      { path: '/drug/category', meta: { title: '分类管理' } },
      { path: '/drug/brand', meta: { title: '品牌管理' } },
      { path: '/drug/audit', meta: { title: '药品审核' } }
    ]
  },
  {
    path: '/store',
    meta: { title: '门店管理', icon: 'Shop' },
    children: [
      { path: '/store/list', meta: { title: '门店列表' } },
      { path: '/store/audit', meta: { title: '门店审核' } }
    ]
  },
  {
    path: '/order',
    meta: { title: '订单管理', icon: 'ShoppingCart' },
    children: [
      { path: '/order/list', meta: { title: '订单列表' } },
      { path: '/order/refund', meta: { title: '退款管理' } },
      { path: '/order/abnormal', meta: { title: '异常订单' } }
    ]
  },
  {
    path: '/consultation',
    meta: { title: '问诊管理', icon: 'ChatDotRound' },
    children: [
      { path: '/consultation/list', meta: { title: '问诊列表' } },
      { path: '/consultation/assign', meta: { title: '问诊分配' } },
      { path: '/consultation/exception', meta: { title: '异常问诊' } },
      { path: '/consultation/stats', meta: { title: '问诊统计' } }
    ]
  },
  {
    path: '/prescription',
    meta: { title: '处方管理', icon: 'Tickets' },
    children: [
      { path: '/prescription/list', meta: { title: '处方列表' } },
      { path: '/prescription/template', meta: { title: '处方模板' } },
      { path: '/prescription/stats', meta: { title: '处方统计' } }
    ]
  },
  {
    path: '/finance',
    meta: { title: '财务管理', icon: 'Wallet' },
    children: [
      { path: '/finance/statistics', meta: { title: '收入统计' } },
      { path: '/finance/transactions', meta: { title: '交易流水' } },
      { path: '/finance/withdrawal', meta: { title: '提现管理' } },
      { path: '/finance/settlement', meta: { title: '医生结算' } }
    ]
  },
  {
    path: '/home-config',
    meta: { title: '首页配置', icon: 'HomeFilled' },
    children: [
      { path: '/home-config/global', meta: { title: '全局设置' } },
      { path: '/home-config/tabs', meta: { title: 'Tab管理' } },
      { path: '/home-config/sections', meta: { title: '模块管理' } },
      { path: '/home-config/banners', meta: { title: '轮播图管理' } },
      { path: '/home-config/kingkong', meta: { title: '金刚位管理' } },
      { path: '/home-config/adslots', meta: { title: '广告位管理' } },
      { path: '/home-config/release', meta: { title: '发布管理' } }
    ]
  },
  {
    path: '/content',
    meta: { title: '内容管理', icon: 'Document' },
    children: [
      { path: '/content/banner', meta: { title: 'Banner管理' } },
      { path: '/content/notice', meta: { title: '公告管理' } },
      { path: '/content/article', meta: { title: '资讯管理' } },
      { path: '/content/help', meta: { title: '帮助中心' } }
    ]
  },
  {
    path: '/operation',
    meta: { title: '运营管理', icon: 'Monitor' },
    children: [
      { path: '/operation/feedback', meta: { title: '意见反馈' } },
      { path: '/operation/complaint', meta: { title: '投诉管理' } },
      { path: '/operation/symptom', meta: { title: '症状题库' } },
      { path: '/operation/ai-config', meta: { title: 'AI配置' } }
    ]
  },
  {
    path: '/settings',
    meta: { title: '系统设置', icon: 'Setting' },
    children: [
      { path: '/settings/basic', meta: { title: '基础配置' } },
      { path: '/settings/payment', meta: { title: '支付配置' } },
      { path: '/settings/permission', meta: { title: '权限管理' } },
      { path: '/settings/log', meta: { title: '操作日志' } }
    ]
  }
]
</script>

<template>
  <div class="sidebar" :class="{ 'is-collapse': isCollapse }">
    <!-- Logo -->
    <div class="sidebar-logo">
      <div class="logo-icon">💊</div>
      <span v-show="!isCollapse" class="title">DrugMall</span>
    </div>
    
    <!-- 菜单 -->
    <el-scrollbar class="sidebar-menu-container">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <sidebar-item
          v-for="route in menuList"
          :key="route.path"
          :item="route"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<style scoped lang="scss">
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: 220px;
  background-color: #304156;
  transition: width 0.3s;
  z-index: 1000;
  
  &.is-collapse {
    width: 64px;
  }
}

.sidebar-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64px;
  padding: 0 16px;
  background-color: #2b3649;
  
  .logo-icon {
    width: 32px;
    height: 32px;
    font-size: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .title {
    margin-left: 12px;
    font-size: 18px;
    font-weight: 600;
    color: #fff;
    white-space: nowrap;
  }
}

.sidebar-menu-container {
  height: calc(100% - 64px);
  
  :deep(.el-menu) {
    border-right: none;
  }
}
</style>
