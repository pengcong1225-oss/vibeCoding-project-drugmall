<template>
  <div class="tabbar">
    <div class="tabbar-content">
      <router-link
        v-for="item in tabs"
        :key="item.path"
        :to="item.path"
        class="tab-item"
        :class="{ active: activePath === item.path }"
      >
        <div class="tab-icon">
          <el-icon :size="24">
            <component :is="activePath === item.path ? item.activeIcon : item.icon" />
          </el-icon>
          <div v-if="item.badge" class="badge">{{ item.badge }}</div>
        </div>
        <span class="tab-text">{{ item.name }}</span>
      </router-link>
    </div>
    <!-- 安全区域占位 -->
    <div class="safe-area-placeholder"></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const cartStore = useCartStore()

const activePath = computed(() => route.path)

const tabs = computed(() => [
  {
    name: '首页',
    path: '/home',
    icon: 'HomeFilled',
    activeIcon: 'HomeFilled'
  },
  {
    name: '分类',
    path: '/category',
    icon: 'Grid',
    activeIcon: 'Grid'
  },
  {
    name: '购物车',
    path: '/cart',
    icon: 'ShoppingCart',
    activeIcon: 'ShoppingCartFull',
    badge: cartStore.totalCount > 0 ? cartStore.totalCount : undefined
  },
  {
    name: '我的',
    path: '/user',
    icon: 'User',
    activeIcon: 'UserFilled'
  }
])
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: $bg-white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.tabbar-content {
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: $tabbar-height;
  padding: 0 $spacing-md;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  height: 100%;
  color: $text-tertiary;
  text-decoration: none;
  transition: color 0.2s ease;

  &.active {
    color: $primary;
  }
}

.tab-icon {
  position: relative;
  margin-bottom: 2px;
}

.tab-text {
  font-size: $font-xs;
  font-weight: 500;
}

.badge {
  position: absolute;
  top: -6px;
  right: -10px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: $error;
  color: $text-white;
  font-size: 10px;
  font-weight: bold;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.safe-area-placeholder {
  height: $safe-area-bottom;
}
</style>
