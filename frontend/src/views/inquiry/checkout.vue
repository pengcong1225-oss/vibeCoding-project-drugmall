<template>
  <div class="checkout-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <span class="title">收银台</span>
      <div class="placeholder"></div>
    </div>

    <!-- 订单金额 -->
    <div class="amount-section">
      <div class="amount-label">订单金额</div>
      <div class="amount-value">¥{{ amount }}</div>
    </div>

    <!-- 支付方式选择 -->
    <div class="payment-section">
      <div class="section-title">选择支付方式</div>
      <div class="payment-methods">
        <div
          v-for="method in paymentMethods"
          :key="method.value"
          :class="['payment-method', { active: selectedMethod === method.value }]"
          @click="selectedMethod = method.value"
        >
          <div class="method-icon" :class="method.value">
            <el-icon v-if="method.value === 'wechat'"><ChatDotRound /></el-icon>
            <el-icon v-else-if="method.value === 'alipay'"><Wallet /></el-icon>
            <el-icon v-else><CreditCard /></el-icon>
          </div>
          <div class="method-info">
            <span class="method-name">{{ method.label }}</span>
            <span v-if="method.desc" class="method-desc">{{ method.desc }}</span>
          </div>
          <div class="method-check">
            <div :class="['check-circle', { checked: selectedMethod === method.value }]">
              <el-icon v-if="selectedMethod === method.value"><Check /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付说明 -->
    <div class="payment-tips">
      <div class="tips-item">
        <el-icon><InfoFilled /></el-icon>
        <span>支付成功后，医生将在24小时内接诊</span>
      </div>
      <div class="tips-item">
        <el-icon><InfoFilled /></el-icon>
        <span>24小时未接诊将自动退款</span>
      </div>
    </div>

    <!-- 底部确认支付按钮 -->
    <div class="checkout-footer">
      <button class="confirm-pay-btn" @click="handlePay" :disabled="paying">
        <span v-if="paying">
          <el-icon class="loading-icon"><Loading /></el-icon>
          支付中...
        </span>
        <span v-else>确认支付 ¥{{ amount }}</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  ChatDotRound,
  Wallet,
  CreditCard,
  Check,
  Loading,
  InfoFilled
} from '@element-plus/icons-vue'
import { payConsultation } from '@/api/modules/inquiry'
import { ROUTES, getInquiryWaitingRoute } from '@/constants/routes'
import { businessApi } from '@/api/modules/business'

const route = useRoute()
const router = useRouter()

// 支付金额
const amount = ref('19.9')
const consultationId = ref('')

// 选中的支付方式
const selectedMethod = ref('wechat')

// 支付方式列表
const paymentMethods = ref<{ value: string; label: string; desc: string }[]>([])

// 支付中状态
const paying = ref(false)

// 加载订单信息
const loadOrderInfo = () => {
  const id = route.params.consultationId as string
  if (!id) {
    ElMessage.error('订单信息不存在')
    return
  }
  consultationId.value = id
  amount.value = route.query.amount as string || '19.9'
}

// 处理支付
const handlePay = async () => {
  if (!consultationId.value) {
    ElMessage.error('订单信息不存在')
    return
  }

  if (!selectedMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }

  paying.value = true

  try {
    // 实际项目中调用支付API
    // const res = await payConsultation(consultationId.value, {
    //   paymentMethod: selectedMethod.value
    // })

    await new Promise(resolve => setTimeout(resolve, 2000))

    ElMessage.success('支付成功')

    router.push({
      path: getInquiryWaitingRoute(consultationId.value),
      query: {
        doctorId: route.query.doctorId,
        doctorName: route.query.doctorName
      }
    })
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败，请重试')
  } finally {
    paying.value = false
  }
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(async () => {
  loadOrderInfo()
  await loadPaymentMethods()
})

async function loadPaymentMethods() {
  try {
    const res = await businessApi.getPaymentMethods()
    paymentMethods.value = res.data
      .filter(p => ['wechat', 'alipay', 'bankcard'].includes(p.code))
      .map(p => ({
        value: p.code,
        label: p.name,
        desc: p.description || ''
      }))
  } catch (error) {
    console.error('加载支付方式失败:', error)
  }
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

$primary-green: #00C9A7;
$primary-green-light: #00b894;
$wechat-green: #07C160;
$alipay-blue: #1677FF;
$pay-yellow: #FFD700;

.checkout-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: 100px;
}

// 顶部导航
.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  padding-top: calc($safe-area-top + 12px);
  background: white;
  border-bottom: 1px solid $border-light;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;

    &:active {
      background: $bg-primary;
    }
  }

  .title {
    font-size: 17px;
    font-weight: 600;
    color: $text-primary;
  }

  .placeholder {
    width: 36px;
  }
}

// 金额区域
.amount-section {
  background: white;
  padding: 30px 16px;
  text-align: center;
  border-bottom: 1px solid $border-light;

  .amount-label {
    font-size: 14px;
    color: $text-secondary;
    margin-bottom: 8px;
  }

  .amount-value {
    font-size: 36px;
    font-weight: 700;
    color: $text-primary;
  }
}

// 支付方式区域
.payment-section {
  background: white;
  padding: 16px;
  margin-top: 12px;

  .section-title {
    font-size: 15px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 16px;
  }

  .payment-methods {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .payment-method {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      background: $bg-primary;
      border-radius: 12px;
      cursor: pointer;
      border: 2px solid transparent;
      transition: all 0.2s;

      &:active {
        transform: scale(0.99);
      }

      &.active {
        border-color: $primary-green;
        background: rgba($primary-green, 0.02);
      }

      .method-icon {
        width: 44px;
        height: 44px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;

        &.wechat {
          background: rgba($wechat-green, 0.1);
          color: $wechat-green;
        }

        &.alipay {
          background: rgba($alipay-blue, 0.1);
          color: $alipay-blue;
        }

        &.bankcard {
          background: rgba($warning, 0.1);
          color: $warning;
        }

        .el-icon {
          font-size: 24px;
        }
      }

      .method-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 4px;

        .method-name {
          font-size: 15px;
          font-weight: 500;
          color: $text-primary;
        }

        .method-desc {
          font-size: 12px;
          color: $text-tertiary;
        }
      }

      .method-check {
        .check-circle {
          width: 22px;
          height: 22px;
          border-radius: 50%;
          border: 2px solid #ddd;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: all 0.2s;

          &.checked {
            background: $primary-green;
            border-color: $primary-green;
            color: white;
          }

          .el-icon {
            font-size: 14px;
          }
        }
      }
    }
  }
}

// 支付提示
.payment-tips {
  padding: 16px;

  .tips-item {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    font-size: 13px;
    color: $text-tertiary;

    .el-icon {
      color: $primary-green;
      font-size: 14px;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }
}

// 底部支付按钮
.checkout-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  padding-bottom: calc(12px + $safe-area-bottom);
  background: white;
  border-top: 1px solid $border-light;

  .confirm-pay-btn {
    width: 100%;
    padding: 14px;
    background: $primary-green;
    color: white;
    border: none;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;

    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }

    &:active:not(:disabled) {
      opacity: 0.9;
      transform: scale(0.98);
    }

    .loading-icon {
      animation: rotate 1s linear infinite;
      margin-right: 4px;
    }
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
