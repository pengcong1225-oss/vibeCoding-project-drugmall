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

const goToDetail = (recordId: string) => {
  // 可以跳转到病历详情
  console.log('查看病历详情:', recordId)
}

onMounted(() => {
  patientStore.fetchMedicalRecords(patientId)
})
</script>

<template>
  <div class="records-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="header-left" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </div>
      <h1 class="page-title">就诊记录</h1>
      <div class="header-right"></div>
    </div>

    <!-- 时间轴列表 -->
    <div class="records-timeline" v-if="patientStore.medicalRecords.length > 0">
      <div
        v-for="(record, index) in patientStore.medicalRecords"
        :key="record.id"
        class="timeline-item"
        @click="goToDetail(record.id)"
      >
        <div class="timeline-marker">
          <div class="marker-dot"></div>
          <div v-if="index !== patientStore.medicalRecords.length - 1" class="marker-line"></div>
        </div>
        <div class="timeline-content">
          <div class="record-card">
            <div class="record-header">
              <div class="record-date">
                <span class="date-day">{{ record.date.split(' ')[0].split('-')[2] }}</span>
                <span class="date-month">{{ record.date.split(' ')[0].split('-')[1] }}月</span>
              </div>
              <div class="record-type">
                <span class="type-tag">{{ record.type }}</span>
              </div>
            </div>
            <div class="record-body">
              <div class="info-row">
                <span class="label">诊断</span>
                <span class="value">{{ record.diagnosis }}</span>
              </div>
              <div class="info-row">
                <span class="label">处方</span>
                <span class="value">{{ record.prescription }}</span>
              </div>
              <div class="info-row">
                <span class="label">备注</span>
                <span class="value">{{ record.notes }}</span>
              </div>
            </div>
            <div class="record-footer">
              <div class="doctor-info">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                <span>{{ record.doctor }}</span>
              </div>
              <div class="record-time">{{ record.date.split(' ')[1] }}</div>
            </div>
          </div>
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
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
          <polyline points="14 2 14 8 20 8"></polyline>
          <line x1="16" y1="13" x2="8" y2="13"></line>
          <line x1="16" y1="17" x2="8" y2="17"></line>
        </svg>
      </div>
      <p class="empty-text">暂无就诊记录</p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.records-page {
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
    width: 32px;
  }
}

.records-timeline {
  padding: 20px 16px;
  
  .timeline-item {
    display: flex;
    gap: 16px;
    cursor: pointer;
    
    &:active {
      .record-card {
        transform: scale(0.99);
      }
    }
    
    .timeline-marker {
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .marker-dot {
        width: 12px;
        height: 12px;
        background: $primary;
        border-radius: 50%;
        border: 2px solid #fff;
        box-shadow: 0 0 0 2px $primary;
      }
      
      .marker-line {
        flex: 1;
        width: 2px;
        background: linear-gradient(to bottom, $primary, transparent);
        margin-top: 8px;
      }
    }
    
    .timeline-content {
      flex: 1;
      padding-bottom: 24px;
      
      .record-card {
        background: #fff;
        border-radius: 12px;
        padding: 16px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.04);
        transition: transform 0.2s;
        
        .record-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;
          padding-bottom: 12px;
          border-bottom: 1px dashed $border-light;
          
          .record-date {
            display: flex;
            align-items: baseline;
            gap: 4px;
            
            .date-day {
              font-size: 24px;
              font-weight: 700;
              color: $primary;
            }
            
            .date-month {
              font-size: 13px;
              color: $text-secondary;
            }
          }
          
          .record-type {
            .type-tag {
              padding: 4px 10px;
              background: $primary-50;
              color: $primary;
              border-radius: 12px;
              font-size: 12px;
              font-weight: 500;
            }
          }
        }
        
        .record-body {
          margin-bottom: 12px;
          
          .info-row {
            display: flex;
            margin-bottom: 8px;
            
            .label {
              width: 50px;
              font-size: 13px;
              color: $text-tertiary;
            }
            
            .value {
              flex: 1;
              font-size: 13px;
              color: $text-primary;
              line-height: 1.5;
            }
          }
        }
        
        .record-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-top: 12px;
          border-top: 1px solid $border-light;
          
          .doctor-info {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 12px;
            color: $text-tertiary;
            
            svg {
              width: 14px;
              height: 14px;
            }
          }
          
          .record-time {
            font-size: 12px;
            color: $text-tertiary;
          }
        }
      }
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
