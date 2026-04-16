<template>
  <div class="income-page">
    <!-- 顶部导航 -->
    <header class="page-header">
      <button class="back-btn" @click="goBack">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M15 18l-6-6 6-6" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <h1 class="page-title">我的收入</h1>
      <button class="help-btn" @click="showHelp = true">?</button>
    </header>

    <!-- 收入总览卡片 -->
    <div class="income-overview">
      <div class="main-stats">
        <div class="stat-item">
          <div class="stat-value">{{ formatMoney(overview.balance) }}</div>
          <div class="stat-label">账户余额</div>
          <button class="withdraw-btn" @click="goWithdraw">提现</button>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ formatMoney(overview.monthIncome) }}</div>
          <div class="stat-label">本月收入</div>
          <div class="trend" :class="overview.monthIncomeRatio >= 0 ? 'up' : 'down'">
            {{ overview.monthIncomeRatio >= 0 ? '↑' : '↓' }} {{ Math.abs(overview.monthIncomeRatio) }}%
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ formatMoney(overview.totalIncome) }}</div>
          <div class="stat-label">累计总收入</div>
        </div>
      </div>
      <div class="sub-stats">
        <div class="sub-item">
          <span class="sub-value">{{ formatMoney(overview.todayIncome) }}</span>
          <span class="sub-label">今日收入</span>
        </div>
        <div class="sub-item">
          <span class="sub-value">{{ formatMoney(overview.weekIncome) }}</span>
          <span class="sub-label">本周收入</span>
        </div>
        <div class="sub-item">
          <span class="sub-value">{{ formatMoney(overview.pendingSettlement) }}</span>
          <span class="sub-label">待结算</span>
        </div>
        <div class="sub-item">
          <span class="sub-value">{{ formatMoney(overview.totalWithdraw) }}</span>
          <span class="sub-label">累计提现</span>
        </div>
      </div>
    </div>

    <!-- 收入趋势 -->
    <div class="trend-section">
      <div class="section-header">
        <span class="section-title">收入趋势</span>
        <div class="time-tabs">
          <span 
            v-for="tab in timeTabs" 
            :key="tab.value"
            :class="['tab', { active: currentTab === tab.value }]"
            @click="currentTab = tab.value"
          >
            {{ tab.label }}
          </span>
        </div>
      </div>
      <div class="chart-placeholder">
        <div class="chart-mock">
          <div v-for="(bar, index) in mockChartData" :key="index" class="bar-item">
            <div class="bar" :style="{ height: bar.height + '%' }"></div>
            <span class="bar-label">{{ bar.label }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Tab切换 -->
    <div class="tab-bar">
      <div 
        :class="['tab-item', { active: activeTab === 'income' }]"
        @click="activeTab = 'income'"
      >
        收入明细
      </div>
      <div 
        :class="['tab-item', { active: activeTab === 'withdraw' }]"
        @click="activeTab = 'withdraw'"
      >
        提现记录
      </div>
    </div>

    <!-- 收入明细列表 -->
    <div v-if="activeTab === 'income'" class="income-list">
      <div v-for="item in incomeList" :key="item.id" class="income-item">
        <div class="item-icon">{{ item.type === '图文问诊' ? '💬' : '📝' }}</div>
        <div class="item-info">
          <div class="item-title">{{ item.type }}</div>
          <div class="item-patient">{{ item.patientName }} · {{ item.time }}</div>
        </div>
        <div class="item-amount">+{{ formatMoney(item.amount) }}</div>
      </div>
    </div>

    <!-- 提现记录列表 -->
    <div v-else class="withdraw-list">
      <div v-for="item in withdrawList" :key="item.withdrawId" class="withdraw-item">
        <div class="item-icon">{{ item.method === 'bank' ? '🏦' : '💳' }}</div>
        <div class="item-info">
          <div class="item-title">提现到{{ item.methodName }}</div>
          <div class="item-time">{{ item.applyTime }}</div>
        </div>
        <div class="item-right">
          <div class="item-amount">-{{ formatMoney(item.amount) }}</div>
          <div :class="['item-status', item.status]">{{ item.statusText }}</div>
        </div>
      </div>
    </div>

    <!-- 帮助弹窗 -->
    <div v-if="showHelp" class="help-modal" @click="showHelp = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <span>收入说明</span>
          <span class="close-btn" @click="showHelp = false">×</span>
        </div>
        <div class="modal-body">
          <div class="help-section">
            <h4>收入构成</h4>
            <p>收入来源于图文问诊、复诊开方等服务，平台收取一定比例分成。</p>
          </div>
          <div class="help-section">
            <h4>结算周期</h4>
            <p>问诊完成后7天内结算，结算后金额进入可提现余额。</p>
          </div>
          <div class="help-section">
            <h4>提现规则</h4>
            <p>最低提现金额100元，1-3个工作日到账。</p>
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
const activeTab = ref('income')
const currentTab = ref('week')
const showHelp = ref(false)

const timeTabs = [
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '本年', value: 'year' }
]

const overview = reactive({
  balance: 5000.00,
  monthIncome: 8500.00,
  monthIncomeRatio: 15.2,
  totalIncome: 128000.00,
  todayIncome: 1200.00,
  weekIncome: 5600.00,
  pendingSettlement: 3000.00,
  totalWithdraw: 85000.00
})

const mockChartData = [
  { label: '周一', height: 40 },
  { label: '周二', height: 65 },
  { label: '周三', height: 80 },
  { label: '周四', height: 55 },
  { label: '周五', height: 90 },
  { label: '周六', height: 70 },
  { label: '周日', height: 45 }
]

const incomeList = ref([
  { id: 'INC001', type: '图文问诊', patientName: '李*', amount: 80.00, time: '04-07 14:30' },
  { id: 'INC002', type: '复诊开方', patientName: '王*', amount: 60.00, time: '04-07 10:15' },
  { id: 'INC003', type: '图文问诊', patientName: '张*', amount: 80.00, time: '04-06 16:45' },
  { id: 'INC004', type: '图文问诊', patientName: '刘*', amount: 80.00, time: '04-06 09:20' },
  { id: 'INC005', type: '复诊开方', patientName: '陈*', amount: 60.00, time: '04-05 14:00' }
])

const withdrawList = ref([
  { withdrawId: 'WIT001', amount: 5000.00, method: 'bank', methodName: '工商银行', status: 'success', statusText: '已到账', applyTime: '2024-04-01 15:30' },
  { withdrawId: 'WIT002', amount: 3000.00, method: 'bank', methodName: '建设银行', status: 'processing', statusText: '处理中', applyTime: '2024-04-05 10:00' }
])

const formatMoney = (amount: number) => {
  return '¥' + amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const goBack = () => {
  router.back()
}

const goWithdraw = () => {
  router.push('/income/withdraw')
}
</script>

<style scoped lang="scss">
.income-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #00B578 0%, #00C78A 100%);
  color: #fff;

  .back-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: none;
    border: none;
    color: #fff;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s;

    &:active {
      background: rgba(255,255,255,0.2);
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
    color: #fff;
    margin: 0;
  }

  .help-btn {
    width: 40px;
    height: 40px;
    background: none;
    border: 1px solid rgba(255,255,255,0.5);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #fff;
    cursor: pointer;
  }
}

.income-overview {
  background: linear-gradient(135deg, #00B578 0%, #00C78A 100%);
  padding: 20px 16px;
  color: #fff;

  .main-stats {
    display: flex;
    justify-content: space-around;
    margin-bottom: 20px;

    .stat-item {
      text-align: center;
      flex: 1;

      .stat-value {
        font-size: 20px;
        font-weight: 600;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 12px;
        opacity: 0.9;
        margin-bottom: 8px;
      }

      .withdraw-btn {
        background: rgba(255,255,255,0.2);
        border: 1px solid rgba(255,255,255,0.5);
        color: #fff;
        padding: 4px 16px;
        border-radius: 12px;
        font-size: 12px;
        cursor: pointer;
      }

      .trend {
        font-size: 12px;
        
        &.up {
          color: #90EE90;
        }
        &.down {
          color: #FFB6C1;
        }
      }
    }
  }

  .sub-stats {
    display: flex;
    justify-content: space-around;
    background: rgba(255,255,255,0.1);
    border-radius: 8px;
    padding: 12px 0;

    .sub-item {
      text-align: center;

      .sub-value {
        display: block;
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 2px;
      }

      .sub-label {
        font-size: 11px;
        opacity: 0.8;
      }
    }
  }
}

.trend-section {
  background: #fff;
  margin: 12px;
  border-radius: 12px;
  padding: 16px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .section-title {
      font-size: 16px;
      font-weight: 500;
      color: #333;
    }

    .time-tabs {
      display: flex;
      gap: 8px;

      .tab {
        font-size: 12px;
        color: #666;
        padding: 4px 12px;
        border-radius: 12px;
        cursor: pointer;

        &.active {
          background: #E8F8F0;
          color: #00B578;
        }
      }
    }
  }

  .chart-mock {
    display: flex;
    justify-content: space-around;
    align-items: flex-end;
    height: 120px;
    padding: 0 8px;

    .bar-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      flex: 1;

      .bar {
        width: 24px;
        background: linear-gradient(180deg, #00C78A 0%, #00B578 100%);
        border-radius: 4px 4px 0 0;
        transition: height 0.3s;
      }

      .bar-label {
        font-size: 11px;
        color: #999;
        margin-top: 8px;
      }
    }
  }
}

.tab-bar {
  display: flex;
  background: #fff;
  margin: 0 12px 12px;
  border-radius: 12px;
  overflow: hidden;

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 14px 0;
    font-size: 14px;
    color: #666;
    cursor: pointer;
    position: relative;

    &.active {
      color: #00B578;
      font-weight: 500;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 40px;
        height: 3px;
        background: #00B578;
        border-radius: 2px;
      }
    }
  }
}

.income-list, .withdraw-list {
  background: #fff;
  margin: 0 12px;
  border-radius: 12px;
  padding: 0 16px;
}

.income-item, .withdraw-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }

  .item-icon {
    font-size: 24px;
    margin-right: 12px;
  }

  .item-info {
    flex: 1;

    .item-title {
      font-size: 14px;
      color: #333;
      margin-bottom: 4px;
    }

    .item-patient, .item-time {
      font-size: 12px;
      color: #999;
    }
  }

  .item-right {
    text-align: right;
  }

  .item-amount {
    font-size: 16px;
    font-weight: 500;
    color: #333;
  }

  .item-status {
    font-size: 11px;
    margin-top: 4px;

    &.success {
      color: #00B578;
    }
    &.processing {
      color: #1890FF;
    }
    &.rejected {
      color: #FF4D4F;
    }
  }
}

.help-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;

  .modal-content {
    background: #fff;
    border-radius: 16px;
    width: 80%;
    max-width: 320px;

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
      padding: 16px;

      .help-section {
        margin-bottom: 16px;

        h4 {
          font-size: 14px;
          color: #333;
          margin-bottom: 8px;
        }

        p {
          font-size: 13px;
          color: #666;
          line-height: 1.6;
        }
      }
    }
  }
}
</style>
