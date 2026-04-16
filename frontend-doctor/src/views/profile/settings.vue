<template>
  <div class="settings-page">
    <!-- 顶部导航 -->
    <header class="page-header">
      <button class="back-btn" @click="goBack">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M15 18l-6-6 6-6" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <h1 class="page-title">设置</h1>
      <div class="header-right"></div>
    </header>

    <!-- 设置菜单 -->
    <div class="settings-section">
      <div class="settings-group">
        <div class="settings-item">
          <span class="item-name">账号安全</span>
          <span class="arrow">></span>
        </div>
        <div class="settings-item">
          <span class="item-name">消息通知</span>
          <span class="item-value">已开启</span>
          <span class="arrow">></span>
        </div>
        <div class="settings-item">
          <span class="item-name">隐私设置</span>
          <span class="arrow">></span>
        </div>
      </div>

      <div class="settings-group">
        <div class="settings-item">
          <span class="item-name">清除缓存</span>
          <span class="item-value">12.5MB</span>
          <span class="arrow">></span>
        </div>
        <div class="settings-item">
          <span class="item-name">关于我们</span>
          <span class="arrow">></span>
        </div>
        <div class="settings-item">
          <span class="item-name">用户协议</span>
          <span class="arrow">></span>
        </div>
        <div class="settings-item">
          <span class="item-name">隐私政策</span>
          <span class="arrow">></span>
        </div>
      </div>

      <div class="settings-group">
        <div class="settings-item">
          <span class="item-name">当前版本</span>
          <span class="item-value">v1.0.0</span>
        </div>
      </div>
    </div>

    <!-- 退出登录 -->
    <div class="logout-section">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.back()
}

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token')
    localStorage.removeItem('doctorInfo')
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.settings-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  margin-bottom: 12px;

  .back-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: none;
    border: none;
    color: #333;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s;

    &:active {
      background: #f5f5f5;
    }

    svg {
      width: 24px;
      height: 24px;
    }
  }

  .page-title {
    flex: 1;
    text-align: center;
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin: 0;
  }

  .header-right {
    width: 40px;
  }
}

.settings-section {
  padding: 0 12px;

  .settings-group {
    background: #fff;
    border-radius: 12px;
    margin-bottom: 12px;
    overflow: hidden;

    .settings-item {
      display: flex;
      align-items: center;
      padding: 16px;
      border-bottom: 1px solid #f5f5f5;
      cursor: pointer;

      &:last-child {
        border-bottom: none;
      }

      &:active {
        background: #f9f9f9;
      }

      .item-name {
        flex: 1;
        font-size: 15px;
        color: #333;
      }

      .item-value {
        font-size: 14px;
        color: #999;
        margin-right: 8px;
      }

      .arrow {
        font-size: 14px;
        color: #ccc;
      }
    }
  }
}

.logout-section {
  padding: 24px 12px;

  .logout-btn {
    width: 100%;
    height: 48px;
    background: #fff;
    border: none;
    border-radius: 8px;
    color: #FF4D4F;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;

    &:active {
      background: #f9f9f9;
    }
  }
}
</style>
