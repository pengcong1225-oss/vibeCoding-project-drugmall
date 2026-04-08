<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 是否已登录
const isLoggedIn = computed(() => userStore.isLoggedIn)

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 默认就诊人
const defaultPatient = computed(() => userStore.defaultPatient)

// 订单状态统计（模拟）
const orderStats = [
  { label: '待付款', value: 2, icon: 'Wallet', path: '/order/list?status=pending' },
  { label: '待发货', value: 0, icon: 'Box', path: '/order/list?status=paid' },
  { label: '待收货', value: 1, icon: 'Van', path: '/order/list?status=shipped' },
  { label: '待评价', value: 3, icon: 'ChatDotRound', path: '/order/list?status=completed' }
]

// 功能菜单
const menuGroups = [
  {
    title: '我的服务',
    items: [
      { label: '我的处方', icon: 'Document', path: '/prescription', badge: 0 },
      { label: '我的问诊', icon: 'FirstAidKit', path: '/inquiry', badge: 0 },
      { label: '就诊人管理', icon: 'User', path: '/patient', badge: 0 },
      { label: '收货地址', icon: 'Location', path: '/address', badge: 0 }
    ]
  },
  {
    title: '更多服务',
    items: [
      { label: '帮助中心', icon: 'QuestionFilled', path: '/help', badge: 0 },
      { label: '意见反馈', icon: 'EditPen', path: '/feedback', badge: 0 },
      { label: '设置', icon: 'Setting', path: '/settings', badge: 0 }
    ]
  }
]

// 去登录
const goToLogin = () => {
  router.push('/login')
}

// 去设置
const goToSettings = () => {
  ElMessage.info('设置功能开发中')
}

// 点击订单状态
const handleOrderStatClick = (stat: typeof orderStats[0]) => {
  router.push(stat.path)
}

// 点击菜单项
const handleMenuClick = (item: typeof menuGroups[0]['items'][0]) => {
  if (item.path) {
    router.push(item.path)
  } else {
    ElMessage.info(`${item.label}功能开发中`)
  }
}

// 退出登录
const logout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '确认退出',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/home')
  }).catch(() => {
    // 取消
  })
}
</script>

<template>
  <div class="user-page">
    <!-- 用户信息卡片 -->
    <div class="user-header">
      <div class="user-info">
        <div class="avatar-wrapper">
          <img
            v-if="isLoggedIn && userInfo?.avatar"
            :src="userInfo.avatar"
            class="avatar"
            alt="头像"
          />
          <div v-else class="avatar-placeholder">
            <el-icon><UserFilled /></el-icon>
          </div>
        </div>
        <div class="info-content">
          <template v-if="isLoggedIn">
            <h2 class="nickname">{{ userInfo?.nickname || userInfo?.phone }}</h2>
            <p class="phone">{{ userInfo?.phone }}</p>
            <div v-if="defaultPatient" class="patient-tag">
              <el-icon><FirstAidKit /></el-icon>
              <span>{{ defaultPatient.name }} {{ defaultPatient.gender === 'male' ? '男' : '女' }} {{ defaultPatient.age }}岁</span>
            </div>
          </template>
          <template v-else>
            <h2 class="nickname" @click="goToLogin">点击登录 / 注册</h2>
            <p class="hint">登录后享受更多服务</p>
          </template>
        </div>
        <div v-if="isLoggedIn" class="settings-btn" @click="goToSettings">
          <el-icon><Setting /></el-icon>
        </div>
      </div>
    </div>

    <!-- 订单状态 -->
    <div class="order-stats-section">
      <div class="section-header">
        <h3>我的订单</h3>
        <span class="view-all" @click="$router.push('/order/list')">
          查看全部 <el-icon><ArrowRight /></el-icon>
        </span>
      </div>
      <div class="stats-grid">
        <div
          v-for="stat in orderStats"
          :key="stat.label"
          class="stat-item"
          @click="handleOrderStatClick(stat)"
        >
          <div class="stat-icon">
            <el-icon :size="24">
              <component :is="stat.icon" />
            </el-icon>
          </div>
          <span class="stat-label">{{ stat.label }}</span>
          <span v-if="stat.value > 0" class="stat-badge">{{ stat.value }}</span>
        </div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div v-for="(group, index) in menuGroups" :key="index" class="menu-section">
      <h3 class="menu-title">{{ group.title }}</h3>
      <div class="menu-grid">
        <div
          v-for="item in group.items"
          :key="item.label"
          class="menu-item"
          @click="handleMenuClick(item)"
        >
          <div class="menu-icon">
            <el-icon :size="24">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <span class="menu-label">{{ item.label }}</span>
          <span v-if="item.badge > 0" class="menu-badge">{{ item.badge }}</span>
        </div>
      </div>
    </div>

    <!-- 退出登录 -->
    <div v-if="isLoggedIn" class="logout-section">
      <button class="logout-btn" @click="logout">退出登录</button>
    </div>

    <!-- 底部占位 -->
    <div class="bottom-placeholder" />
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.user-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: calc($tabbar-height + $safe-area-bottom);
}

// 用户信息头部
.user-header {
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  padding: $spacing-lg;
  padding-top: calc($safe-area-top + $spacing-lg);

  .user-info {
    display: flex;
    align-items: center;
    gap: $spacing-md;

    .avatar-wrapper {
      position: relative;

      .avatar {
        width: 72px;
        height: 72px;
        border-radius: 50%;
        object-fit: cover;
        border: 3px solid rgba(255, 255, 255, 0.3);
      }

      .avatar-placeholder {
        width: 72px;
        height: 72px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.2);
        display: flex;
        align-items: center;
        justify-content: center;
        color: $text-white;
        font-size: 32px;
        border: 3px solid rgba(255, 255, 255, 0.3);
      }
    }

    .info-content {
      flex: 1;
      min-width: 0;

      .nickname {
        font-size: $font-xl;
        font-weight: 600;
        color: $text-white;
        margin-bottom: $spacing-xs;
        cursor: pointer;
      }

      .phone {
        font-size: $font-sm;
        color: rgba(255, 255, 255, 0.8);
        margin-bottom: $spacing-xs;
      }

      .hint {
        font-size: $font-sm;
        color: rgba(255, 255, 255, 0.6);
      }

      .patient-tag {
        display: inline-flex;
        align-items: center;
        gap: $spacing-xs;
        padding: 4px 10px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: $radius-md;
        font-size: $font-xs;
        color: $text-white;
        margin-top: $spacing-xs;
      }
    }

    .settings-btn {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $text-white;
      font-size: 20px;
      cursor: pointer;
      border-radius: 50%;
      transition: background 0.2s ease;

      &:hover {
        background: rgba(255, 255, 255, 0.2);
      }
    }
  }
}

// 订单统计
.order-stats-section {
  margin: $spacing-md;
  padding: $spacing-lg;
  background: $bg-white;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-md;

    h3 {
      font-size: $font-md;
      font-weight: 600;
      color: $text-primary;
    }

    .view-all {
      display: flex;
      align-items: center;
      gap: $spacing-xs;
      font-size: $font-sm;
      color: $text-tertiary;
      cursor: pointer;
      transition: color 0.2s ease;

      &:hover {
        color: $primary;
      }
    }
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: $spacing-md;

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: $spacing-xs;
      padding: $spacing-sm;
      cursor: pointer;
      border-radius: $radius-md;
      transition: background 0.2s ease;
      position: relative;

      &:hover {
        background: $bg-gray;
      }

      .stat-icon {
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: rgba($primary, 0.1);
        border-radius: 50%;
        color: $primary;
      }

      .stat-label {
        font-size: $font-xs;
        color: $text-secondary;
      }

      .stat-badge {
        position: absolute;
        top: 4px;
        right: 4px;
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
    }
  }
}

// 菜单区域
.menu-section {
  margin: $spacing-md;

  .menu-title {
    font-size: $font-md;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-md;
    padding-left: $spacing-xs;
  }

  .menu-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: $spacing-md;
    padding: $spacing-lg;
    background: $bg-white;
    border-radius: $radius-lg;
    box-shadow: $shadow-sm;

    .menu-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: $spacing-xs;
      padding: $spacing-sm;
      cursor: pointer;
      border-radius: $radius-md;
      transition: background 0.2s ease;
      position: relative;

      &:hover {
        background: $bg-gray;

        .menu-icon {
          transform: scale(1.1);
        }
      }

      .menu-icon {
        width: 44px;
        height: 44px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, rgba($primary, 0.1) 0%, rgba($primary, 0.05) 100%);
        border-radius: 50%;
        color: $primary;
        font-size: 22px;
        transition: transform 0.2s ease;
      }

      .menu-label {
        font-size: $font-xs;
        color: $text-secondary;
        text-align: center;
      }

      .menu-badge {
        position: absolute;
        top: 4px;
        right: 4px;
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
    }
  }
}

// 退出登录
.logout-section {
  padding: $spacing-md;
  margin-top: $spacing-md;

  .logout-btn {
    width: 100%;
    padding: $spacing-md;
    background: $bg-white;
    border: 1px solid $border-light;
    border-radius: $radius-lg;
    color: $error;
    font-size: $font-md;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: rgba($error, 0.05);
      border-color: $error;
    }
  }
}

// 底部占位
.bottom-placeholder {
  height: 20px;
}
</style>
