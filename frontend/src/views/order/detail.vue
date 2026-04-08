<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useOrderStore } from '@/stores/order'
import { getOrderDetail, cancelOrder, confirmReceipt, deleteOrder, getLogisticsInfo } from '@/api/modules/order'
import { formatPrice, formatDateTime } from '@/utils'
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
      if (['shipped', 'delivered', 'completed'].includes(res.status)) {
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
  const status = order.value.status

  const steps = [
    { label: '提交订单', time: order.value.createTime, done: true },
    { label: '支付成功', time: order.value.payTime, done: ['paid', 'confirmed', 'shipped', 'delivered', 'completed'].includes(status) },
    { label: '商家发货', time: order.value.deliveryTime, done: ['shipped', 'delivered', 'completed'].includes(status) },
    { label: '确认收货', time: order.value.confirmTime, done: ['completed'].includes(status) }
  ]

  // 处理取消和退款订单
  if (['cancelled', 'refunding', 'refunded'].includes(status)) {
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
    router.push(`/order/pay?id=${order.value.id}`)
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
  if (order.value) {
    router.push(`/order/review/${order.value.id}`)
  }
}

const handleRefund = () => {
  if (order.value) {
    router.push(`/order/refund/${order.value.id}`)
  }
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
    <div v-if="!['cancelled', 'refunding', 'refunded'].includes(order.status)" class="progress-section">
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
          @click="$router.push(`/drug/${item.drugId}`)"
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
          <span>¥{{ format