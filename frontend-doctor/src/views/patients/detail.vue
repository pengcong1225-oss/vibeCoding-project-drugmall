<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePatientStore } from '@/stores/patient'

const route = useRoute()
const router = useRouter()
const patientStore = usePatientStore()

const patientId = route.params.id as string

const goBack = () => {
  router.back()
}

const goToRecords = () => {
  router.push(`/patients/records/${patientId}`)
}

const goToConsultation = () => {
  router.push('/consultation')
}

onMounted(() => {
  patientStore.fetchPatientDetail(patientId)
})
</script>

<template>
  <div class="patient-detail-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="header-left" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </div>
      <h1 class="page-title">患者详情</h1>
      <div class="header-right">
        <button class="btn-consult" @click="goToConsultation">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          </svg>
          问诊
        </button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-area" v-if="patientStore.currentPatient">
      <!-- 患者基本信息卡片 -->
      <div class="patient-header-card">
        <div class="patient-avatar">
          {{ patientStore.currentPatient.name[0] }}
          <span v-if="patientStore.currentPatient.isVip" class="vip-badge">VIP</span>
        </div>
        <div class="patient-basic-info">
          <div class="info-row">
            <span class="patient-name">{{ patientStore.currentPatient.name }}</span>
            <span class="patient-gender" :class="patientStore.currentPatient.gender">
              {{ patientStore.currentPatient.gender }}
            </span>
            <span class="patient-age">{{ patientStore.currentPatient.age }}岁</span>
          </div>
          <div class="info-row">
            <span class="patient-phone">{{ patientStore.currentPatient.phone }}</span>
          </div>
          <div class="patient-tags">
            <span v-for="(tag, index) in patientStore.currentPatient.tags" :key="index" class="tag">
              {{ tag }}
            </span>
          </div>
        </div>
      </div>

      <!-- 就诊统计 -->
      <div class="stats-card">
        <div class="stat-item">
          <div class="stat-value">{{ patientStore.currentPatient.visitCount }}</div>
          <div class="stat-label">就诊次数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ patientStore.currentPatient.lastVisit }}</div>
          <div class="stat-label">最近就诊</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">3</div>
          <div class="stat-label">处方数量</div>
        </div>
      </div>

      <!-- 健康档案 -->
      <div class="info-section">
        <div class="section-title">健康档案</div>
        <div class="info-list">
          <div class="info-row-item">
            <span class="label">过敏史</span>
            <span class="value">{{ patientStore.currentPatient.allergies || '无' }}</span>
          </div>
          <div class="info-row-item">
            <span class="label">既往病史</span>
            <span class="value">{{ patientStore.currentPatient.medicalHistory || '无' }}</span>
          </div>
          <div class="info-row-item">
            <span class="label">诊断记录</span>
            <span class="value">
              <span v-for="(diag, index) in patientStore.currentPatient.diagnosis" :key="index" class="diagnosis-tag">
                {{ diag }}
              </span>
            </span>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions">
        <div class="action-item" @click="goToRecords">
          <div class="action-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
            </svg>
          </div>
          <span class="action-label">查看病历</span>
        </div>
        <div class="action-item" @click="goToConsultation">
          <div class="action-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4.8 2.3A.3.3 0 1 0 5 2H4a2 2 0 0 0-2 2v5a6 6 0 0 0 6 6v0a6 6 0 0 0 6-6V4a2 2 0 0 0-2-2h-1a.2.2 0 1 0 .3.3"></path>
              <path d="M8 15v1a6 6 0 0 0 6 6v0a6 6 0 0 0 6-6v-4"></path>
            </svg>
          </div>
          <span class="action-label">发起问诊</span>
        </div>
        <div class="action-item">
          <div class="action-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
            </svg>
          </div>
          <span class="action-label">开具处方</span>
        </div>
        <div class="action-item">
          <div class="action-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
          </div>
          <span class="action-label">预约随访</span>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else-if="patientStore.loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
          <circle cx="9" cy="7" r="4"></circle>
          <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
          <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
        </svg>
      </div>
      <p class="empty-text">未找到患者信息</p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.patient-detail-page {
  min-height: 100vh;
  background: $bg-primary;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid $border-light;
  position: sticky;
  top: 0;
  z-index: 100;
  
  .header-left {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 4px;
    
    svg {
      width: 24px;
      height: 24px;
      color: $text-primary;
    }
  }
  
  .page-title {
    font-size: 17px;
    font-weight: 600;
    color: $text-primary;
  }
  
  .header-right {
    .btn-consult {
      display: flex;
      align-items: center;
      gap: 4px;
      padding: 6px 12px;
      background: $primary;
      border: none;
      border-radius: 16px;
      color: #fff;
      font-size: 13px;
      cursor: pointer;
      
      svg {
        width: 14px;
        height: 14px;
      }
    }
  }
}

.content-area {
  padding: 12px;
}

.patient-header-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  border-radius: 16px;
  margin-bottom: 12px;
  color: #fff;
  
  .patient-avatar {
    position: relative;
    width: 72px;
    height: 72px;
    border-radius: 50%;
    background: rgba(255,255,255,0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
    font-weight: 600;
    flex-shrink: 0;
    
    .vip-badge {
      position: absolute;
      bottom: 0;
      right: 0;
      padding: 2px 8px;
      background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
      color: #fff;
      border-radius: 10px;
      font-size: 10px;
      font-weight: 700;
    }
  }
  
  .patient-basic-info {
    flex: 1;
    
    .info-row {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 6px;
      
      .patient-name {
        font-size: 20px;
        font-weight: 600;
      }
      
      .patient-gender {
        padding: 2px 8px;
        background: rgba(255,255,255,0.2);
        border-radius: 4px;
        font-size: 12px;
      }
      
      .patient-age {
        font-size: 14px;
        opacity: 0.9;
      }
      
      .patient-phone {
        font-size: 14px;
        opacity: 0.9;
      }
    }
    
    .patient-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-top: 8px;
      
      .tag {
        padding: 4px 10px;
        background: rgba(255,255,255,0.15);
        border-radius: 12px;
        font-size: 12px;
      }
    }
  }
}

.stats-card {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 12px;
  
  .stat-item {
    text-align: center;
    padding: 16px;
    background: #fff;
    border-radius: 12px;
    
    .stat-value {
      font-size: 20px;
      font-weight: 700;
      color: $primary;
      margin-bottom: 4px;
    }
    
    .stat-label {
      font-size: 12px;
      color: $text-tertiary;
    }
  }
}

.info-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  
  .section-title {
    font-size: 15px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid $border-light;
  }
  
  .info-list {
    .info-row-item {
      display: flex;
      padding: 12px 0;
      border-bottom: 1px solid $border-light;
      
      &:last-child {
        border-bottom: none;
      }
      
      .label {
        width: 80px;
        font-size: 14px;
        color: $text-tertiary;
      }
      
      .value {
        flex: 1;
        font-size: 14px;
        color: $text-primary;
        
        .diagnosis-tag {
          display: inline-block;
          padding: 2px 8px;
          background: $primary-50;
          color: $primary;
          border-radius: 4px;
          font-size: 12px;
          margin-right: 6px;
          margin-bottom: 4px;
        }
      }
    }
  }
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-top: 12px;
  
  .action-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 16px 8px;
    background: #fff;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s;
    
    &:active {
      transform: scale(0.98);
      background: $gray-50;
    }
    
    .action-icon {
      width: 44px;
      height: 44px;
      background: $primary-50;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      svg {
        width: 22px;
        height: 22px;
        color: $primary;
      }
    }
    
    .action-label {
      font-size: 12px;
      color: $text-secondary;
    }
  }
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid $primary;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

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
</style>
