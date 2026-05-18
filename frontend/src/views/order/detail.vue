<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useOrderStore } from '@/stores/order'
import { getOrderDetail, cancelOrder, confirmReceipt, deleteOrder, getLogisticsInfo } from '@/api/modules/order'
import { formatPrice, formatDateTime } from '@/utils'
import { OrderStatus, SHIPPING_ORDER_STATUSES, CANCELLED_ORDER_STATUSES, ORDER_PROGRESS_STEPS } from '@/constants'
import { ROUTES, getDrugDetailRoute } from '@/constants/routes'
import type { Order, LogisticsInfo } from '@/types'

const router = useRouter()
const route = useRoute()
const orderStore = useOrderStore()

// 订单ID
const orderId = computed(() => route.params.id as string)

// 订单数据
const order = ref<Order | null>(null)
const loading = ref(false)

// 物流信息
const logistics = ref<{
  company: string
  no: string
  list: LogisticsInfo[]
} | null>(null)

// 页面初始化
onMounted(() => {
  if (orderId.value) {
    loadOrderDetail()
  } else {
    ElMessage.error('订单ID不能为空')
    router.back()
  }
})

// 加载订单详情
const loadOrderDetail = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(orderId.value)
    if (res) {
      order.value = res
      orderStore.setCurrentOrder(res)

      // 如果订单已发货，加载物流信息
      if (res && SHIPPING_ORDER_STATUSES.includes(res.status as OrderStatus)) {
        loadLogistics()
      }
    }
  } catch (error) {
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

// 加载物流信息
const loadLogistics = async () => {
  try {
    const res = await getLogisticsInfo(orderId.value)
    if (res) {
      logistics.value = res
    }
  } catch (error) {
    // 物流信息非必须，失败不提示
  }
}

// 状态配置
const statusConfig = computed(() => {
  if (!order.value) return null
  const configs: Record<string, { text: string; color: string; icon: string; desc: string }> = {
    pending: { text: '待支付', color: '#ff9500', icon: 'Wallet', desc: '订单已创建，请在30分钟内完成支付' },
    paid: { text: '待发货', color: '#00b578', icon: 'Box', desc: '订单已支付，商家正在准备发货' },
    confirmed: { text: '已确认', color: '#1890ff', icon: 'CircleCheck', desc: '订单已确认，即将发货' },
    shipped: { text: '配送中', color: '#00b578', icon: 'Van', desc: '商品正在配送中，请保持电话畅通' },
    delivered: { text: '已送达', color: '#00b578', icon: 'Check', desc: '商品已送达，请确认收货' },
    completed: { text: '已完成', color: '#666', icon: 'CircleCheckFilled', desc: '订单已完成，感谢您的购买' },
    cancelled: { text: '已取消', color: '#999', icon: 'CircleClose', desc: '订单已取消' },
    refunding: { text: '退款中', color: '#ff9500', icon: 'Money', desc: '退款申请处理中' },
    refunded: { text: '已退款', color: '#666', icon: 'CircleCheck', desc: '退款已完成' }
  }
  return configs[order.value.status]
})

// 进度条步骤
const progressSteps = computed(() => {
  if (!order.value) return []
  const status = order.value.status as OrderStatus

  const steps = [
    { label: '提交订单', time: order.value.createTime, done: true },
    { label: '支付成功', time: order.value.payTime, done: [OrderStatus.PAID, 'confirmed', OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.COMPLETED].includes(status) },
    { label: '商家发货', time: order.value.deliveryTime, done: SHIPPING_ORDER_STATUSES.includes(status) },
    { label: '确认收货', time: order.value.confirmTime, done: [OrderStatus.COMPLETED].includes(status) }
  ]

  if (CANCELLED_ORDER_STATUSES.includes(status)) {
    return steps.slice(0, 2)
  }

  return steps
})

// 返回上一页
const goBack = () => {
  router.back()
}

// 操作按钮
const handlePay = () => {
  if (order.value) {
    router.push(`${ROUTES.ORDER_PAY}?id=${order.value.id}`)
  }
}

const handleCancel = async () => {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await cancelOrder(order.value.id, '用户主动取消')
    ElMessage.success('订单已取消')
    loadOrderDetail()
  } catch (error) {
    // 取消操作
  }
}

const handleConfirm = async () => {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'info'
    })

    await confirmReceipt(order.value.id)
    ElMessage.success('已确认收货')
    loadOrderDetail()
  } catch (error) {
    // 取消操作
  }
}

const handleDelete = async () => {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？删除后不可恢复', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteOrder(order.value.id)
    ElMessage.success('订单已删除')
    router.back()
  } catch (error) {
    // 取消操作
  }
}

const handleRebuy = () => {
  ElMessage.success('商品已加入购物车')
}

const handleReview = () => {
  ElMessage.info('评价功能开发中')
}

const handleRefund = () => {
  ElMessage.info('退款功能开发中')
}

const handleContactService = () => {
  ElMessage.info('客服功能开发中')
}

const handleViewLogistics = () => {
  // 查看物流详情
  ElMessage.info('物流详情功能开发中')
}

const handleCopyOrderNo = () => {
  if (order.value?.orderNo) {
    navigator.clipboard.writeText(order.value.orderNo)
    ElMessage.success('订单号已复制')
  }
}
</script>

<template>
  <div v-if="order" class="order-detail-page">
    <!-- 头部 -->
    <div class="header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <h1 class="title">订单详情</h1>
    </div>

    <!-- 状态区域 -->
    <div class="status-section" :style="{ background: statusConfig?.color }">
      <div class="status-icon">
        <el-icon :size="32">
          <component :is="statusConfig?.icon || 'InfoFilled'" />
        </el-icon>
      </div>
      <div class="status-info">
        <h2 class="status-text">{{ statusConfig?.text }}</h2>
        <p class="status-desc">{{ statusConfig?.desc }}</p>
      </div>
    </div>

    <!-- 进度条（仅非取消/退款订单） -->
    <div v-if="!CANCELLED_ORDER_STATUSES.includes(order.status as OrderStatus)" class="progress-section">
      <div class="progress-line">
        <div
          class="progress-fill"
          :style="{ width: `${(progressSteps.filter(s => s.done).length / progressSteps.length) * 100}%` }"
        />
      </div>
      <div class="progress-steps">
        <div
          v-for="(step, index) in progressSteps"
          :key="index"
          class="step-item"
          :class="{ done: step.done, current: step.done && !progressSteps[index + 1]?.done }"
        >
          <div class="step-dot" :style="{ borderColor: step.done ? statusConfig?.color : '#ddd', background: step.done ? statusConfig?.color : '#fff' }" />
          <div class="step-label">{{ step.label }}</div>
          <div v-if="step.time" class="step-time">{{ formatDateTime(step.time, 'MM-DD HH:mm') }}</div>
        </div>
      </div>
    </div>

    <!-- 物流信息 -->
    <div v-if="logistics && logistics.list.length > 0" class="logistics-section" @click="handleViewLogistics">
      <div class="logistics-header">
        <el-icon><Van /></el-icon>
        <span>{{ logistics.company }} {{ logistics.no }}</span>
      </div>
      <div class="logistics-content">
        <div class="latest-info">
          <p class="info-text">{{ logistics.list[0].content }}</p>
          <p class="info-time">{{ formatDateTime(logistics.list[0].time) }}</p>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="goods-section">
      <div class="section-title">商品信息</div>
      <div class="goods-list">
        <div
          v-for="item in order.items"
          :key="item.id"
          class="goods-item"
          @click="$router.push(getDrugDetailRoute(item.drugId))"
        >
          <img :src="item.image" :alt="item.name" class="goods-image" />
          <div class="goods-info">
            <h4 class="goods-name">{{ item.name }}</h4>
            <p class="goods-spec">{{ item.specification }}</p>
            <div class="goods-bottom">
              <span class="goods-price">¥{{ formatPrice(item.price) }}</span>
              <span class="goods-quantity">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 金额明细 -->
    <div class="amount-section">
      <div class="section-title">金额明细</div>
      <div class="amount-list">
        <div class="amount-item">
          <span>商品总额</span>
          <span>¥{{ formatPrice(order.drugAmount) }}</span>
        </div>
        <div class="amount-item">
          <span>运费</span>
          <span>+¥{{ formatPrice(order.deliveryFee) }}</span>
        </div>
        <div v-if="order.discountAmount > 0" class="amount-item discount">
          <span>优惠金额</span>
          <span>-¥{{ formatPrice(order.discountAmount) }}</span>
        </div>
        <div v-if="order.couponAmount > 0" class="amount-item discount">
          <span>优惠券</span>
          <span>-¥{{ formatPrice(order.couponAmount) }}</span>
        </div>
        <div class="amount-item total">
          <span>实付金额</span>
          <span class="total-price">¥{{ formatPrice(order.paidAmount || order.payableAmount) }}</span>
        </div>
      </div>
    </div>

    <!-- 订单信息 -->
    <div class="info-section">
      <div class="section-title">订单信息</div>
      <div class="info-list">
        <div class="info-item">
          <span class="label">订单编号</span>
          <div class="value">
            <span>{{ order.orderNo }}</span>
            <el-button type="primary" link size="small" @click="handleCopyOrderNo">复制</el-button>
          </div>
        </div>
        <div class="info-item">
          <span class="label">下单时间</span>
          <span class="value">{{ formatDateTime(order.createTime) }}</span>
        </div>
        <div v-if="order.payTime" class="info-item">
          <span class="label">支付时间</span>
          <span class="value">{{ formatDateTime(order.payTime) }}</span>
        </div>
        <div v-if="order.deliveryTime" class="info-item">
          <span class="label">发货时间</span>
          <span class="value">{{ formatDateTime(order.deliveryTime) }}</span>
        </div>
        <div v-if="order.completeTime" class="info-item">
          <span class="label">完成时间</span>
          <span class="value">{{ formatDateTime(order.completeTime) }}</span>
        </div>
        <div v-if="order.remark" class="info-item">
          <span class="label">订单备注</span>
          <span class="value">{{ order.remark }}</span>
        </div>
      </div>
    </div>

    <!-- 配送信息 -->
    <div class="address-section">
      <div class="section-title">配送信息</div>
      <div class="address-info">
        <div class="receiver">
          <el-icon><User /></el-icon>
          <span>{{ order.receiverName }}</span>
          <span>{{ order.receiverPhone }}</span>
        </div>
        <div class="address">
          <el-icon><Location /></el-icon>
          <span>{{ order.receiverAddress }}</span>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="action-bar">
      <!-- 待支付 -->
      <template v-if="order.status === OrderStatus.PENDING">
        <el-button type="primary" size="large" @click="handlePay">立即支付</el-button>
        <el-button size="large" @click="handleCancel">取消订单</el-button>
      </template>

      <template v-if="order.status === OrderStatus.PAID">
        <el-button size="large" @click="handleRefund">申请退款</el-button>
        <el-button size="large" @click="handleContactService">联系客服</el-button>
      </template>

      <template v-if="order.status === OrderStatus.SHIPPED">
        <el-button type="primary" size="large" @click="handleViewLogistics">查看物流</el-button>
        <el-button type="success" size="large" @click="handleConfirm">确认收货</el-button>
      </template>

      <template v-if="order.status === OrderStatus.COMPLETED">
        <el-button type="primary" size="large" @click="handleReview">评价</el-button>
        <el-button size="large" @click="handleRebuy">再次购买</el-button>
        <el-button size="large" @click="handleDelete">删除订单</el-button>
      </template>

      <template v-if="order.status === OrderStatus.CANCELLED">
        <el-button size="large" @click="handleRebuy">再次购买</el-button>
        <el-button size="large" @click="handleDelete">删除订单</el-button>
      </template>
    </div>

    <!-- 底部安全区域 -->
    <div class="safe-area-bottom" />
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.order-detail-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: calc(80px + $safe-area-bottom);
}

// 头部
.header {
  display: flex;
  align-items: center;
  padding: $spacing-sm $spacing-md;
  padding-top: calc($safe-area-top + $spacing-sm);
  background: $bg-white;
  position: sticky;
  top: 0;
  z-index: 100;

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
    flex: 1;
    text-align: center;
    font-size: $font-lg;
    font-weight: 600;
    color: $text-primary;
    margin: 0;
    padding-right: 36px;
  }
}

// 状态区域
.status-section {
  display: flex;
  align-items: center;
  padding: $spacing-lg;
  color: #fff;

  .status-icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    margin-right: $spacing-md;
  }

  .status-info {
    flex: 1;

    .status-text {
      font-size: $font-xl;
      font-weight: 600;
      margin: 0 0 $spacing-xs 0;
    }

    .status-desc {
      font-size: $font-sm;
      opacity: 0.9;
      margin: 0;
    }
  }
}

// 进度条
.progress-section {
  padding: $spacing-lg;
  background: $bg-white;
  margin-bottom: $spacing-sm;

  .progress-line {
    height: 2px;
    background: $bg-gray;
    border-radius: 1px;
    margin-bottom: $spacing-md;
    position: relative;

    .progress-fill {
      height: 100%;
      background: $primary;
      border-radius: 1px;
      transition: width 0.3s ease;
    }
  }

  .progress-steps {
    display: flex;
    justify-content: space-between;

    .step-item {
      flex: 1;
      text-align: center;
      position: relative;

      &.done {
        .step-label {
          color: $text-primary;
        }
      }

      &.current {
        .step-label {
          color: $primary;
          font-weight: 500;
        }
      }

      .step-dot {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        border: 2px solid #ddd;
        background: #fff;
        margin: 0 auto $spacing-xs;
        position: relative;
        z-index: 1;
      }

      .step-label {
        font-size: $font-xs;
        color: $text-secondary;
        margin-bottom: $spacing-xs;
      }

      .step-time {
        font-size: 10px;
        color: $text-tertiary;
      }
    }
  }
}

// 物流信息
.logistics-section {
  margin: $spacing-sm 0;
  padding: $spacing-md;
  background: $bg-white;
  display: flex;
  align-items: center;
  cursor: pointer;

  .logistics-icon {
    width: 40px;
    height: 40px;
    background: $primary-light;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: $spacing-md;

    .el-icon {
      font-size: 20px;
      color: $primary;
    }
  }

  .logistics-info {
    flex: 1;

    .logistics-status {
      font-size: $font-md;
      color: $text-primary;
      margin-bottom: $spacing-xs;
    }

    .logistics-desc {
      font-size: $font-sm;
      color: $text-secondary;
    }
  }

  .el-icon {
    font-size: 16px;
    color: $text-tertiary;
  }
}

// 商品列表
.goods-section {
  margin: $spacing-sm 0;
  padding: $spacing-md;
  background: $bg-white;

  .section-title {
    font-size: $font-md;
    font-weight: 500;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }

  .goods-list {
    .goods-item {
      display: flex;
      padding: $spacing-md 0;
      border-bottom: 1px solid $border-light;
      cursor: pointer;

      &:last-child {
        border-bottom: none;
      }

      .goods-image {
        width: 80px;
        height: 80px;
        border-radius: $radius-sm;
        object-fit: cover;
        margin-right: $spacing-md;
      }

      .goods-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .goods-name {
          font-size: $font-md;
          color: $text-primary;
          font-weight: 500;
          margin: 0;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .goods-spec {
          font-size: $font-sm;
          color: $text-secondary;
          margin: 0;
        }

        .goods-bottom {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .goods-price {
            font-size: $font-md;
            color: $error;
            font-weight: 500;
          }

          .goods-quantity {
            font-size: $font-sm;
            color: $text-secondary;
          }
        }
      }
    }
  }
}

// 金额明细
.amount-section {
  margin: $spacing-sm 0;
  padding: $spacing-md;
  background: $bg-white;

  .section-title {
    font-size: $font-md;
    font-weight: 500;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }

  .amount-list {
    .amount-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $spacing-xs 0;
      font-size: $font-sm;
      color: $text-secondary;

      &.discount {
        color: $error;
      }

      &.total {
        padding-top: $spacing-md;
        margin-top: $spacing-sm;
        border-top: 1px solid $border-light;
        font-size: $font-md;
        font-weight: 500;
        color: $text-primary;

        .total-price {
          color: $error;
          font-size: $font-xl;
        }
      }
    }
  }
}

// 订单信息
.info-section {
  margin: $spacing-sm 0;
  padding: $spacing-md;
  background: $bg-white;

  .section-title {
    font-size: $font-md;
    font-weight: 500;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }

  .info-list {
    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      padding: $spacing-xs 0;
      font-size: $font-sm;

      .label {
        color: $text-secondary;
        white-space: nowrap;
        margin-right: $spacing-md;
      }

      .value {
        color: $text-primary;
        text-align: right;
        word-break: break-all;
        display: flex;
        align-items: center;
        gap: $spacing-sm;
      }
    }
  }
}

// 配送信息
.address-section {
  margin: $spacing-sm 0;
  padding: $spacing-md;
  background: $bg-white;

  .section-title {
    font-size: $font-md;
    font-weight: 500;
    color: $text-primary;
    margin-bottom: $spacing-md;
  }

  .address-info {
    .receiver {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      font-size: $font-md;
      color: $text-primary;
      font-weight: 500;
      margin-bottom: $spacing-sm;

      .el-icon {
        color: $primary;
      }
    }

    .address {
      display: flex;
      align-items: flex-start;
      gap: $spacing-sm;
      font-size: $font-sm;
      color: $text-secondary;
      line-height: 1.5;

      .el-icon {
        color: $primary;
        margin-top: 2px;
      }
    }
  }
}

// 底部操作栏
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: $spacing-sm;
  padding: $spacing-md;
  padding-bottom: calc($spacing-md + $safe-area-bottom);
  background: $bg-white;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  z-index: 100;

  .el-button {
    min-width: 100px;
  }
}

// 底部安全区域
.safe-area-bottom {
  height: $safe-area-bottom;
}
</style>
