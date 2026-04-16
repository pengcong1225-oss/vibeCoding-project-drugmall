<template>
  <div class="income-detail-page">
    <!-- 顶部导航 -->
    <header class="page-header">
      <button class="back-btn" @click="goBack">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M15 18l-6-6 6-6" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <h1 class="page-title">收入明细</h1>
      <button class="filter-btn" @click="showFilter = true">筛选</button>
    </header>

    <!-- 日期筛选 -->
    <div class="date-filter">
      <div 
        v-for="item in dateOptions" 
        :key="item.value"
        :class="['date-option', { active: dateRange === item.value }]"
        @click="dateRange = item.value"
      >
        {{ item.label }}
      </div>
    </div>

    <!-- 统计汇总 -->
    <div class="summary-card">
      <div class="summary-item">
        <span class="summary-value">{{ formatMoney(summary.totalIncome) }}</span>
        <span class="summary-label">收入合计</span>
      </div>
      <div class="summary-item">
        <span class="summary-value">{{ summary.inquiryCount }}</span>
        <span class="summary-label">问诊次数</span>
      </div>
    </div>

    <!-- 收入列表 -->
    <div class="income-list">
      <div class="list-header">
        <span>共 {{ incomeList.length }} 条记录</span>
        <span class="export-btn" @click="exportData">导出账单</span>
      </div>
      
      <div v-for="item in incomeList" :key="item.id" class="income-card">
        <div class="card-header">
          <div class="income-type">
            <span class="type-icon">{{ item.type === '图文问诊' ? '💬' : '📝' }}</span>
            <span class="type-name">{{ item.type }}</span>
          </div>
          <span class="income-amount">+{{ formatMoney(item.amount) }}</span>
        </div>
        <div class="card-body">
          <div class="info-row">
            <span class="label">患者</span>
            <span class="value">{{ item.patientName }}</span>
          </div>
          <div class="info-row">
            <span class="label">时间</span>
            <span class="value">{{ item.time }}</span>
          </div>
          <div class="info-row">
            <span class="label">收入单号</span>
            <span class="value">{{ item.id }}</span>
          </div>
        </div>
        <div class="card-footer">
          <span :class="['status-tag', item.status]">{{ item.status }}</span>
          <span class="view-detail" @click="viewDetail(item)">查看详情 ></span>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="showDetail && currentItem" class="detail-modal" @click="showDetail = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <span>收入详情</span>
          <span class="close-btn" @click="showDetail = false">×</span>
        </div>
        <div class="modal-body">
          <div class="detail-amount">+{{ formatMoney(currentItem.amount) }}</div>
          <div class="detail-status" :class="currentItem.status">{{ currentItem.status }}</div>
          
          <div class="detail-list">
            <div class="detail-item">
              <span class="label">收入单号</span>
              <span class="value">{{ currentItem.id }}</span>
            </div>
            <div class="detail-item">
              <span class="label">问诊单号</span>
              <span class="value">{{ currentItem.inquiryId }}</span>
            </div>
            <div class="detail-item">
              <span class="label">患者信息</span>
              <span class="value">{{ currentItem.patientName }} {{ currentItem.patientGender }} {{ currentItem.patientAge }}岁</span>
            </div>
            <div class="detail-item">
              <span class="label">问诊类型</span>
              <span class="value">{{ currentItem.type }}</span>
            </div>
            <div class="detail-item">
              <span class="label">问诊时长</span>
              <span class="value">{{ currentItem.duration }}分钟</span>
            </div>
            <div class="detail-item">
              <span class="label">收入时间</span>
              <span class="value">{{ currentItem.time }}</span>
            </div>
          </div>

          <div class="amount-breakdown">
            <div class="breakdown-title">金额明细</div>
            <div class="breakdown-item">
              <span>订单金额</span>
              <span>{{ formatMoney(currentItem.totalAmount) }}</span>
            </div>
            <div class="breakdown-item">
              <span>平台分成 ({{ currentItem.platformRatio }}%)</span>
              <span>-{{ formatMoney(currentItem.platformAmount) }}</span>
            </div>
            <div class="breakdown-item total">
              <span>实际收入</span>
              <span>{{ formatMoney(currentItem.doctorAmount) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const dateRange = ref('7')
const showFilter = ref(false)
const showDetail = ref(false)
const currentItem = ref<any>(null)

const dateOptions = [
  { label: '近7天', value: '7' },
  { label: '近30天', value: '30' },
  { label: '近90天', value: '90' }
]

const summary = reactive({
  totalIncome: 12500.00,
  inquiryCount: 156
})

const incomeList = ref([
  { 
    id: 'INC202404070001', 
    inquiryId: 'INQ202404070001',
    type: '图文问诊', 
    patientName: '李*', 
    patientGender: '女',
    patientAge: 35,
    amount: 80.00, 
    totalAmount: 100.00,
    platformRatio: 20,
    platformAmount: 20.00,
    doctorAmount: 80.00,
    duration: 15,
    time: '2024-04-07 14:30:00',
    status: '已结算'
  },
  { 
    id: 'INC202404070002', 
    inquiryId: 'INQ202404070002',
    type: '复诊开方', 
    patientName: '王*', 
    patientGender: '男',
    patientAge: 28,
    amount: 60.00, 
    totalAmount: 80.00,
    platformRatio: 25,
    platformAmount: 20.00,
    doctorAmount: 60.00,
    duration: 8,
    time: '2024-04-07 10:15:00',
    status: '已结算'
  },
  { 
    id: 'INC202404060001', 
    inquiryId: 'INQ202404060001',
    type: '图文问诊', 
    patientName: '张*', 
    patientGender: '女',
    patientAge: 42,
    amount: 80.00, 
    totalAmount: 100.00,
    platformRatio: 20,
    platformAmount: 20.00,
    doctorAmount: 80.00,
    duration: 22,
    time: '2024-04-06 16:45:00',
    status: '已结算'
  }
])

const formatMoney = (amount: number) => {
  return '¥' + amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const goBack = () => {
  router.back()
}

const viewDetail = (item: any) => {
  currentItem.value = item
  showDetail.value = true
}

const exportData = () => {
  alert('账单导出功能开发中...')
}
</script>

<style scoped lang="scss">
.income-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fff;

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

  .filter-btn {
    width: 40px;
    height: 40px;
    background: none;
    border: none;
    font-size: 14px;
    color: #00B578;
    cursor: pointer;
  }
}

.date-filter {
  display: flex;
  background: #fff;
  padding: 12px 16px;
  gap: 12px;

  .date-option {
    padding: 6px 16px;
    border-radius: 16px;
    font-size: 13px;
    color: #666;
    background: #f5f5f5;
    cursor: pointer;

    &.active {
      background: #E8F8F0;
      color: #00B578;
    }
  }
}

.summary-card {
  display: flex;
  background: linear-gradient(135deg, #00B578 0%, #00C78A 100%);
  margin: 12px;
  border-radius: 12px;
  padding: 20px;
  color: #fff;

  .summary-item {
    flex: 1;
    text-align: center;

    .summary-value {
      display: block;
      font-size: 24px;
      font-weight: 600;
      margin-bottom: 4px;
    }

    .summary-label {
      font-size: 13px;
      opacity: 0.9;
    }
  }
}

.income-list {
  padding: 0 12px;

  .list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 4px;
    font-size: 13px;
    color: #666;

    .export-btn {
      color: #00B578;
      cursor: pointer;
    }
  }
}

.income-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .income-type {
      display: flex;
      align-items: center;
      gap: 8px;

      .type-icon {
        font-size: 20px;
      }

      .type-name {
        font-size: 14px;
        color: #333;
      }
    }

    .income-amount {
      font-size: 18px;
      font-weight: 600;
      color: #00B578;
    }
  }

  .card-body {
    padding: 12px 0;
    border-top: 1px solid #f0f0f0;
    border-bottom: 1px solid #f0f0f0;

    .info-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        font-size: 13px;
        color: #999;
      }

      .value {
        font-size: 13px;
        color: #666;
      }
    }
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12px;

    .status-tag {
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;

      &.已结算 {
        background: #E8F8F0;
        color: #00B578;
      }

      &.待结算 {
        background: #FFF7E6;
        color: #FAAD14;
      }
    }

    .view-detail {
      font-size: 13px;
      color: #00B578;
      cursor: pointer;
    }
  }
}

.detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: flex-end;
  z-index: 1000;

  .modal-content {
    background: #fff;
    border-radius: 20px 20px 0 0;
    width: 100%;
    max-height: 80vh;
    overflow-y: auto;

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      border-bottom: 1px solid #f0f0f0;
      font-size: 16px;
      font-weight: 500;

      .close-btn {
        font-size: 24px;
        color: #999;
        cursor: pointer;
      }
    }

    .modal-body {
      padding: 20px;

      .detail-amount {
        text-align: center;
        font-size: 32px;
        font-weight: 600;
        color: #00B578;
        margin-bottom: 8px;
      }

      .detail-status {
        text-align: center;
        font-size: 14px;
        margin-bottom: 24px;

        &.已结算 {
          color: #00B578;
        }
        &.待结算 {
          color: #FAAD14;
        }
      }

      .detail-list {
        margin-bottom: 24px;

        .detail-item {
          display: flex;
          justify-content: space-between;
          padding: 12px 0;
          border-bottom: 1px solid #f5f5f5;

          .label {
            font-size: 14px;
            color: #999;
          }

          .value {
            font-size: 14px;
            color: #333;
          }
        }
      }

      .amount-breakdown {
        background: #f8f8f8;
        border-radius: 12px;
        padding: 16px;

        .breakdown-title {
          font-size: 14px;
          font-weight: 500;
          color: #333;
          margin-bottom: 12px;
        }

        .breakdown-item {
          display: flex;
          justify-content: space-between;
          padding: 8px 0;
          font-size: 14px;
          color: #666;

          &.total {
            border-top: 1px solid #e0e0e0;
            margin-top: 8px;
            padding-top: 12px;
            font-weight: 600;
            color: #333;
          }
        }
      }
    }
  }
}
</style>
