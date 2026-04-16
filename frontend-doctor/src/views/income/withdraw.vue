<template>
  <div class="withdraw-page">
    <!-- 顶部导航 -->
    <header class="page-header">
      <button class="back-btn" @click="goBack">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M15 18l-6-6 6-6" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <h1 class="page-title">提现申请</h1>
      <div class="header-right"></div>
    </header>

    <!-- 可提现金额 -->
    <div class="balance-card">
      <div class="balance-label">可提现金额</div>
      <div class="balance-amount">{{ formatMoney(balance) }}</div>
    </div>

    <!-- 提现表单 -->
    <div class="withdraw-form">
      <div class="form-item">
        <label>提现金额</label>
        <div class="amount-input-wrapper">
          <span class="currency">¥</span>
          <input 
            v-model="form.amount" 
            type="number" 
            placeholder="请输入提现金额"
            class="amount-input"
          />
          <span class="all-btn" @click="form.amount = balance">全部</span>
        </div>
        <div class="form-tip">最低提现金额 ¥100</div>
      </div>

      <div class="form-item">
        <label>提现方式</label>
        <div class="method-options">
          <div 
            :class="['method-option', { active: form.method === 'bank' }]"
            @click="form.method = 'bank'"
          >
            <span class="method-icon">🏦</span>
            <span class="method-name">银行卡</span>
          </div>
          <div 
            :class="['method-option', { active: form.method === 'alipay' }]"
            @click="form.method = 'alipay'"
          >
            <span class="method-icon">💳</span>
            <span class="method-name">支付宝</span>
          </div>
        </div>
      </div>

      <!-- 银行卡选择 -->
      <div v-if="form.method === 'bank'" class="form-item">
        <label>选择银行卡</label>
        <div class="bank-list">
          <div 
            v-for="card in bankCards" 
            :key="card.bankId"
            :class="['bank-card', { active: form.bankId === card.bankId }]"
            @click="form.bankId = card.bankId"
          >
            <div class="bank-name">{{ card.bankName }}</div>
            <div class="card-no">{{ card.cardNoMask }}</div>
            <div v-if="card.isDefault" class="default-tag">默认</div>
          </div>
          <div class="add-bank" @click="addBankCard">
            <span class="add-icon">+</span>
            <span>添加银行卡</span>
          </div>
        </div>
      </div>

      <!-- 支付宝账号 -->
      <div v-else class="form-item">
        <label>支付宝账号</label>
        <input 
          v-model="form.alipayAccount" 
          type="text" 
          placeholder="请输入支付宝绑定的手机号或邮箱"
          class="form-input"
        />
      </div>
    </div>

    <!-- 提现规则 -->
    <div class="rules-section">
      <div class="rules-title">提现规则</div>
      <ul class="rules-list">
        <li>最低提现金额：¥100</li>
        <li>提现到账时间：1-3个工作日</li>
        <li>每月最多可提现5次</li>
        <li>节假日顺延到账</li>
      </ul>
    </div>

    <!-- 提交按钮 -->
    <div class="submit-section">
      <button 
        class="submit-btn" 
        :disabled="!canSubmit"
        @click="handleSubmit"
      >
        确认提现
      </button>
    </div>

    <!-- 确认弹窗 -->
    <div v-if="showConfirm" class="confirm-modal" @click="showConfirm = false">
      <div class="modal-content" @click.stop>
        <div class="modal-title">确认提现</div>
        <div class="modal-amount">{{ formatMoney(form.amount) }}</div>
        <div class="modal-info">
          <div class="info-row">
            <span>提现方式</span>
            <span>{{ form.method === 'bank' ? '银行卡' : '支付宝' }}</span>
          </div>
          <div class="info-row">
            <span>到账账户</span>
            <span>{{ getAccountDisplay() }}</span>
          </div>
        </div>
        <div class="modal-actions">
          <button class="cancel-btn" @click="showConfirm = false">取消</button>
          <button class="confirm-btn" @click="confirmSubmit">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const balance = ref(5000.00)
const showConfirm = ref(false)

const form = reactive({
  amount: '',
  method: 'bank',
  bankId: 'BANK001',
  alipayAccount: ''
})

const bankCards = ref([
  {
    bankId: 'BANK001',
    bankName: '中国工商银行',
    cardNo: '6222021234568888',
    cardNoMask: '622202********8888',
    cardType: '储蓄卡',
    isDefault: true
  },
  {
    bankId: 'BANK002',
    bankName: '中国建设银行',
    cardNo: '6227001234566666',
    cardNoMask: '622700********6666',
    cardType: '储蓄卡',
    isDefault: false
  }
])

const canSubmit = computed(() => {
  const amount = parseFloat(form.amount)
  if (!amount || amount < 100) return false
  if (amount > balance.value) return false
  if (form.method === 'bank' && !form.bankId) return false
  if (form.method === 'alipay' && !form.alipayAccount) return false
  return true
})

const formatMoney = (amount: number) => {
  return '¥' + amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const goBack = () => {
  router.back()
}

const getAccountDisplay = () => {
  if (form.method === 'bank') {
    const card = bankCards.value.find(c => c.bankId === form.bankId)
    return card ? card.cardNoMask : ''
  }
  return form.alipayAccount
}

const addBankCard = () => {
  alert('添加银行卡功能开发中...')
}

const handleSubmit = () => {
  showConfirm.value = true
}

const confirmSubmit = () => {
  alert('提现申请已提交！')
  showConfirm.value = false
  router.back()
}
</script>

<style scoped lang="scss">
.withdraw-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 100px;
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

  .header-right {
    width: 40px;
  }

  .title {
    font-size: 18px;
    font-weight: 500;
    color: #333;
    flex: 1;
    text-align: center;
    margin-right: 32px;
  }
}

.balance-card {
  background: linear-gradient(135deg, #00B578 0%, #00C78A 100%);
  margin: 12px;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  color: #fff;

  .balance-label {
    font-size: 14px;
    opacity: 0.9;
    margin-bottom: 8px;
  }

  .balance-amount {
    font-size: 32px;
    font-weight: 600;
  }
}

.withdraw-form {
  background: #fff;
  margin: 0 12px 12px;
  border-radius: 12px;
  padding: 20px;

  .form-item {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }

    label {
      display: block;
      font-size: 14px;
      color: #333;
      margin-bottom: 12px;
      font-weight: 500;
    }

    .amount-input-wrapper {
      display: flex;
      align-items: center;
      border: 1px solid #E8E8E8;
      border-radius: 8px;
      padding: 0 12px;
      height: 48px;

      .currency {
        font-size: 18px;
        color: #333;
        margin-right: 8px;
      }

      .amount-input {
        flex: 1;
        border: none;
        outline: none;
        font-size: 18px;
        color: #333;

        &::placeholder {
          color: #999;
          font-size: 14px;
        }
      }

      .all-btn {
        font-size: 14px;
        color: #00B578;
        cursor: pointer;
        padding: 4px 8px;
      }
    }

    .form-tip {
      font-size: 12px;
      color: #999;
      margin-top: 8px;
    }

    .method-options {
      display: flex;
      gap: 12px;

      .method-option {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 16px;
        border: 1px solid #E8E8E8;
        border-radius: 8px;
        cursor: pointer;

        &.active {
          border-color: #00B578;
          background: #E8F8F0;
        }

        .method-icon {
          font-size: 24px;
          margin-bottom: 8px;
        }

        .method-name {
          font-size: 13px;
          color: #666;
        }
      }
    }

    .form-input {
      width: 100%;
      height: 48px;
      padding: 0 12px;
      border: 1px solid #E8E8E8;
      border-radius: 8px;
      font-size: 14px;
      outline: none;

      &:focus {
        border-color: #00B578;
      }

      &::placeholder {
        color: #999;
      }
    }

    .bank-list {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .bank-card {
        position: relative;
        padding: 16px;
        border: 1px solid #E8E8E8;
        border-radius: 8px;
        cursor: pointer;

        &.active {
          border-color: #00B578;
          background: #E8F8F0;
        }

        .bank-name {
          font-size: 14px;
          color: #333;
          margin-bottom: 4px;
        }

        .card-no {
          font-size: 13px;
          color: #999;
        }

        .default-tag {
          position: absolute;
          top: 12px;
          right: 12px;
          font-size: 11px;
          color: #00B578;
          background: rgba(0, 181, 120, 0.1);
          padding: 2px 8px;
          border-radius: 4px;
        }
      }

      .add-bank {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        padding: 16px;
        border: 1px dashed #E8E8E8;
        border-radius: 8px;
        cursor: pointer;
        font-size: 14px;
        color: #666;

        .add-icon {
          font-size: 20px;
          color: #00B578;
        }
      }
    }
  }
}

.rules-section {
  background: #fff;
  margin: 0 12px;
  border-radius: 12px;
  padding: 16px;

  .rules-title {
    font-size: 14px;
    font-weight: 500;
    color: #333;
    margin-bottom: 12px;
  }

  .rules-list {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      font-size: 13px;
      color: #666;
      margin-bottom: 8px;
      padding-left: 16px;
      position: relative;

      &::before {
        content: '•';
        position: absolute;
        left: 0;
        color: #999;
      }

      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 12px 16px;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.05);

  .submit-btn {
    width: 100%;
    height: 48px;
    background: linear-gradient(135deg, #00B578 0%, #00C78A 100%);
    border: none;
    border-radius: 8px;
    color: #fff;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;

    &:disabled {
      background: #ccc;
      cursor: not-allowed;
    }
  }
}

.confirm-modal {
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
    padding: 24px;
    text-align: center;

    .modal-title {
      font-size: 18px;
      font-weight: 500;
      color: #333;
      margin-bottom: 16px;
    }

    .modal-amount {
      font-size: 32px;
      font-weight: 600;
      color: #00B578;
      margin-bottom: 20px;
    }

    .modal-info {
      background: #f8f8f8;
      border-radius: 8px;
      padding: 16px;
      margin-bottom: 20px;

      .info-row {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;
        font-size: 14px;

        &:last-child {
          margin-bottom: 0;
        }

        span:first-child {
          color: #999;
        }

        span:last-child {
          color: #333;
        }
      }
    }

    .modal-actions {
      display: flex;
      gap: 12px;

      button {
        flex: 1;
        height: 44px;
        border-radius: 8px;
        font-size: 15px;
        cursor: pointer;
        border: none;
      }

      .cancel-btn {
        background: #f5f5f5;
        color: #666;
      }

      .confirm-btn {
        background: linear-gradient(135deg, #00B578 0%, #00C78A 100%);
        color: #fff;
      }
    }
  }
}
</style>
