<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useOrderStore } from '@/stores/order'
import { getOrderDetail, payOrder, getPayStatus } from '@/api/modules/order'
import { useCountdown } from '@/composables/useCountdown'
import Loading from '@/components/Loading/index.vue'
import type { Order, PayType } from '@/types'

const router = useRouter()
const route = useRoute()
const orderStore = useOrderStore()

// 订单ID
const orderId = computed(() => route.query.id as string)

// 订单数据
const order = ref<Order | null>(null)
const loading = ref(false)

// 支付方式选择
const selectedPayType = ref<PayType>('wechat')
const payTypes = [
  { value: 'wechat' as PayType, label: '微信支付', icon: 'Wallet', color: '#07c160' },
  { value: 'alipay' as PayType, label: '支付宝', icon: 'Money', color: '#1677ff' },
  { value: 'balance' as PayType, label: '余额支付', icon: 'Coin', color: '#ff9500' }
]

// 倒计时 - 30分钟
const { minutes, seconds, isExpired, start, stop } = useCountdown(30 * 60)

// 页面初始化
onMounted(() => {
  if (orderId.value) {
    loadOrderDetail()
    start()
  } else {
    ElMessage.error('订单ID不能为空')
    router.back()
  }
})

onUnmounted(() => {
  stop()
})

// 监听倒计时结束
const handleExpired = () => {
  if (isExpired.value && order.value?.status === 'pending') {
    ElMessage.warning('支付超时，订单已关闭')
    setTimeout(() => {
      router.push('/order/list')
    }, 1500)
  }
}

// 加载订单详情
const loadOrderDetail = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(orderId.value)
    if (res) {
      order.value = res

      // 检查订单状态
      if (res.status !== 'pending') {
        ElMessage.info('该订单无需支付')
        router.replace(`/order/${res.id}`)
        return
      }

      // 设置倒计时
      if (res.expireTime) {
        const expireTime = new Date(res.expireTime).getTime()
        const now = Date.now()
        const remaining = Math.max(0, Math.floor((expireTime - now) / 1000))
        // 如果倒计时模块支持设置时间，这里可以设置
      }
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

// 支付中状态
const isPaying = ref(false)

// 确认支付
const handleConfirmPay = async () => {
  if (!order.value) return
  if (isExpired.value) {
    ElMessage.warning('支付已超时，请重新下单')
    return
  }

  isPaying.value = true
  try {
    // 模拟支付过程
    await new Promise(resolve => setTimeout(resolve, 1500))

    // 实际支付接口
    // const res = await payOrder({
    //   orderId: order.value.id,
    //   payType: selectedPayType.value,
    //   returnUrl: window.location.origin + '/order/pay-success'
    // })

    ElMessage.success('支付成功')
    router.replace({
      path: '/order/pay-result',
      query: { id: order.value.id, status: 'success' }
    })
  } catch (error) {
    ElMessage.error('支付失败，请重试')
  } finally {
    isPaying.value = false
  }
}

// 取消支付
const handleCancel = () => {
  router.back()
}

// 格式化倒计时显示
const formatCountdown = computed(() => {
  const m = minutes.value.toString().padStart(2, '0')
  const s = seconds.value.toString().padStart(2, '0')
  return `${m}分${s}秒`
})

// 格式化金额
const formatAmount = (amount: number | undefined) => {
  if (amount === undefined) return '0.00'
  return amount.toFixed(2)
}
</script>

<template>
  <div class="pay-page">
    <div v-if="loading" class="loading-wrapper">
      <Loading />
    </div>

    <div v-else-if="order" class="pay-content">
      <!-- 头部 -->
      <div class="header">
        <div class="back-btn" @click="handleCancel">
          <el-icon><ArrowLeft /></el-icon>
        </div>
        <h1 class="title">确认支付</h1>
        <div class="placeholder" />
      </div>

      <!-- 倒计时提醒 -->
      <div v-if="!isExpired" class="countdown-tips">
        <el-icon><Clock /></el-icon>
        <span>请在 {{ formatCountdown }} 内完成支付</span>
      </div>
      <div v-else class="countdown-tips expired">
        <el-icon><Warning /></el-icon>
        <span>支付已超时</span>
      </div>

      <!-- 支付金额 -->
      <div class="amount-section">
        <div class="amount-label">支付金额</div>
        <div class="amount-value">
          <span class="currency">¥</span>
          <span class="number">{{ formatAmount(order.payableAmount) }}</span>
        </div>
        <div class="amount-detail">
          <span>商品总额 ¥{{ formatAmount(order.drugAmount) }}</span>
          <span v-if="order.deliveryFee > 0"> + 运费 ¥{{ formatAmount(order.deliveryFee) }}</span>
          <span v-if="order.discountAmount > 0"> - 优惠 ¥{{ formatAmount(order.discountAmount) }}</span>
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
            :class="{ active: selectedPayType === type.value, disabled: isExpired || isPaying }"
            @click="!isExpired && !isPaying && (selectedPayType = type.value)"
          >
            <div class="paytype-icon" :style="{ background: type.color }">
              <el-icon :size="20">
                <component :is="type.icon" />
              </el-icon>
            </div>
            <div class="paytype-info">
              <span class="paytype-name">{{ type.label }}</span>
              <span v-if="type.value === 'balance'" class="paytype-desc">余额 ¥0.00</span>
              <span v-else-if="type.value === 'wechat'" class="paytype-desc">推荐使用</span>
            </div>
            <div class="check-icon">
              <el-icon v-if="selectedPayType === type.value" :color="'#00b578'">
                <CircleCheckFilled />
              </el-icon>
              <div v-else class="check-circle" />
            </div>
          </div>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="order-info">
        <div class="info-row">
          <span class="label">订单号</span>
          <span class="value">{{ order.orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="label">收货人</span>
          <span class="value">{{ order.receiverName }} {{ order.receiverPhone }}</span>
        </div>
        <div class="info-row">
          <span class="label">收货地址</span>
          <span class="value address">{{ order.receiverAddress }}</span>
        </div>
      </div>

      <!-- 底部支付按钮 -->
      <div class="pay-footer">
        <div class="total-info">
          <span class="total-label">实付金额</span>
          <span class="total-value">¥{{ formatAmount(order.payableAmount) }}</span>
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
    margin: 0;
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
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: $radius-md;
        color: $text-white;
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
    padding: $spacing-sm 0;
    font-size: $font-sm;
    border-bottom: 1px dashed $border-light;
