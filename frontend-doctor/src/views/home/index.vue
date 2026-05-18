<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDoctorStore } from '@/stores/doctor'
import Tabbar from '@/components/Tabbar/index.vue'

const router = useRouter()
const doctorStore = useDoctorStore()

const quickActions = [
  { icon: 'stethoscope', name: '开始接诊', path: '/consultation' },
  { icon: 'prescription', name: '处方管理', path: '/prescription' },
  { icon: 'users', name: '患者管理', path: '/patients' },
  { icon: 'wallet', name: '收入明细', path: '/income' },
  { icon: 'calendar', name: '我的排班', path: '/profile/schedule' },
  { icon: 'book', name: '学术资料', path: '#' },
  { icon: 'chart', name: '数据统计', path: '#' },
  { icon: 'more', name: '更多功能', path: '#' }
]

// 使用真实数据替换硬编码
const todoItems = computed(() => [
  { 
    icon: 'warning', 
    color: 'orange', 
    title: '待回复患者咨询', 
    count: `${doctorStore.todayStats.pending}条待回复`, 
    action: '去回复',
    path: '/consultation?tab=pending'
  },
  { 
    icon: 'prescription', 
    color: 'blue', 
    title: '待审核处方', 
    count: '待开发', 
    action: '去审核',
    path: '#'
  },
  { 
    icon: 'file', 
    color: 'gray', 
    title: '待完善病历', 
    count: '待开发', 
    action: '去完善',
    path: '#'
  },
  { 
    icon: 'clock', 
    color: 'red', 
    title: '即将过期随访', 
    count: '待开发', 
    action: '去随访',
    path: '#'
  }
])

const patientMessages = [
  { name: '李女士', time: '10:23', count: 1, message: '医生，我最近头疼得厉害，睡眠也不好...' },
  { name: '王先生', time: '09:45', count: 2, message: '上次开的药吃完了，需要再开一个疗程...' },
  { name: '张女士', time: '昨天', count: 0, message: '谢谢医生指导，已经好转多了' }
]

const notices = [
  { tag: '置顶', tagType: 'primary', title: '关于春节期间问诊服务调整的通知', date: '2024-12-06' },
  { tag: '新', tagType: 'error', title: '新增慢病管理功能上线公告', date: '2024-12-05' },
  { tag: '', tagType: '', title: '2024年度医生执业培训计划发布', date: '2024-12-01' }
]

const goToPage = (path: string) => {
  if (path !== '#') {
    router.push(path)
  }
}

onMounted(() => {
  doctorStore.initDoctorInfo()
})
</script>

<template>
  <div class="home-page">
    <!-- 头部区域 -->
    <div class="header">
      <div class="header-bg"></div>
      <div class="header-content">
        <div class="doctor-info">
          <div class="doctor-avatar">
            <img v-if="doctorStore.doctorInfo?.avatar" :src="doctorStore.doctorInfo.avatar" alt="头像">
            <span v-else class="avatar-text">{{ doctorStore.doctorInfo?.name?.[0] || '医' }}</span>
            <span v-if="doctorStore.doctorInfo?.isCertified" class="certified-badge">✓</span>
          </div>
          <div class="doctor-meta">
            <div class="doctor-name">
              {{ doctorStore.doctorInfo?.name || '张医生' }}
              <span v-if="doctorStore.doctorInfo?.isCertified" class="certified-icon">✓</span>
            </div>
            <div class="doctor-hospital">
              {{ doctorStore.doctorInfo?.hospital || '北京协和医院' }} {{ doctorStore.doctorInfo?.department || '心内科' }}
            </div>
          </div>
        </div>
        <div class="header-actions">
          <div class="action-btn" @click="goToPage('/profile/settings')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="3"></circle>
              <path d="M12 1v6m0 6v6m4.22-10.22l4.24-4.24M6.34 17.66l-4.24 4.24M23 12h-6m-6 0H1m20.24 4.24l-4.24-4.24M6.34 6.34L2.1 2.1"></path>
            </svg>
          </div>
          <div class="action-btn" @click="goToPage('/profile/messages')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path>
              <polyline points="22,6 12,13 2,6"></polyline>
            </svg>
            <span v-if="doctorStore.unreadCount > 0" class="message-badge">{{ doctorStore.unreadCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据概览 -->
    <div class="stats-card">
      <div class="stats-grid">
        <div class="stat-item" @click="goToPage('/consultation')">
          <div class="stat-value">{{ doctorStore.todayStats.pending }}</div>
          <div class="stat-label">待接诊</div>
        </div>
        <div class="stat-item" @click="goToPage('/consultation')">
          <div class="stat-value">{{ doctorStore.todayStats.processing }}</div>
          <div class="stat-label">接诊中</div>
        </div>
        <div class="stat-item" @click="goToPage('/consultation')">
          <div class="stat-value">{{ doctorStore.todayStats.completed }}</div>
          <div class="stat-label">已完成</div>
        </div>
        <div class="stat-item" @click="goToPage('/income')">
          <div class="stat-value income">{{ doctorStore.todayStats.income }}</div>
          <div class="stat-label">今日收入</div>
        </div>
      </div>
    </div>

    <div class="page-content">
      <!-- 快捷功能入口 -->
      <div class="quick-entry">
        <div class="entry-item" @click="goToPage('/consultation')">
          <div class="entry-icon primary">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4.8 2.3A.3.3 0 1 0 5 2H4a2 2 0 0 0-2 2v5a6 6 0 0 0 6 6v0a6 6 0 0 0 6-6V4a2 2 0 0 0-2-2h-1a.2.2 0 1 0 .3.3"/><path d="M8 15v1a6 6 0 0 0 6 6v0a6 6 0 0 0 6-6v-4"/><circle cx="20" cy="10" r="2"/>
            </svg>
          </div>
          <span class="entry-name">开始接诊</span>
        </div>
        <div class="entry-item" @click="goToPage('/prescription')">
          <div class="entry-icon success">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/>
            </svg>
          </div>
          <span class="entry-name">处方管理</span>
        </div>
        <div class="entry-item" @click="goToPage('/patients')">
          <div class="entry-icon warning">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
            </svg>
          </div>
          <span class="entry-name">患者管理</span>
        </div>
        <div class="entry-item" @click="goToPage('/income')">
          <div class="entry-icon danger">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 12V8H6a2 2 0 0 1-2-2c0-1.1.9-2 2-2h12v4"/><path d="M4 6v12a2 2 0 0 0 2 2h14v-4"/><path d="M18 12a2 2 0 0 0-2 2c0 1.1.9 2 2 2h4v-4h-4z"/>
            </svg>
          </div>
          <span class="entry-name">收入明细</span>
        </div>
      </div>

      <!-- 待办事项 -->
      <div class="section">
        <div class="section-header">
          <h3 class="section-title">今日待办</h3>
          <span class="section-more" @click="goToPage('/consultation')">
            查看全部 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
          </span>
        </div>
        <div class="todo-list">
          <div 
            v-for="(item, index) in todoItems" 
            :key="index" 
            class="todo-item"
            @click="item.path !== '#' && goToPage(item.path)"
            :style="{ cursor: item.path !== '#' ? 'pointer' : 'default' }"
          >
            <div class="todo-icon" :class="item.color">
              <svg v-if="item.icon === 'warning'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
              </svg>
              <svg v-else-if="item.icon === 'prescription'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/>
              </svg>
              <svg v-else-if="item.icon === 'file'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
              </svg>
            </div>
            <div class="todo-content">
              <div class="todo-title">{{ item.title }}</div>
              <div class="todo-count">{{ item.count }}</div>
            </div>
            <div class="todo-action">{{ item.action }}</div>
          </div>
        </div>
      </div>

      <!-- 患者消息 -->
      <div class="section">
        <div class="section-header">
          <h3 class="section-title">患者消息</h3>
          <span class="section-more" @click="goToPage('/consultation')">
            进入会话 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
          </span>
        </div>
        <div class="message-list">
          <div v-for="(msg, index) in patientMessages" :key="index" class="message-item" @click="goToPage('/consultation')">
            <div class="message-avatar">
              {{ msg.name[0] }}
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="message-name">{{ msg.name }}</span>
                <span class="message-time">{{ msg.time }}</span>
                <span v-if="msg.count > 0" class="message-badge">{{ msg.count }}</span>
                <span v-else class="message-read">·</span>
              </div>
              <div class="message-preview">{{ msg.message }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 收入概览 -->
      <div class="section">
        <div class="section-header">
          <h3 class="section-title">收入概览</h3>
          <div class="section-actions">
            <span class="action-btn" @click="goToPage('/income/withdraw')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
              提现
            </span>
            <span class="action-btn" @click="goToPage('/income/detail')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
              明细
            </span>
          </div>
        </div>
        <div class="income-card" @click="goToPage('/income')">
          <div class="income-chart">
            <!-- 简化的趋势图 -->
            <div class="chart-placeholder">
              <svg viewBox="0 0 200 60" preserveAspectRatio="none">
                <path d="M0,50 Q50,30 100,35 T200,20" fill="none" stroke="#00B578" stroke-width="2"/>
                <path d="M0,50 Q50,30 100,35 T200,20 V60 H0 Z" fill="url(#gradient)" opacity="0.3"/>
                <defs>
                  <linearGradient id="gradient" x1="0%" y1="0%" x2="0%" y2="100%">
                    <stop offset="0%" style="stop-color:#00B578"/>
                    <stop offset="100%" style="stop-color:#00B578;stop-opacity:0"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
          </div>
          <div class="income-summary">
            <div class="summary-item">
              <span class="label">累计总收入</span>
              <span class="value">¥12,580</span>
            </div>
            <div class="summary-item highlight">
              <span class="label">可提现金额</span>
              <span class="value">¥8,500</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 系统公告 -->
      <div class="section">
        <div class="section-header">
          <h3 class="section-title">系统公告</h3>
        </div>
        <div class="notice-list">
          <div v-for="(notice, index) in notices" :key="index" class="notice-item" @click="goToPage('#')">
            <span v-if="notice.tag" class="notice-tag" :class="notice.tagType">{{ notice.tag }}</span>
            <span class="notice-title">{{ notice.title }}</span>
            <span class="notice-date">{{ notice.date }}</span>
          </div>
        </div>
      </div>

      <!-- 底部占位 -->
      <div class="bottom-placeholder"></div>
    </div>

    <!-- Tabbar -->
    <Tabbar />
  </div>
</template>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: calc(56px + env(safe-area-inset-bottom));
}

// 头部区域
.header {
  position: relative;
  padding: 12px 16px;
  
  .header-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 120px;
    background: linear-gradient(135deg, #2E7D32 0%, #4CAF50 100%);
    border-radius: 0 0 24px 24px;
  }
  
  .header-content {
    position: relative;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .doctor-info {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .doctor-avatar {
    position: relative;
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    font-weight: 600;
    color: #2E7D32;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    
    img {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      object-fit: cover;
    }
    
    .avatar-text {
      font-size: 24px;
    }
    
    .certified-badge {
      position: absolute;
      bottom: 0;
      right: 0;
      width: 18px;
      height: 18px;
      background: #4CAF50;
      color: #fff;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 10px;
      border: 2px solid #fff;
    }
  }
  
  .doctor-meta {
    color: #fff;
  }
  
  .doctor-name {
    font-size: 18px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 6px;
    
    .certified-icon {
      width: 16px;
      height: 16px;
      background: #4CAF50;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 10px;
    }
  }
  
  .doctor-hospital {
    font-size: 13px;
    opacity: 0.9;
    margin-top: 4px;
  }
  
  .header-actions {
    display: flex;
    gap: 8px;
  }
  
  .action-btn {
    position: relative;
    width: 36px;
    height: 36px;
    background: rgba(255,255,255,0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover {
      background: rgba(255,255,255,0.3);
    }
    
    svg {
      width: 18px;
      height: 18px;
    }
    
    .message-badge {
      position: absolute;
      top: -2px;
      right: -2px;
      min-width: 16px;
      height: 16px;
      padding: 0 4px;
      background: #D32F2F;
      color: #fff;
      border-radius: 8px;
      font-size: 10px;
      font-weight: 600;
      line-height: 16px;
      text-align: center;
    }
  }
}

// 数据概览卡片
.stats-card {
  margin: 12px 16px 16px;
  background: linear-gradient(135deg, #2E7D32 0%, #4CAF50 100%);
  border-radius: 16px;
  padding: 20px 16px;
  box-shadow: 0 4px 16px rgba(46, 125, 50, 0.3);
  position: relative;
  z-index: 1;
  
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 8px;
  }
  
  .stat-item {
    text-align: center;
    color: #fff;
    cursor: pointer;
    transition: transform 0.2s;
    
    &:active {
      transform: scale(0.95);
    }
    
    .stat-value {
      font-size: 28px;
      font-weight: 700;
      line-height: 1.2;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
      
      &.income {
        font-size: 28px;
      }
    }
    
    .stat-label {
      font-size: 13px;
      font-weight: 500;
      opacity: 0.95;
      margin-top: 6px;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
    }
  }
}

// 页面内容区
.page-content {
  padding: 0 16px;
}

// 通用区块样式
.section {
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.section-more {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #2E7D32;
  cursor: pointer;
  
  svg {
    width: 14px;
    height: 14px;
  }
}

.section-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #FF6B00;
  cursor: pointer;
  
  svg {
    width: 14px;
    height: 14px;
  }
}

// 快捷入口
.quick-entry {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px 4px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  
  &:active {
    background: #f5f5f5;
    transform: scale(0.98);
  }
  
  .entry-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    svg {
      width: 24px;
      height: 24px;
    }
    
    &.primary {
      background: #E8F5E9;
      color: #2E7D32;
    }
    
    &.success {
      background: #E3F2FD;
      color: #1976D2;
    }
    
    &.warning {
      background: #FFF3E0;
      color: #FF9800;
    }
    
    &.danger {
      background: #FFEBEE;
      color: #D32F2F;
    }
  }
  
  .entry-name {
    font-size: 12px;
    color: #666;
    text-align: center;
    font-weight: 500;
  }
}

// 快捷功能（旧版兼容）
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  padding: 16px;
}

.quick-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 8px 4px;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;
  
  &:active {
    background: #f5f5f5;
  }
  
  .action-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #E8F5E9;
    color: #2E7D32;
    
    svg {
      width: 24px;
      height: 24px;
    }
  }
  
  .action-name {
    font-size: 12px;
    color: #666;
    text-align: center;
  }
}

// 待办事项
.todo-list {
  padding: 0 16px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid $border-light;
  cursor: pointer;
  
  &:last-child {
    border-bottom: none;
  }
  
  &:active {
    opacity: 0.7;
  }
}

.todo-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 18px;
    height: 18px;
  }
  
  &.orange {
    background: #FFF7E6;
    color: #FA8C16;
  }
  
  &.blue {
    background: #E6F7FF;
    color: #1890FF;
  }
  
  &.gray {
    background: #F5F5F5;
    color: #999;
  }
  
  &.red {
    background: #FFF1F0;
    color: #FF4D4F;
  }
}

.todo-content {
  flex: 1;
  min-width: 0;
}

.todo-title {
  font-size: 14px;
  color: $text-primary;
  margin-bottom: 2px;
}

.todo-count {
  font-size: 12px;
  color: $text-tertiary;
}

.todo-action {
  font-size: 12px;
  color: $primary;
  padding: 4px 8px;
  border: 1px solid $primary;
  border-radius: 4px;
  white-space: nowrap;
}

// 患者消息
.message-list {
  padding: 0 16px;
}

.message-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid $border-light;
  cursor: pointer;
  
  &:last-child {
    border-bottom: none;
  }
  
  &:active {
    background: $gray-50;
  }
}

.message-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: $primary-50;
  color: $primary;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.message-name {
  font-size: 14px;
  font-weight: 600;
  color: $text-primary;
}

.message-time {
  font-size: 12px;
  color: $text-tertiary;
}

.message-badge {
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: $error;
  color: #fff;
  border-radius: 9px;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.message-read {
  font-size: 12px;
  color: $text-tertiary;
}

.message-preview {
  font-size: 13px;
  color: $text-secondary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

// 收入卡片
.income-card {
  padding: 16px;
  cursor: pointer;
  
  &:active {
    background: $gray-50;
  }
}

.income-chart {
  margin-bottom: 16px;
}

.chart-placeholder {
  height: 80px;
  
  svg {
    width: 100%;
    height: 100%;
  }
}

.income-summary {
  display: flex;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid $border-light;
}

.summary-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  
  .label {
    font-size: 12px;
    color: $text-tertiary;
  }
  
  .value {
    font-size: 18px;
    font-weight: 700;
    color: $text-primary;
  }
  
  &.highlight .value {
    color: $income;
  }
}

// 公告列表
.notice-list {
  padding: 0 16px;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 0;
  border-bottom: 1px solid $border-light;
  cursor: pointer;
  
  &:last-child {
    border-bottom: none;
  }
  
  &:active {
    opacity: 0.7;
  }
}

.notice-tag {
  flex-shrink: 0;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
  
  &.primary {
    background: $primary-50;
    color: $primary;
  }
  
  &.error {
    background: #FFF1F0;
    color: $error;
  }
}

.notice-title {
  flex: 1;
  font-size: 14px;
  color: $text-primary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-date {
  flex-shrink: 0;
  font-size: 12px;
  color: $text-tertiary;
}

// 底部占位
.bottom-placeholder {
  height: 20px;
}
</style>
