<template>
  <div class="main-layout">
    <!-- 页面内容 -->
    <div class="page-content" :class="{ 'has-tabbar': showTabbar }">
      <router-view v-slot="{ Component }">
        <keep-alive :include="cachedViews">
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </div>
    
    <!-- 底部导航栏 -->
    <Tabbar v-if="showTabbar" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import Tabbar from '@/components/Tabbar/index.vue'

const route = useRoute()

// 需要缓存的页面
const cachedViews = ref(['Home', 'Category', 'Cart', 'User'])

// 是否显示底部导航栏
const showTabbar = computed(() => {
  const noTabbarRoutes = ['Search', 'DrugDetail', 'OrderConfirm', 'OrderPay', 'Login']
  return !noTabbarRoutes.includes(route.name as string)
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.main-layout {
  min-height: 100vh;
  background-color: $bg-primary;
}

.page-content {
  min-height: 100vh;
  
  &.has-tabbar {
    padding-bottom: calc($tabbar-height + $safe-area-bottom + 10px);
  }
}
</style>
