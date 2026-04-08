<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePrescriptionStore } from '@/stores/prescription'

const route = useRoute()
const router = useRouter()
const prescriptionStore = usePrescriptionStore()

const prescriptionId = route.params.id as string

const goBack = () => {
  router.back()
}

const getStatusClass = (status: string) => {
  switch (status) {
    case 'pending':
      return 'status-pending'
    case 'approved':
      return 'status-approved'
    case 'rejected':
      return 'status-rejected'
    default:
      return ''
  }
}

onMounted(() => {
  prescriptionStore.fetchPrescriptionDetail(prescriptionId)
})
</script>

<template>
  <div class="prescription-detail-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="header-left" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </div>
      <h1 class="page-title">处方详情</h1>
      <div class="header-right"></div>
    </div>

    <!-- 内容区域 -->
    <div class="content-area" v-if="prescriptionStore.currentPrescription">
      <!-- 状态卡片 -->
      <div class="status-card" :class="getStatusClass(prescriptionStore.currentPrescription.status)">
        <div class="status-icon">
          <svg v-if="prescriptionStore.currentPrescription.status === 'pending'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <polyline points="12 6 12 12 16 14"></polyline>
          </svg>
          <svg v-else-if="prescriptionStore.currentPrescription.status === 'approved'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
            <polyline points="22 4 12 14.01 9 11.01"></polyline>
          </svg>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="15" y1="9" x2="9" y2="15"></line>
            <line x1="9" y1="9" x2="15" y2="15"></line>
          </svg>
        </div>
        <div class="status-info">
          <div class="status-text">{{ prescriptionStore.currentPrescription.statusText }}</div>
          <div class="status-time">{{ prescriptionStore.currentPrescription.createTime }}</div>
        </div>
      </div>

      <!-- 患者信息 -->
      <div class="info-card">
        <div class="card-title">患者信息</div>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">姓名</span>
            <span class="value">{{ prescriptionStore.currentPrescription.patientName }}</span>
          </div>
          <div class="info-item">
            <span class="label">性别</span>
            <span class="value">{{ prescriptionStore.currentPrescription.patientGender }}</span>
          </div>
          <div class="info-item">
            <span class="label">年龄</span>
            <span class="value">{{ prescriptionStore.currentPrescription.patientAge }}岁</span>
          </div>
        </div>
      </div>

      <!-- 诊断信息 -->
      <div class="info-card">
        <div class="card-title">诊断信息</div>
        <div class="diagnosis-content">
          <div class="diagnosis-item">
            <span class="label">诊断结果</span>
            <span class="value">{{ prescriptionStore.currentPrescription.diagnosis }}</span>
          </div>
        </div>
      </div>

      <!-- 药品清单 -->
      <div class="info-card">
        <div class="card-title">
          药品清单
          <span class="drug-count">共 {{ prescriptionStore.currentPrescription.drugs.length }} 种</span>
        </div>
        <div class="drug-list">
          <div 
            v-for="(drug, index) in prescriptionStore.currentPrescription.drugs" 
            :key="index" 
            class="drug-item"
          >
            <div class="drug-header">
              <span class="drug-index">{{ index + 1 }}</span>
              <span class="drug-name">{{ drug.name }}</span>
              <span class="drug-spec">{{ drug.spec }}</span>
            </div>
            <div class="drug-usage">
              <span class="usage-tag">{{ drug.dosage }}</span>
              <span class="usage-tag">{{ drug.frequency }}</span>
              <span class="usage-tag">{{ drug.duration }}</span>
            </div>
            <div class="drug-amount">
              <span class="quantity">×{{ drug.quantity }}{{ drug.unit }}</span>
              <span class="price">¥{{ (drug.price * drug.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </div>
        <div class="total-amount">
          <span class="label">合计金额</span>
          <span class="amount">¥{{ prescriptionStore.currentPrescription.totalAmount.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 审核信息 -->
      <div class="info-card" v-if="prescriptionStore.currentPrescription.pharmacist">
        <div class="card-title">审核信息</div>
        <div class="review-info">
          <div class="review-item">
            <span class="label">审核药师</span>
            <span class="value">{{ prescriptionStore.currentPrescription.pharmacist }}</span>
          </div>
          <div class="review-item">
            <span class="label">审核时间</span>
            <span class="value">{{ prescriptionStore.currentPrescription.reviewTime }}</span>
          </div>
        </div>
      </div>

      <!-- 拒绝原因 -->
      <div class="reject-card" v-if="prescriptionStore.currentPrescription.rejectReason">
        <div class="reject-header">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
          </svg>
          <span>拒绝原因</span>
        </div>
        <p class="reject-reason">{{ prescriptionStore.currentPrescription.rejectReason }}</p>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else-if="prescriptionStore.loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
          <polyline points="14 2 14 8 20 8"></polyline>
        </svg>
      </div>
      <p class="empty-text">未找到处方信息</p>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.prescription-detail-page {
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

.content-area {
  padding: 12px;
}

.status-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
  
  &.status-pending {
    background: linear-gradient(135deg, #FFF7E6 0%, #FFFBF0 100%);
    border: 1px solid #FFE7BA;
    
    .status-icon {
      background: #FA8C16;
    }
    
    .status-text {
      color: #D46B08;
    }
  }
  
  &.status-approved {
    background: linear-gradient(135deg, #F6FFED 0%, #F0FFF0 100%);
    border: 1px solid #B7EB8F;
    
    .status-icon {
      background: #52C41A;
    }
    
    .status-text {
      color: #389E0D;
    }
  }
  
  &.status-rejected {
    background: linear-gradient(135deg, #FFF1F0 0%, #FFF5F5 100%);
    border: 1px solid #FFCCC7;
    
    .status-icon {
      background: #FF4D4F;
    }
    
    .status-text {
      color: #CF1322;
    }
  }
  
  .status-icon {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    
    svg {
      width: 24px;
      height: 24px;
      color: #fff;
    }
  }
  
  .status-info {
    .status-text {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 4px;
    }
    
    .status-time {
      font-size: 13px;
      color: $text-tertiary;
    }
  }
}

.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  
  .card-title {
    font-size: 15px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .drug-count {
      font-size: 13px;
      color: $text-tertiary;
      font-weight: 400;
    }
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  
  .label {
    font-size: 12px;
    color: $text-tertiary;
  }
  
  .value {
    font-size: 14px;
    color: $text-primary;
    font-weight: 500;
  }
}

.diagnosis-content {
  .diagnosis-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
    
    .label {
      font-size: 12px;
      color: $text-tertiary;
    }
    
    .value {
      font-size: 14px;
      color: $text-primary;
      line-height: 1.5;
    }
  }
}

.drug-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.drug-item {
  padding: 12px;
  background: #FAFAFA;
  border-radius: 8px;
  
  .drug-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    
    .drug-index {
      width: 20px;
      height: 20px;
      background: $primary;
      color: #fff;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 11px;
      font-weight: 600;
    }
    
    .drug-name {
      font-size: 14px;
      font-weight: 600;
      color: $text-primary;
    }
    
    .drug-spec {
      font-size: 12px;
      color: $text-tertiary;
    }
  }
  
  .drug-usage {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-bottom: 8px;
    
    .usage-tag {
      padding: 2px 8px;
      background: #E6F7FF;
      color: #1890FF;
      border-radius: 4px;
      font-size: 12px;
    }
  }
  
  .drug-amount {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .quantity {
      font-size: 12px;
      color: $text-secondary;
    }
    
    .price {
      font-size: 14px;
      font-weight: 600;
      color: $income;
    }
  }
}

.total-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  margin-top: 16px;
  border-top: 1px solid $border-light;
  
  .label {
    font-size: 14px;
    color: $text-secondary;
  }
  
  .amount {
    font-size: 20px;
    font-weight: 700;
    color: $income;
  }
}

.review-info {
  .review-item {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    border-bottom: 1px solid $border-light;
    
    &:last-child {
      border-bottom: none;
    }
    
    .label {
      font-size: 13px;
      color: $text-tertiary;
    }
    
    .value {
      font-size: 13px;
      color: $text-primary;
    }
  }
}

.reject-card {
  background: #FFF1F0;
  border: 1px solid #FFCCC7;
  border-radius: 12px;
  padding: 16px;
  margin-top: 12px;
  
  .reject-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    
    svg {
      width: 20px;
      height: 20px;
      color: $error;
    }
    
    span {
      font-size: 14px;
      font-weight: 600;
      color: $error;
    }
  }
  
  .reject-reason {
    font-size: 13px;
    color: $text-secondary;
    line-height: 1.5;
    padding-left: 28px;
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
