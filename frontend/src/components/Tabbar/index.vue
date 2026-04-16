<template>
  <div class="tabbar">
    <div class="tabbar-inner">
      <div class="tabbar-content">
      <router-link
        v-for="item in tabs"
        :key="item.path"
        :to="item.path"
        class="tab-item"
        :class="{ 
          active: activePath === item.path,
          'center-item': item.id === 'health-butler'
        }"
      >
        <!-- 中间健康管家 - 特殊样式 -->
        <template v-if="item.id === 'health-butler'">
          <div class="center-icon-wrapper">
            <img 
              src="/images/yige-logo.png" 
              class="center-icon"
              alt="宜格健康管家"
            />
          </div>
          <span class="center-text">{{ item.name }}</span>
        </template>
        
        <!-- 普通Tab项 -->
        <template v-else>
          <div class="tab-icon" :class="{ active: activePath === item.path }">
            <img 
              v-if="item.iconUrl" 
              :src="activePath === item.path ? item.activeIconUrl : item.iconUrl" 
              class="tab-icon-img"
              :alt="item.name"
            />
            <el-icon v-else :size="22">
              <component :is="activePath === item.path ? item.activeIcon : item.icon" />
            </el-icon>
          </div>
          <span class="tab-text" :class="{ active: activePath === item.path }">{{ item.name }}</span>
        </template>
      </router-link>
      </div>
      <!-- 安全区域占位 -->
      <div class="safe-area-placeholder"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activePath = computed(() => route.path)

const tabs = computed(() => [
  {
    id: 'buy',
    name: '买药',
    path: '/home',
    icon: 'FirstAidKit',
    activeIcon: 'FirstAidKit',
    iconUrl: '',
    activeIconUrl: ''
  },
  {
    id: 'health-butler',
    name: '宜格健康管家',
    path: '/ai-assistant',
    icon: 'ChatDotRound',
    activeIcon: 'ChatDotRound',
    iconUrl: '',
    activeIconUrl: ''
  },
  {
    id: 'profile',
    name: '我的',
    path: '/user',
    icon: 'User',
    activeIcon: 'UserFilled',
    iconUrl: '',
    activeIconUrl: ''
  }
])
</script>

<style scoped lang="scss">
$primary-yellow: #FFD100;
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;

.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 9999;
  border-top: 1px solid #f0f0f0;
}

.tabbar-inner {
  max-width: 430px;
  margin: 0 auto;
}

.tabbar-content {
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 56px;
  padding: 0 20px;
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
  transition: all 0.2s ease;
  position: relative;

  &.active {
    color: $text-primary;
  }

  // 中间健康管家特殊样式
  &.center-item {
      .center-icon-wrapper {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        background: transparent;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-top: -16px;
        border: 3px solid #fff;
        box-shadow: 0 4px 12px rgba(255, 209, 0, 0.3);
        overflow: hidden;

        .center-icon {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

    .center-text {
      font-size: 11px;
      color: $text-secondary;
      margin-top: 2px;
      transform: scale(0.9);
    }

    &.active {
      .center-icon-wrapper {
        background: linear-gradient(135deg, $primary-yellow 0%, #FFA500 100%);
      }
      .center-text {
        color: $text-primary;
        font-weight: 500;
      }
    }
  }
}

.tab-icon {
  position: relative;
  margin-bottom: 2px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;

  .tab-icon-img {
    width: 22px;
    height: 22px;
    object-fit: contain;
  }

  &.active {
    color: $primary-yellow;
  }
}

.tab-text {
  font-size: 11px;
  font-weight: 400;

  &.active {
    color: $primary-yellow;
    font-weight: 600;
  }
}

.safe-area-placeholder {
  height: env(safe-area-inset-bottom, 0);
}
</style>
