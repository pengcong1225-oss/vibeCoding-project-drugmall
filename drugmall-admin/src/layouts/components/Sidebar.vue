<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SidebarItem from './SidebarItem.vue'

const props = defineProps<{
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

// 菜单列表
const menuList = [
  {
    path: '/dashboard',
    meta: { title: '仪表盘', icon: 'Odometer' }
  },
  {
    path: '/user',
    meta: { title: '用户管理', icon: 'User' },
    children: [
      { path: '/user/list', meta: { title: '用户列表' } }
    ]
  },
  {
    path: '/drug',
    meta: { title: '药品管理', icon: 'FirstAidKit' },
    children: [
      { path: '/drug/list', meta: { title: '药品列表' } },
      { path: '/drug/category', meta: { title: '分类管理' } },
      { path: '/drug/brand', meta: { title: '品牌管理' } }
    ]
  },
  {
    path: '/order',
    meta: { title: '订单管理', icon: 'ShoppingCart' },
    children: [
      { path: '/order/list', meta: { title: '订单列表' } }
    ]
  },
  {
    path: '/finance',
    meta: { title: '财务管理', icon: 'Wallet' },
    children: [
      { path: '/finance/statistics', meta: { title: '收入统计' } },
      { path: '/finance/transactions', meta: { title: '交易流水' } }
    ]
  },
  {
    path: '/content',
    meta: { title: '内容管理', icon: 'Document' },
    children: [
      { path: '/content/banner', meta: { title: 'Banner管理' } },
      { path: '/content/notice', meta: { title: '公告管理' } }
    ]
  },
  {
    path: '/settings',
    meta: { title: '系统设置', icon: 'Setting' },
    children: [
      { path: '/settings/basic', meta: { title: '基础配置' } }
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
