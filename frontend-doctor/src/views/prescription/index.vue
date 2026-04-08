<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePrescriptionStore } from '@/stores/prescription'

const router = useRouter()
const prescriptionStore = usePrescriptionStore()

const activeTab = ref('all')

const tabs = [
  { key: 'all', label: '全部' },
  { key: 'pending', label: '待审核' },
  { key: 'approved', label: '已通过' },
  { key: 'rejected', label: '已拒绝' }
]

const getFilteredPrescriptions = () => {
  switch (activeTab.value) {
    case 'pending':
      return prescriptionStore.pendingPrescriptions
    case 'approved':
      return prescriptionStore.approvedPrescriptions
    case 'rejected':
      return prescriptionStore.rejectedPrescriptions
    default:
      return prescriptionStore.prescriptions
  }
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

const goToDetail = (id: string) => {
  router.push(`/prescription/detail/${id}`)
}

const goToCreate = () => {
  router.push('/prescription/create')
}

onMounted(() => {
  prescriptionStore.fetchPrescriptions()
})
</script>

<template>
  <div class="prescription-page">
    <!-- 顶部导航 -->
    <div class="page-header">
      <h1 class="page-title">处方管理</h1>
      <button class="btn-create" @click="goToCreate">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        <span>开处方</span>
      </button>
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
        {{ tab.label }}
      </div>
    </div>

    <!-- 处方列表 -->
    <div class="prescription-list">
      <div
        v-for="item in getFilteredPrescriptions()"
        :key="item.id"
        class="prescription-card"
        @click="goToDetail(item.id)"
      >
        <div class="card-header">
          <div class="header-left">
            <span class="prescription-id">{{ item.id }}</span>
            <span class="create-time">{{ item.createTime }}</span>
          </div>
          <span class="status-tag" :class="getStatusClass(item.status)">
            {{ item.statusText }}
          </span>
        </div>

        <div class="card-body">
          <div class="patient-info">
            <div class="patient-avatar">{{ item.patientName[0] }}</div>
            <div class="patient-detail">
              <div class="patient-name">{{ item.patientName }} <span class="patient-age">{{ item.patientAge }}岁</span></div>
              <div class="diagnosis">{{ item.diagnosis }}</div>
            </div>
          </div>

          <div class="drugs-preview">
            <div class="drug-count">{{ item.drugs.length }}种药品</div>
            <div class="drug-names">{{ item.drugs.map(d => d.name).join('、') }}</div>
          </div>
        </div>

        <div class="card-footer">
          <div class="total-amount">
            <span class="label">合计：</span>
            <span class="amount">¥{{ item.totalAmount.toFixed(2) }}</span>
          </div>
          <div v-if="item.pharmacist" class="review-info">
            <span>审核：{{ item.pharmacist }} · {{ item.reviewTime }}</span>
          </div>
        </div>

        <div v-if="item.rejectReason" class="reject-reason">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
          </svg>
          <span>拒绝原因：{{ item.rejectReason }}</span>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="getFilteredPrescriptions().length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
        </div>
        <p class="empty-text">暂无{{ tabs.find(t => t.key === activeTab)?.label }}处方</p>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.prescription-page {
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
  
  .page-title {
    font-size: 17px;
    font-weight: 600;
    color: $text-primary;
  }
  
  .btn-create {
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
    
    &:active {
      background: darken($primary, 5%);
    }
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
    padding: 12px 0;
    text-align: center;
    font-size: 14px;
    color: $text-secondary;
    cursor: pointer;
    
    &.active {
      color: $primary;
      font-weight: 600;
      
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
}

.prescription-list {
  padding: 12px;
}

.prescription-card {
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  cursor: pointer;
  
  &:active {
    opacity: 0.9;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #FAFAFA;
  border-bottom: 1px solid $border-light;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .prescription-id {
      font-size: 13px;
      color: $text-primary;
      font-weight: 500;
    }
    
    .create-time {
      font-size: 12px;
      color: $text-tertiary;
    }
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
  
  &.status-approved {
    background: #F6FFED;
    color: #52C41A;
  }
  
  &.status-rejected {
    background: #FFF1F0;
    color: #FF4D4F;
  }
}

.card-body {
  padding: 16px;
}

.patient-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed $border-light;
  
  .patient-avatar {
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
  }
  
  .patient-detail {
    flex: 1;
    
    .patient-name {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 4px;
      
      .patient-age {
        font-size: 13px;
        color: $text-secondary;
        font-weight: 400;
      }
    }
    
    .diagnosis {
      font-size: 13px;
      color: $text-secondary;
    }
  }
}

.drugs-preview {
  .drug-count {
    font-size: 13px;
    color: $text-secondary;
    margin-bottom: 8px;
  }
  
  .drug-names {
    font-size: 14px;
    color: $text-primary;
    line-height: 1.6;
  }
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #FAFAFA;
  border-top: 1px solid $border-light;
  
  .total-amount {
    .label {
      font-size: 13px;
      color: $text-secondary;
    }
    
    .amount {
      font-size: 16px;
      font-weight: 600;
      color: $income;
    }
  }
  
  .review-info {
    font-size: 12px;
    color: $text-tertiary;
  }
}

.reject-reason {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px 16px;
  background: #FFF1F0;
  border-top: 1px solid #FFCCC7;
  
  svg {
    width: 16px;
    height: 16px;
    color: $error;
    flex-shrink: 0;
    margin-top: 2px;
  }
  
  span {
    font-size: 13px;
    color: $error;
    line-height: 1.5;
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
