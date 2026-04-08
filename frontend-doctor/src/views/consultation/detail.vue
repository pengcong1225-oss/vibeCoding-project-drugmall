<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useConsultationStore } from '@/stores/consultation'

const route = useRoute()
const router = useRouter()
const consultationStore = useConsultationStore()

const consultationId = route.params.id as string

const goBack = () => {
  router.back()
}

const goToPatient = (patientId: string) => {
  router.push(`/patients/detail/${patientId}`)
}

onMounted(() => {
  consultationStore.fetchConsultationDetail(consultationId)
})
</script>

<template>
  <div class="detail-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="header-left" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </div>
      <h1 class="page-title">问诊详情</h1>
      <div class="header-right"></div>
    </div>

    <!-- 内容区域 -->
    <div class="content-area">
      <!-- 患者信息卡片 -->
      <div class="info-card patient-card" @click="goToPatient(consultationStore.currentConsultation?.patientId || '')">
        <div class="card-header">
          <div class="header-icon patient">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
          </div>
          <h3 class="header-title">患者信息</h3>
          <svg class="arrow-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </div>
        <div class="card-body">
          <div class="info-row">
            <div class="info-item">
              <span class="label">姓名</span>
              <span class="value">{{ consultationStore.currentConsultation?.patientName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别</span>
              <span class="value">{{ consultationStore.currentConsultation?.patientGender || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">年龄</span>
              <span class="value">{{ consultationStore.currentConsultation?.patientAge || '-' }}岁</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 问诊信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <div class="header-icon consultation">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4.8 2.3A.3.3 0 1 0 5 2H4a2 2 0 0 0-2 2v5a6 6 0 0 0 6 6v0a6 6 0 0 0 6-6V4a2 2 0 0 0-2-2h-1a.2.2 0 1 0 .3.3"></path>
              <path d="M8 15v1a6 6 0 0 0 6 6v0a6 6 0 0 0 6-6v-4"></path>
              <circle cx="20" cy="10" r="2"></circle>
            </svg>
          </div>
          <h3 class="header-title">问诊信息</h3>
        </div>
        <div class="card-body">
          <div class="info-row">
            <div class="info-item">
              <span class="label">问诊类型</span>
              <span class="value">{{ consultationStore.currentConsultation?.type || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">问诊状态</span>
              <span class="value status" :class="consultationStore.currentConsultation?.status">
                {{ consultationStore.currentConsultation?.status === 'pending' ? '待接诊' : 
                   consultationStore.currentConsultation?.status === 'processing' ? '进行中' : '已完成' }}
              </span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-item full">
              <span class="label">症状描述</span>
              <span class="value symptom">{{ consultationStore.currentConsultation?.symptom || '-' }}</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-item">
              <span class="label">创建时间</span>
              <span class="value">{{ consultationStore.currentConsultation?.createTime || '-' }}</span>
            </div>
            <div class="info-item" v-if="consultationStore.currentConsultation?.status !== 'pending'">
              <span class="label">是否处方药</span>
              <span class="value">{{ consultationStore.currentConsultation?.isRx ? '是' : '否' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.detail-page {
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

.content-area {
  padding: 12px;
}

.info-card {
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  
  &.patient-card {
    cursor: pointer;
    
    &:active {
      opacity: 0.8;
    }
  }
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 16px 12px;
  
  .header-icon {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    svg {
      width: 18px;
      height: 18px;
    }
    
    &.patient {
      background: #E6F7FF;
      color: #1890FF;
    }
    
    &.consultation {
      background: #F6FFED;
      color: #52C41A;
    }
  }
  
  .header-title {
    flex: 1;
    font-size: 15px;
    font-weight: 600;
    color: $text-primary;
  }
  
  .arrow-icon {
    width: 16px;
    height: 16px;
    color: $text-tertiary;
  }
}

.card-body {
  padding: 0 16px 16px;
}

.info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.info-item {
  flex: 1;
  min-width: 100px;
  
  &.full {
    flex: 0 0 100%;
  }
  
  .label {
    display: block;
    font-size: 12px;
    color: $text-tertiary;
    margin-bottom: 4px;
  }
  
  .value {
    font-size: 14px;
    color: $text-primary;
    font-weight: 500;
    
    &.status {
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;
      
      &.pending {
        background: #FFF7E6;
        color: #FA8C16;
      }
      
      &.processing {
        background: #E6F7FF;
        color: #1890FF;
      }
      
      &.completed {
        background: #F6FFED;
        color: #52C41A;
      }
    }
    
    &.symptom {
      font-weight: 400;
      color: $text-secondary;
      line-height: 1.5;
    }
  }
}
</style>
