<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useOrderStore } from '@/stores/order'
import { useCountdown } from '@/composables'
import { getOrderDetail, payOrder } from '@/api/modules/order'
import { formatPrice } from '@/utils'
import { OrderStatus } from '@/constants'
import { getOrderDetailRoute } from '@/constants/routes'
import { IMAGES } from '@/constants/images'
import { businessApi } from '@/api/modules/business'
import type { PayType } from '@/types'

const router = useRouter()
const route = useRoute()
const orderStore = useOrderStore()

// 订单ID
const orderId = computed(() => route.query.id as string)

// 订单数据
const order = ref<any>(null)
const loading = ref(false)
const isPaying = ref(false)

// 选中的支付方式
const selectedPayType = ref<PayType>('wechat')

const payTypes = ref<{ value: PayType; label: string; icon: string }[]>([])

// 倒计时 - 30分钟
const { minutes, seconds, isExpired, start, stop } = useCountdown(30 * 60)

// 页面初始化
onMounted(async () => {
  if (orderId.value) {
    loadOrderDetail()
    start()
  } else {
    ElMessage.error('订单ID不能为空')
    router.back()
  }
  await loadPaymentMethods()
})

async function loadPaymentMethods() {
  try {
    const res = await businessApi.getPaymentMethods()
    payTypes.value = res.data
      .filter(p => ['wechat', 'alipay', 'balance'].includes(p.code))
      .map(p => ({
        value: p.code as PayType,
        label: p.name,
        icon: p.code === 'wechat' ? IMAGES.PAY_WECHAT : p.code === 'alipay' ? IMAGES.PAY_ALIPAY : IMAGES.PAY_BALANCE
      }))
  } catch (error) {
    console.error('加载支付方式失败:', error)
  }
}

onUnmounted(() => {
  stop()
})

// 加载订单详情
const loadOrderDetail = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(orderId.value)
    if (res) {
      order.value = res
      orderStore.setCurrentOrder(res)

      // 检查订单状态
      if (res.status !== OrderStatus.PENDING) {
        ElMessage.warning('该订单已支付或已取消')
        router.replace(getOrderDetailRoute(res.id))
        return
      }

      // 检查是否已过期
      if (res.expireTime) {
        const expireTime = new Date(res.expireTime).getTime()
        const now = Date.now()
        if (expireTime <= now) {
          // 已过期
          stop()
        } else {
          // 重新开始倒计时
          const remainingSeconds = Math.floor((expireTime - now) / 1000)
          stop()
          start(remainingSeconds)
        }
      }
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 确认支付
const handleConfirmPay = async () => {
  if (isExpired.value) {
    ElMessage.error('支付已超时，请重新下单')
    return
  }

  if (!order.value) return

  // 检查余额
  if (selectedPayType.value === 'balance') {
    // 模拟检查余额
    const balance = 0
    if (balance < order.value.payableAmount) {
      ElMessage.warning('余额不足，请选择其他支付方式')
      return
    }
  }

  isPaying.value = true
  try {
    const res = await payOrder({
      orderId: order.value.id,
      payType: selectedPayType.value
    })

    if (res) {
      stop()
      ElMessage.success('支付成功')
      // 跳转到支付结果页
      router.replace({
        path: `/order/${order.value.id}`,
        query: {
          paySuccess: 'true'
        }
      })
    }
  } catch (error) {
    ElMessage.error('支付失败，请重试')
  } finally {
    isPaying.value = false
  }
}

// 取消支付
const handleCancel = () => {
  ElMessageBox.confirm('确定要取消支付吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.back()
  }).catch(() => {})
}

// 选择支付方式
const selectPayType = (type: PayType) => {
  selectedPayType.value = type
}
</script>

<template>
  <div class="pay-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 支付内容 -->
    <div v-else-if="order" class="pay-content">
      <!-- 头部 -->
      <div class="header">
        <div class="back-btn" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
        </div>
        <h1 class="title">支付订单</h1>
        <div class="placeholder" />
      </div>

      <!-- 倒计时提示 -->
      <div class="countdown-tips" :class="{ expired: isExpired }">
        <el-icon><Clock /></el-icon>
        <span v-if="isExpired">支付已超时，请重新下单</span>
        <span v-else>
          支付剩余时间
          <strong>{{ String(minutes).padStart(2, '0') }}:{{ String(seconds).padStart(2, '0') }}</strong>
        </span>
      </div>

      <!-- 支付金额 -->
      <div class="amount-section">
        <div class="amount-label">支付金额</div>
        <div class="amount-value">
          <span class="currency">¥</span>
          <span class="number">{{ formatPrice(order.payableAmount) }}</span>
        </div>
        <div class="amount-detail">
          商品总额 ¥{{ formatPrice(order.drugAmount) }} + 运费 ¥{{ formatPrice(order.deliveryFee) }}
          <span v-if="order.discountAmount > 0"> - 优惠 ¥{{ formatPrice(order.discountAmount) }}</span>
        </div>
      </div>

      <!-- 支付方式 -->
      <div class="paytype-section">
        <div class="section-title">选择支付方式</div>
        <div class="paytype-list">
          <div
            v-for="type in payTypes"
            :key="type.value"
            class="paytype-item"
            :class="{ active: selectedPayType === type.value, disabled: isExpired }"
            @click="!isExpired && selectPayType(type.value)"
          >
            <img :src="type.icon" :alt="type.label" class="paytype-icon" />
            <div class="paytype-info">
              <div class="paytype-name">{{ type.label }}</div>
              <div class="paytype-desc">
                <span v-if="type.value === 'balance'" class="balance-text">余额 ¥0.00</span>
                <span v-else-if="type.value === 'wechat'" class="recommend-text">推荐使用</span>
              </div>
            </div>
            <div class="check-icon">
              <el-icon v-if="selectedPayType === type.value" :size="20" color="#00b578">
                <CircleCheckFilled />
              </el-icon>
              <div v-else class="check-circle" />
            </div>
          </div>
        </div>
      </div>

      <!-- 底部支付按钮 -->
      <div class="pay-footer">
        <div class="total-info">
          <span class="total-label">实付金额</span>
          <span class="total-value">¥{{ formatPrice(order.payableAmount) }}</span>
        </div>
        <div class="pay-actions">
          <el-button size="large" @click="handleCancel" :disabled="isPaying">取消</el-button>
          <el-button
            type="primary"
            size="large"
            :loading="isPaying"
            :disabled="isExpired"
            @click="handleConfirmPay"
          >
            {{ isExpired ? '支付已超时' : isPaying ? '支付中...' : '确认支付' }}
          </el-button>
        </div>
      </div>

      <!-- 安全区域 -->
      <div class="safe-area-bottom" />
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.pay-page {
  min-height: 100vh;
  background: $bg-primary;
}

.loading-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
}

// 头部
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-sm $spacing-md;
  padding-top: calc($safe-area-top + $spacing-sm);
  background: $bg-white;

  .back-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $text-primary;
    font-size: 20px;
    cursor: pointer;
    border-radius: 50%;
    transition: background 0.2s;

    &:hover {
      background: $bg-gray;
    }
  }

  .title {
    font-size: $font-lg;
    font-weight: 600;
    color: $text-primary;
  }

  .placeholder {
    width: 36px;
  }
}

// 倒计时提示
.countdown-tips {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-xs;
  padding: $spacing-sm;
  background: rgba($warning, 0.1);
  color: $warning;
  font-size: $font-sm;

  &.expired {
    background: rgba($error, 0.1);
    color: $error;
  }

  .el-icon {
    font-size: 16px;
  }

  strong {
    font-size: $font-lg;
    font-weight: 600;
  }
}

// 支付金额
.amount-section {
  background: $bg-white;
  padding: $spacing-xl $spacing-md;
  text-align: center;
  border-bottom: 1px solid $border-light;

  .amount-label {
    font-size: $font-sm;
    color: $text-secondary;
    margin-bottom: $spacing-sm;
  }

  .amount-value {
    margin-bottom: $spacing-sm;

    .currency {
      font-size: $font-lg;
      color: $error;
      margin-right: 2px;
    }

    .number {
      font-size: 48px;
      font-weight: bold;
      color: $error;
    }
  }

  .amount-detail {
    font-size: $font-sm;
    color: $text-tertiary;
  }
}

// 支付方式
.paytype-section {
  background: $bg-white;
  margin-top: $spacing-md;
  padding: $spacing-md;

  .section-title {
    font-size: $font-md;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }

  .paytype-list {
    .paytype-item {
      display: flex;
      align-items: center;
      gap: $spacing-md;
      padding: $spacing-md;
      border: 1px solid $border-light;
      border-radius: $radius-lg;
      margin-bottom: $spacing-md;
      cursor: pointer;
      transition: all 0.2s;

      &:last-child {
        margin-bottom: 0;
      }

      &:hover:not(.disabled) {
        border-color: $primary;
      }

      &.active {
        border-color: $primary;
        background: rgba($primary, 0.02);
      }

      &.disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }

      .paytype-icon {
        width: 40px;
        height: 40px;
        border-radius: $radius-md;
        object-fit: contain;
      }

      .paytype-info {
        flex: 1;
        display: flex;
        flex-direction: column;

        .paytype-name {
          font-size: $font-md;
          font-weight: 500;
          color: $text-primary;
          margin-bottom: 2px;
        }

        .paytype-desc {
          font-size: $font-sm;
          color: $text-tertiary;

          .balance-text {
            color: $text-secondary;
          }

          .recommend-text {
            color: $primary;
          }
        }
      }

      .check-icon {
        .check-circle {
          width: 20px;
          height: 20px;
          border: 2px solid $border-color;
          border-radius: 50%;
        }
      }
    }
  }
}

// 订单信息
.order-info {
  background: $bg-white;
  margin-top: $spacing-md;
  padding: $spacing-md;

  .info-row {
    display: flex;
    justify-content: space-between;
    padding: $spacing-sm 0;
    font-size: $font-sm;
    border-bottom: 1px dashed $border-light;

    &:last-child {
      border-bottom: none;
    }

    .label {
      color: $text-secondary;
    }

    .value {
      color: $text-primary;
      font-weight: 500;
    }
  }
}

// 底部支付按钮
.pay-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: $spacing-md;
  padding: $spacing-md;
  padding-bottom: calc($spacing-md + $safe-area-bottom);
  background: $bg-white;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  z-index: 100;

  .total-info {
    display: flex;
    flex-direction: column;

    .total-label {
      font-size: $font-sm;
      color: $text-secondary;
      margin-bottom: 2px;
    }

    .total-value {
      font-size: $font-xl;
      font-weight: bold;
      color: $error;
    }
  }

  .pay-actions {
    display: flex;
    gap: $spacing-sm;

    .el-button {
      min-width: 120px;
    }
  }
}

// 底部安全区域
.safe-area-bottom {
  height: calc(80px + $safe-area-bottom);
}
</style>
