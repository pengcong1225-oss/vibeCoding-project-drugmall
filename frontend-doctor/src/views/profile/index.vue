<template>
  <div class="profile-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <span class="title">我的</span>
      <span class="setting-btn" @click="goToSettings">设置</span>
    </div>

    <!-- 医生信息卡片 -->
    <div class="doctor-card">
      <div class="doctor-avatar">
        <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=doctor" alt="头像" />
        <div class="verified-badge">✓</div>
      </div>
      <div class="doctor-info">
        <div class="doctor-name">
          张医生
          <span class="verified-tag">已认证</span>
        </div>
        <div class="doctor-title">副主任医师</div>
        <div class="doctor-hospital">北京协和医院 · 心内科</div>
      </div>
      <div class="arrow">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="9 18 15 12 9 6"/>
        </svg>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="stats-section">
      <div class="stat-item">
        <div class="stat-value">1,280</div>
        <div class="stat-label">接诊次数</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">4.9</div>
        <div class="stat-label">患者评分</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">98%</div>
        <div class="stat-label">好评率</div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-section">
      <div class="menu-group">
        <div class="menu-item" @click="goToIncome">
          <span class="menu-icon income">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 1v22M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
            </svg>
          </span>
          <span class="menu-name">我的收入</span>
          <span class="menu-extra">¥5,000</span>
          <span class="arrow">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </span>
        </div>
        <div class="menu-item" @click="goToSchedule">
          <span class="menu-icon primary">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
          </span>
          <span class="menu-name">我的排班</span>
          <span class="arrow">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </span>
        </div>
        <div class="menu-item" @click="goToLicense">
          <span class="menu-icon info">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/>
            </svg>
          </span>
          <span class="menu-name">执业信息</span>
          <span class="arrow">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </span>
        </div>
      </div>

      <div class="menu-group">
        <div class="menu-item" @click="goToEditProfile">
          <span class="menu-icon warning">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
          </span>
          <span class="menu-name">编辑资料</span>
          <span class="arrow">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </span>
        </div>
        <div class="menu-item">
          <span class="menu-icon danger">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/>
            </svg>
          </span>
          <span class="menu-name">消息通知</span>
          <span class="arrow">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </span>
        </div>
        <div class="menu-item">
          <span class="menu-icon success">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
          </span>
          <span class="menu-name">帮助中心</span>
          <span class="arrow">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </span>
        </div>
      </div>
    </div>

    <!-- Tabbar -->
    <Tabbar />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import Tabbar from '@/components/Tabbar/index.vue'

const router = useRouter()

const goToHome = () => router.push('/home')
const goToConsultation = () => router.push('/consultation')
const goToPatients = () => router.push('/patients')
const goToIncome = () => router.push('/income')
const goToSchedule = () => router.push('/profile/schedule')
const goToLicense = () => router.push('/profile/license')
const goToEditProfile = () => router.push('/profile/edit')
const goToSettings = () => router.push('/profile/settings')
</script>

<style scoped lang="scss">
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
}

.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: linear-gradient(135deg, #2E7D32 0%, #4CAF50 100%);
  color: #fff;

  .title {
    font-size: 18px;
    font-weight: 600;
    flex: 1;
    text-align: center;
  }

  .setting-btn {
    font-size: 14px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 4px;
    transition: background 0.2s;
    
    &:hover {
      background: rgba(255,255,255,0.1);
    }
  }
}

.doctor-card {
  background: linear-gradient(135deg, #2E7D32 0%, #4CAF50 100%);
  padding: 20px 16px;
  display: flex;
  align-items: center;
  color: #fff;
  cursor: pointer;
  transition: opacity 0.2s;
  
  &:active {
    opacity: 0.95;
  }

  .doctor-avatar {
    position: relative;
    margin-right: 16px;

    img {
      width: 64px;
      height: 64px;
      border-radius: 50%;
      border: 3px solid rgba(255,255,255,0.3);
    }

    .verified-badge {
      position: absolute;
      bottom: 0;
      right: 0;
      width: 20px;
      height: 20px;
      background: #fff;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      color: #2E7D32;
      font-weight: 600;
    }
  }

  .doctor-info {
    flex: 1;

    .doctor-name {
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 4px;
      display: flex;
      align-items: center;
      gap: 8px;

      .verified-tag {
        font-size: 11px;
        background: rgba(255,255,255,0.2);
        padding: 2px 8px;
        border-radius: 10px;
        font-weight: normal;
      }
    }

    .doctor-title {
      font-size: 14px;
      opacity: 0.9;
      margin-bottom: 2px;
    }

    .doctor-hospital {
      font-size: 13px;
      opacity: 0.8;
    }
  }

  .arrow {
    opacity: 0.7;
    display: flex;
    align-items: center;
  }
}

.stats-section {
  display: flex;
  background: #fff;
  margin: 12px;
  border-radius: 12px;
  padding: 20px 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);

  .stat-item {
    flex: 1;
    text-align: center;
    cursor: pointer;
    transition: opacity 0.2s;
    
    &:active {
      opacity: 0.7;
    }

    .stat-value {
      font-size: 20px;
      font-weight: 700;
      color: #2E7D32;
      margin-bottom: 4px;
    }

    .stat-label {
      font-size: 12px;
      color: #999;
    }
  }
}

.menu-section {
  padding: 0 12px;

  .menu-group {
    background: #fff;
    border-radius: 12px;
    margin-bottom: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0,0,0,0.04);

    .menu-item {
      display: flex;
      align-items: center;
      padding: 16px;
      border-bottom: 1px solid #f5f5f5;
      cursor: pointer;
      transition: background 0.2s;

      &:last-child {
        border-bottom: none;
      }

      &:active {
        background: #f9f9f9;
      }

      .menu-icon {
        width: 36px;
        height: 36px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        
        &.primary {
          background: #E8F5E9;
          color: #2E7D32;
        }
        
        &.income {
          background: #FFF3E0;
          color: #FF6B00;
        }
        
        &.info {
          background: #E3F2FD;
          color: #1976D2;
        }
        
        &.warning {
          background: #FFF8E1;
          color: #FF9800;
        }
        
        &.danger {
          background: #FFEBEE;
          color: #D32F2F;
        }
        
        &.success {
          background: #E8F5E9;
          color: #4CAF50;
        }
      }

      .menu-name {
        flex: 1;
        font-size: 15px;
        color: #333;
        font-weight: 500;
      }

      .menu-extra {
        font-size: 14px;
        color: #FF6B00;
        margin-right: 8px;
        font-weight: 600;
      }

      .arrow {
        display: flex;
        align-items: center;
        color: #ccc;
      }
    }
  }
}
</style>
