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
$primary-cyan: #0891B2;
$primary-light: #22D3EE;
$text-primary: #1A1A1A;
$text-secondary: #666666;
$text-tertiary: #999999;

.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  background: linear-gradient(180deg, #FFFFFF 0%, #FAFAFA 100%);
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.08);
  z-index: 9999;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.tabbar-inner {
  max-width: 430px;
  margin: 0 auto;
  padding: 0 env(safe-area-inset-left, 0) 0 env(safe-area-inset-right, 0);
}

.tabbar-content {
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 56px;
  padding: 0 32px;
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
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;

  &:active {
    transform: scale(0.92);
  }

  &.active {
    color: $primary-cyan;
  }

  // 中间健康管家特殊样式
  &.center-item {
      .center-icon-wrapper {
        width: 52px;
        height: 52px;
        border-radius: 50%;
        background: linear-gradient(135deg, #0E7490 0%, #0891B2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-top: -20px;
        border: 4px solid #fff;
        box-shadow: 0 6px 16px rgba(8, 145, 178, 0.4);
        overflow: hidden;
        transition: all 0.3s ease;

        .center-icon {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

    .center-text {
      font-size: 10px;
      color: $text-secondary;
      margin-top: 4px;
      font-weight: 500;
    }

    &:active {
      .center-icon-wrapper {
        transform: scale(0.95);
      }
    }

    &.active {
      .center-icon-wrapper {
        background: linear-gradient(135deg, #0891B2 0%, #22D3EE 100%);
        box-shadow: 0 6px 16px rgba(8, 145, 178, 0.4);
      }
      .center-text {
        color: $primary-cyan;
        font-weight: 600;
      }
    }
  }
}

.tab-icon {
  position: relative;
  margin-bottom: 3px;
  width: 26px;
  height: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: all 0.25s ease;

  .tab-icon-img {
    width: 24px;
    height: 24px;
    object-fit: contain;
  }

  &.active {
    color: $primary-cyan;
    background: rgba(8, 145, 178, 0.1);
  }
}

.tab-text {
  font-size: 11px;
  font-weight: 400;
  transition: all 0.25s ease;

  &.active {
    color: $primary-cyan;
    font-weight: 600;
    transform: scale(1.05);
  }
}

.safe-area-placeholder {
  height: env(safe-area-inset-bottom, 0);
}
</style>
