<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useConsultationStore } from '@/stores/consultation'
import Tabbar from '@/components/Tabbar/index.vue'

const router = useRouter()
const consultationStore = useConsultationStore()

const activeTab = ref('pending')

const tabs = [
  { key: 'pending', label: '待接诊', icon: 'clock' },
  { key: 'processing', label: '进行中', icon: 'stethoscope' },
  { key: 'completed', label: '已完成', icon: 'check' }
]

const getFilteredConsultations = () => {
  switch (activeTab.value) {
    case 'pending':
      return consultationStore.pendingConsultations
    case 'processing':
      return consultationStore.processingConsultations
    case 'completed':
      return consultationStore.completedConsultations
    default:
      return []
  }
}

const getStatusClass = (status: string) => {
  switch (status) {
    case 'pending':
      return 'status-pending'
    case 'processing':
      return 'status-processing'
    case 'completed':
      return 'status-completed'
    default:
      return ''
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'pending':
      return '待接诊'
    case 'processing':
      return '进行中'
    case 'completed':
      return '已完成'
    default:
      return ''
  }
}

const goToChat = (id: string) => {
  router.push(`/consultation/chat/${id}`)
}

const goToDetail = (id: string) => {
  router.push(`/consultation/detail/${id}`)
}

const startConsultation = async (id: string, event: Event) => {
  event.stopPropagation()
  await consultationStore.startConsultation(id)
  router.push(`/consultation/chat/${id}`)
}

onMounted(() => {
  consultationStore.fetchConsultations()
})
</script>

<template>
  <div class="consultation-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <h1 class="page-title">问诊管理</h1>
    </div>

    <!-- 标签切换 -->
    <div class="tab-bar">
      <div
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-item"
        :class="{ active: activeTab === tab.key }"
        @click="activeTab = tab.key"
      >
        <span class="tab-label">{{ tab.label }}</span>
        <span v-if="tab.key === 'pending' && consultationStore.pendingConsultations.length > 0" class="tab-badge">
          {{ consultationStore.pendingConsultations.length }}
        </span>
      </div>
    </div>

    <!-- 问诊列表 -->
    <div class="consultation-list">
      <div
        v-for="item in getFilteredConsultations()"
        :key="item.id"
        class="consultation-card"
        @click="goToChat(item.id)"
      >
        <!-- 头部信息 -->
        <div class="card-header">
          <div class="patient-info">
            <div class="patient-avatar">
              {{ item.patientName[0] }}
              <span v-if="item.isUrgent" class="urgent-badge">急</span>
            </div>
            <div class="patient-meta">
              <div class="patient-name">
                {{ item.patientName }}
                <span class="patient-age">{{ item.patientAge }}岁</span>
                <span class="gender-tag" :class="item.patientGender === '男' ? 'male' : 'female'">
                  {{ item.patientGender }}
                </span>
              </div>
              <div class="consultation-type">{{ item.type }}</div>
            </div>
          </div>
          <div class="status-tag" :class="getStatusClass(item.status)">
            {{ getStatusText(item.status) }}
          </div>
        </div>

        <!-- 症状描述 -->
        <div class="symptom-section">
          <div class="section-label">症状描述</div>
          <div class="symptom-text">{{ item.symptom }}</div>
        </div>

        <!-- 底部信息 -->
        <div class="card-footer">
          <div class="time-info">
            <span v-if="item.status === 'pending'" class="wait-time">
              已等待 {{ item.waitTime }}
            </span>
            <span v-else-if="item.status === 'processing'" class="remain-time">
              剩余 {{ item.remainingTime }}
            </span>
            <span v-else class="complete-time">
              {{ item.createTime }}
            </span>
          </div>
          <div class="card-actions">
            <button 
              v-if="item.status === 'pending'" 
              class="btn-primary"
              @click="startConsultation(item.id, $event)"
            >
              开始接诊
            </button>
            <button v-else-if="item.status === 'processing'" class="btn-primary" @click="goToChat(item.id)">
              继续问诊
            </button>
            <button v-else class="btn-default" @click="goToDetail(item.id)">
              查看详情
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="getFilteredConsultations().length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path>
            <polyline points="22,6 12,13 2,6"></polyline>
          </svg>
        </div>
        <p class="empty-text">暂无{{ tabs.find(t => t.key === activeTab)?.label }}问诊</p>
      </div>
    </div>

    <Tabbar />
  </div>
</template>

<style lang="scss" scoped>
.consultation-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: calc(56px + env(safe-area-inset-bottom));
}

.page-header {
  padding: 16px;
  background: #fff;
  border-bottom: 1px solid $border-light;
  
  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    text-align: center;
  }
}

.tab-bar {
  display: flex;
  background: #fff;
  padding: 0 16px;
  border-bottom: 1px solid $border-light;
  
  .tab-item {
    flex: 1;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    padding: 12px 0;
    cursor: pointer;
    
    &.active {
      .tab-label {
        color: $primary;
        font-weight: 600;
      }
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 24px;
        height: 3px;
        background: $primary;
        border-radius: 2px;
      }
    }
  }
  
  .tab-label {
    font-size: 14px;
    color: $text-secondary;
  }
  
  .tab-badge {
    min-width: 16px;
    height: 16px;
    padding: 0 4px;
    background: $error;
    color: #fff;
    border-radius: 8px;
    font-size: 10px;
    font-weight: 600;
    line-height: 16px;
    text-align: center;
  }
}

.consultation-list {
  padding: 12px 16px;
}

.consultation-card {
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: transform 0.2s;
  
  &:active {
    transform: scale(0.99);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  border-bottom: 1px solid $border-light;
}

.patient-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.patient-avatar {
  position: relative;
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
  
  .urgent-badge {
    position: absolute;
    top: -4px;
    right: -4px;
    width: 18px;
    height: 18px;
    background: $error;
    color: #fff;
    border-radius: 50%;
    font-size: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.patient-meta {
  .patient-name {
    font-size: 15px;
    font-weight: 600;
    color: $text-primary;
    display: flex;
    align-items: center;
    gap: 6px;
    
    .patient-age {
      font-size: 13px;
      color: $text-secondary;
      font-weight: 400;
    }
    
    .gender-tag {
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 500;
      
      &.male {
        background: #E6F7FF;
        color: #1890FF;
      }
      
      &.female {
        background: #FFF0F6;
        color: #EB2F96;
      }
    }
  }
  
  .consultation-type {
    font-size: 12px;
    color: $text-tertiary;
    margin-top: 4px;
  }
}

.status-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  
  &.status-pending {
    background: #FFF7E6;
    color: #FA8C16;
  }
  
  &.status-processing {
    background: #E6F7FF;
    color: #1890FF;
  }
  
  &.status-completed {
    background: #F6FFED;
    color: #52C41A;
  }
}

.symptom-section {
  padding: 12px 16px;
  background: #FAFAFA;
  
  .section-label {
    font-size: 12px;
    color: $text-tertiary;
    margin-bottom: 4px;
  }
  
  .symptom-text {
    font-size: 14px;
    color: $text-primary;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-top: 1px solid $border-light;
}

.time-info {
  font-size: 12px;
  color: $text-tertiary;
  
  .wait-time {
    color: $warning;
  }
  
  .remain-time {
    color: $primary;
  }
}

.card-actions {
  display: flex;
  gap: 8px;
  
  button {
    padding: 6px 14px;
    border-radius: 16px;
    font-size: 13px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    border: none;
    
    &.btn-primary {
      background: $primary;
      color: #fff;
      
      &:active {
        background: $primary-dark;
      }
    }
    
    &.btn-default {
      background: #F5F5F5;
      color: $text-secondary;
      
      &:active {
        background: #E8E8E8;
      }
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  
  .empty-icon {
    width: 80px;
    height: 80px;
    background: #F5F5F5;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
    
    svg {
      width: 40px;
      height: 40px;
      color: $text-tertiary;
    }
  }
  
  .empty-text {
    font-size: 14px;
    color: $text-secondary;
  }
}
</style>