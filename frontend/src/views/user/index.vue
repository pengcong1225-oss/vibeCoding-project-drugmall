<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { ROUTES } from '@/constants/routes'

const router = useRouter()
const userStore = useUserStore()

// 是否已登录
const isLoggedIn = computed(() => userStore.isLoggedIn)

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 未读消息数
const unreadMessageCount = ref(5)

// 优惠券数量
const couponCount = ref(5)

// 会员卡数量
const memberCardCount = ref(0)

// 根据时间获取问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '上午好!'
  if (hour < 18) return '下午好!'
  return '晚上好!'
})

// 用户ID显示
const displayUserId = computed(() => {
  if (userInfo.value?.phone) {
    return userInfo.value.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  }
  return 'MBI808642777'
})

// 返回上一页
const goBack = () => {
  router.back()
}

const goToMessages = () => {
  router.push(ROUTES.MESSAGES)
}

const goToSettings = () => {
  router.push(ROUTES.SETTINGS)
}

const goToLogin = () => {
  router.push(ROUTES.LOGIN)
}

const openHealthCard = () => {
  ElMessage.info('健康卡功能开发中')
}

const clickActivityBanner = () => {
  ElMessage.info('春季过敏守护活动')
}

const clickRedPacket = (type: string) => {
  const messages: Record<string, string> = {
    'allergy': '过敏补贴红包',
    'help': '助力红包',
    'member': '美团会员'
  }
  ElMessage.info(`${messages[type]}功能开发中`)
}

const clickAssetStat = (type: string) => {
  if (type === 'coupon') {
    router.push(ROUTES.COUPONS)
  } else {
    ElMessage.info('会员卡包功能开发中')
  }
}

const clickQuickEntry = (type: string) => {
  const paths: Record<string, string> = {
    'cart': ROUTES.CART,
    'order': ROUTES.ORDER_LIST,
    'refund': '/order/refund'
  }
  if (type === 'refund') {
    ElMessage.info('退款/售后功能开发中')
    return
  }
  router.push(paths[type])
}

const clickHealthService = (label: string) => {
  const pathMap: Record<string, string> = {
    '家庭健康档案': ROUTES.PATIENT,
    '为TA买药': ROUTES.PATIENT,
    '我的咨询': ROUTES.INQUIRY_LIST,
    '处方病历': ROUTES.PRESCRIPTION,
    '我的检测': '/test',
    '意见/反馈': ROUTES.FEEDBACK
  }
  const path = pathMap[label]
  if (path) {
    router.push(path)
  }
}

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
    router.push(ROUTES.HOME)
  }).catch(() => {
    // 取消
  })
}
</script>

<template>
  <div class="user-center-page">
    <!-- 渐变区域（固定） -->
    <div class="gradient-section">
      <!-- 顶部导航栏 -->
      <div class="top-navbar">
        <div class="nav-left" @click="goBack">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none">
            <path d="M15 19L8 12L15 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="nav-right">
          <div class="nav-item message-icon" @click="goToMessages">
            <svg class="nav-icon" viewBox="0 0 24 24" fill="none">
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M22 6l-10 7L2 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span v-if="unreadMessageCount > 0" class="message-badge">{{ unreadMessageCount }}</span>
          </div>
          <div class="nav-item" @click="goToSettings">
            <svg class="nav-icon" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
              <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
      </div>

      <!-- 用户信息区 -->
      <div class="user-info-section">
        <div class="user-info-content">
          <div class="user-avatar-wrapper">
            <img
              v-if="isLoggedIn && userInfo?.avatar"
              :src="userInfo.avatar"
              class="user-avatar"
              alt="头像"
            />
            <div v-else class="user-avatar-placeholder">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
          <div class="user-text-info">
            <template v-if="isLoggedIn">
              <p class="greeting-text">{{ greeting }}</p>
              <p class="user-id">{{ displayUserId }}</p>
            </template>
            <template v-else>
              <p class="greeting-text" @click="goToLogin">点击登录</p>
              <p class="user-id">登录后享受更多服务</p>
            </template>
          </div>
        </div>
      </div>

      <!-- 健康卡推广区 -->
      <div class="health-card-promo">
        <div class="promo-header">
          <div class="promo-title">
            <span class="card-icon">🛍️</span>
            <span class="title-highlight">健康卡</span>
            <span class="title-desc">全年预计可省348元</span>
          </div>
          <button class="open-card-btn" @click="openHealthCard">去开通</button>
        </div>
        <div class="benefits-grid">
          <div class="benefit-item">
            <div class="benefit-icon-wrapper">
              <span class="benefit-icon">🎁</span>
            </div>
            <span class="benefit-text">开卡礼免费领<br/>价值10元3选1</span>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon-wrapper">
              <span class="benefit-icon">💰</span>
            </div>
            <span class="benefit-text">购药返现15%<br/>千款精选好药</span>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon-wrapper">
              <span class="benefit-icon">⚡</span>
            </div>
            <span class="benefit-text">买药1对1急送<br/>平均快20分钟</span>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon-wrapper">
              <span class="benefit-icon">🏷️</span>
            </div>
            <span class="benefit-text">健康专享价<br/>低至5折</span>
          </div>
        </div>
      </div>

      <!-- 活动横幅 -->
      <div class="activity-banner" @click="clickActivityBanner">
        <span class="topic-tag">春季过敏守护</span>
        <span class="activity-desc">领20元过敏补贴红包~</span>
        <svg class="arrow-icon" viewBox="0 0 24 24" fill="none">
          <path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>

      <!-- 红包/会员区 -->
      <div class="asset-cards-section">
        <div class="asset-cards-container">
          <div class="asset-card allergy" @click="clickRedPacket('allergy')">
            <div class="asset-icon-wrapper">
              <span class="asset-icon">🧧</span>
            </div>
            <div class="asset-info">
              <span class="asset-label">过敏补贴</span>
              <span class="asset-value">领20元红包</span>
            </div>
          </div>
          <div class="asset-card help" @click="clickRedPacket('help')">
            <div class="asset-icon-wrapper">
              <span class="asset-icon">🎁</span>
            </div>
            <div class="asset-info">
              <span class="asset-label">助力红包</span>
              <span class="asset-value">限时发放中</span>
            </div>
          </div>
          <div class="asset-card member" @click="clickRedPacket('member')">
            <div class="asset-icon-wrapper">
              <span class="asset-icon">💳</span>
            </div>
            <div class="asset-info">
              <span class="asset-label">美团会员</span>
              <span class="asset-value">免费问诊</span>
            </div>
          </div>
          <div class="asset-card extra" @click="clickRedPacket('extra')">
            <div class="asset-icon-wrapper">
              <span class="asset-icon">🎯</span>
            </div>
            <div class="asset-info">
              <span class="asset-label">额外福利</span>
              <span class="asset-value">限时领取</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 可滚动区域 -->
    <div class="scrollable-section">
      <!-- 资产统计 -->
      <div class="asset-stats-section">
        <div class="stat-item" @click="clickAssetStat('coupon')">
          <span class="stat-number red">{{ couponCount }}<span class="unit">个</span></span>
          <span class="stat-label">优惠券/红包</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item" @click="clickAssetStat('card')">
          <span class="stat-number orange">{{ memberCardCount }}<span class="unit">张</span></span>
          <span class="stat-label">会员卡包</span>
        </div>
      </div>

      <!-- 快捷入口 -->
      <div class="quick-entries-section">
        <div class="quick-entry-item" @click="clickQuickEntry('cart')">
          <div class="quick-entry-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <circle cx="9" cy="21" r="1" fill="currentColor"/>
              <circle cx="20" cy="21" r="1" fill="currentColor"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <span class="quick-entry-label">购物车</span>
        </div>
        <div class="quick-entry-item" @click="clickQuickEntry('order')">
          <div class="quick-entry-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="14 2 14 8 20 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <span class="quick-entry-label">全部订单</span>
        </div>
        <div class="quick-entry-item" @click="clickQuickEntry('refund')">
          <div class="quick-entry-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <span class="quick-entry-label">退款/售后</span>
        </div>
      </div>

      <!-- 健康服务区 -->
      <div class="health-services-section">
        <h3 class="section-title">健康服务</h3>
        <div class="services-grid">
          <div class="service-item" @click="clickHealthService('家庭健康档案')">
            <div class="service-icon-wrapper">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span class="service-label">家庭健康档案</span>
          </div>
          <div class="service-item" @click="clickHealthService('为TA买药')">
            <div class="service-icon-wrapper">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span class="service-label">为TA买药</span>
          </div>
          <div class="service-item" @click="clickHealthService('我的咨询')">
            <div class="service-icon-wrapper">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span class="service-label">我的咨询</span>
          </div>
          <div class="service-item" @click="clickHealthService('处方病历')">
            <div class="service-icon-wrapper">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="14 2 14 8 20 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </div>
            <span class="service-label">处方病历</span>
          </div>
          <div class="service-item" @click="clickHealthService('我的检测')">
            <div class="service-icon-wrapper">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M9 3v18M15 3v18M3 9h18M3 15h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </div>
            <span class="service-label">我的检测</span>
          </div>
          <div class="service-item" @click="clickHealthService('意见/反馈')">
            <div class="service-icon-wrapper">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span class="service-label">意见/反馈</span>
          </div>
        </div>
      </div>

      <!-- 退出登录 -->
      <div v-if="isLoggedIn" class="logout-section">
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <!-- 底部占位 -->
      <div class="bottom-placeholder"></div>
    </div>
  </div>
</template>

<style scoped lang="scss">
// 精确配色方案 - 完全复刻图片
$color-primary: #FFD54F;
$color-primary-light: #FFF3E0;
$color-primary-dark: #FFC107;
$color-cream: #FFF8E1;
$color-cream-dark: #FFE082;
$color-brown: #8D6E63;
$color-red: #FF4D4F;
$color-red-light: #FF8A80;
$color-orange: #FF7A45;
$color-green: #52C41A;
$color-blue: #1890FF;
$color-purple: #722ED1;
$color-pink: #EB2F96;

$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$text-white: #FFFFFF;

$bg-page: #FFFFFF;
$bg-card: #FFFFFF;
$bg-warm: #FFF8E1;

$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 12px;
$spacing-lg: 16px;
$spacing-xl: 20px;

$radius-sm: 6px;
$radius-md: 8px;
$radius-lg: 12px;
$radius-xl: 16px;
$radius-full: 50%;

.user-center-page {
  min-height: 100vh;
  background: $bg-page;
  padding-bottom: 80px;
}

// 渐变区域（固定）
.gradient-section {
  position: sticky;
  top: 0;
  z-index: 10;
  background: linear-gradient(180deg, #FFC107 0%, #FFE082 50%, #FFF8E1 100%);
  padding-bottom: $spacing-md;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

// 可滚动区域
.scrollable-section {
  margin-top: -$spacing-md;
  border-radius: $radius-lg $radius-lg 0 0;
  background: $bg-page;
  min-height: calc(100vh - 300px);
}

// 顶部导航栏
.top-navbar {
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $spacing-md;
  background: transparent;

  .nav-left {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: $radius-full;
    transition: background 0.2s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }

  .nav-right {
    display: flex;
    align-items: center;
    gap: $spacing-xs;

    .nav-item {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      border-radius: $radius-full;
      transition: background 0.2s ease;
      position: relative;

      &:hover {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    .message-icon {
      position: relative;
    }

    .message-badge {
      position: absolute;
      top: 2px;
      right: 2px;
      min-width: 16px;
      height: 16px;
      padding: 0 4px;
      background: $color-red;
      color: $text-white;
      font-size: 10px;
      font-weight: bold;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .nav-icon {
    width: 20px;
    height: 20px;
    color: $text-white;
  }
}

// 用户信息区
.user-info-section {
  position: relative;
  padding: $spacing-sm $spacing-md $spacing-lg;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .user-info-content {
    display: flex;
    align-items: center;
    gap: $spacing-md;
  }

  .user-avatar-wrapper {
    .user-avatar {
      width: 52px;
      height: 52px;
      border-radius: $radius-full;
      object-fit: cover;
      border: 2px solid rgba(255, 255, 255, 0.9);
    }

    .user-avatar-placeholder {
      width: 52px;
      height: 52px;
      border-radius: $radius-full;
      background: rgba(255, 255, 255, 0.3);
      border: 2px solid rgba(255, 255, 255, 0.9);
      display: flex;
      align-items: center;
      justify-content: center;

      svg {
        width: 26px;
        height: 26px;
        color: $text-white;
      }
    }
  }

  .user-text-info {
    .greeting-text {
      font-size: 18px;
      font-weight: 600;
      color: $text-white;
      margin-bottom: 2px;
      cursor: pointer;
    }

    .user-id {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.85);
    }
  }

}

// 健康卡推广区
.health-card-promo {
  margin: 0 $spacing-md;
  padding: $spacing-md;
  background: #FFF8E1;
  border-radius: $radius-lg;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);

  .promo-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-md;

    .promo-title {
      display: flex;
      align-items: center;
      gap: 4px;

      .card-icon {
        font-size: 16px;
      }

      .title-highlight {
        font-size: 15px;
        font-weight: 700;
        color: $color-brown;
      }

      .title-desc {
        font-size: 12px;
        color: $text-secondary;
      }
    }

    .open-card-btn {
      padding: 4px 12px;
      background: $color-brown;
      color: $text-white;
      font-size: 12px;
      font-weight: 500;
      border: none;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        opacity: 0.9;
        transform: translateY(-1px);
      }
    }
  }

  .benefits-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: $spacing-xs;

    .benefit-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      gap: 4px;
      background: #FFFFFF;
      border-radius: $radius-md;
      padding: $spacing-sm $spacing-xs;

      .benefit-icon-wrapper {
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;

        .benefit-icon {
          font-size: 20px;
          color: $text-primary;
        }
      }

      .benefit-text {
        font-size: 9px;
        color: $text-secondary;
        line-height: 1.3;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
    }
  }
}

// 活动横幅
.activity-banner {
  margin: $spacing-md;
  padding: $spacing-sm $spacing-md;
  background: linear-gradient(90deg, #FFF8E1 0%, #FFE082 100%);
  border: 1px solid #FFE082;
  border-radius: $radius-md;
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: linear-gradient(90deg, #FFE082 0%, #FFD54F 100%);
  }

  .topic-tag {
    font-size: 13px;
    color: #FF9800;
    font-weight: 500;

    &::before {
      content: '#';
    }
  }

  .activity-desc {
    font-size: 13px;
    color: $text-primary;
    flex: 1;
  }

  .arrow-icon {
    width: 14px;
    height: 14px;
    color: $text-tertiary;
  }
}

// 红包/会员区
.asset-cards-section {
  margin: 0 $spacing-md;
  padding-bottom: $spacing-sm;
  overflow-x: auto;
  white-space: nowrap;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  
  &::-webkit-scrollbar {
    display: none;
  }

  .asset-cards-container {
    display: inline-flex;
    gap: $spacing-sm;
  }

  .asset-card {
    min-width: 140px;
    background: $bg-card;
    border-radius: $radius-md;
    padding: $spacing-sm;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .asset-icon-wrapper {
      width: 36px;
      height: 36px;
      border-radius: $radius-full;
      background: #F5F5F5;
      display: flex;
      align-items: center;
      justify-content: center;

      .asset-icon {
        font-size: 18px;
        color: $text-primary;
      }
    }

    .asset-info {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .asset-label {
      font-size: 12px;
      color: $text-secondary;
    }

    .asset-value {
      font-size: 12px;
      font-weight: 600;
    }

    &.allergy {
      .asset-icon-wrapper {
        background: #F5F5F5;
      }
      .asset-value {
        color: $color-red;
      }
    }

    &.help {
      .asset-icon-wrapper {
        background: #F5F5F5;
      }
      .asset-value {
        color: $color-orange;
      }
    }

    &.member {
      .asset-icon-wrapper {
        background: #F5F5F5;
      }
      .asset-value {
        color: $color-orange;
      }
    }

    &.extra {
      .asset-icon-wrapper {
        background: #F5F5F5;
      }
      .asset-value {
        color: $color-blue;
      }
    }
  }
}

// 资产统计
.asset-stats-section {
  margin: $spacing-md;
  padding: $spacing-md;
  background: $bg-card;
  border-radius: $radius-md;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-around;

  .stat-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    cursor: pointer;
    transition: opacity 0.2s ease;

    &:hover {
      opacity: 0.8;
    }

    .stat-number {
      font-size: 22px;
      font-weight: 700;
      display: flex;
      align-items: baseline;
      gap: 2px;

      .unit {
        font-size: 12px;
        font-weight: 400;
      }

      &.red {
        color: $color-red;
      }

      &.orange {
        color: $color-orange;
      }
    }

    .stat-label {
      font-size: 11px;
      color: $text-secondary;
    }
  }

  .stat-divider {
    width: 1px;
    height: 32px;
    background: #EEEEEE;
  }
}

// 快捷入口
.quick-entries-section {
  margin: 0 $spacing-md;
  padding: $spacing-md;
  background: $bg-card;
  border-radius: $radius-md;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-md;

  .quick-entry-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-xs;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      .quick-entry-icon {
        transform: scale(1.05);
      }
    }

    .quick-entry-icon {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s ease;

      svg {
        width: 24px;
        height: 24px;
        color: $text-primary;
      }
    }

    .quick-entry-label {
      font-size: 12px;
      color: $text-primary;
    }
  }
}

// 健康服务区
.health-services-section {
  margin: $spacing-md;
  padding: $spacing-md;
  background: $bg-card;
  border-radius: $radius-lg;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

  .section-title {
    font-size: 14px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }

  .services-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: $spacing-md $spacing-sm;

    .service-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: $spacing-xs;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        .service-icon-wrapper {
          transform: scale(1.05);
        }
      }

      .service-icon-wrapper {
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s ease;

        svg {
          width: 28px;
          height: 28px;
          color: $text-primary;
        }
      }

      .service-label {
        font-size: 11px;
        color: $text-secondary;
        text-align: center;
      }
    }
  }
}

// 退出登录
.logout-section {
  margin: $spacing-md;

  .logout-btn {
    width: 100%;
    padding: $spacing-sm $spacing-md;
    background: $bg-card;
    border: 1px solid #E0E0E0;
    border-radius: $radius-md;
    color: $color-red;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: rgba($color-red, 0.05);
      border-color: $color-red;
    }
  }
}

// 底部占位
.bottom-placeholder {
  height: 20px;
}

// 响应式适配
@media (max-width: 375px) {
  .health-card-promo {
    .benefits-grid {
      .benefit-item {
        .benefit-text {
          font-size: 8px;
        }
      }
    }
  }

  .asset-cards-section {
    .asset-card {
      padding: $spacing-xs;
    }
  }
}
</style>
