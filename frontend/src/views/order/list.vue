<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import OrderCard from '@/components/OrderCard/index.vue'
import Empty from '@/components/Empty/index.vue'
import Loading from '@/components/Loading/index.vue'
import { useOrderStore } from '@/stores/order'
import { getOrders, cancelOrder, deleteOrder, confirmReceipt } from '@/api/modules/order'
import type { Order, OrderStatus } from '@/types'

const router = useRouter()
const route = useRoute()
const orderStore = useOrderStore()

// 状态筛选
const statusTabs = [
  { label: '全部', value: '', count: 0 },
  { label: '待付款', value: 'pending', count: 0 },
  { label: '待发货', value: 'paid', count: 0 },
  { label: '待收货', value: 'shipped', count: 0 },
  { label: '待评价', value: 'completed', count: 0 }
]

const currentStatus = ref('')
const activeTabIndex = computed(() => {
  return statusTabs.findIndex(tab => tab.value === currentStatus.value)
})

// 列表数据
const orders = ref<Order[]>([])
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)
const pageSize = 10

// 空状态判断
const isEmpty = computed(() => !loading.value && orders.value.length === 0)

// 监听路由参数变化
watch(() => route.query.status, (newStatus) => {
  currentStatus.value = (newStatus as string) || ''
  refreshOrders()
}, { immediate: true })

// 刷新订单列表
const refreshOrders = async () => {
  page.value = 1
  hasMore.value = true
  orders.value = []
  await loadOrders()
}

// 加载订单列表
const loadOrders = async () => {
  if (loading.value || !hasMore.value) return

  loading.value = true
  try {
    const params: any = {
      page: page.value,
      size: pageSize
    }
    if (currentStatus.value) {
      params.status = currentStatus.value
    }

    const res = await getOrders(params)
    if (res && res.list) {
      if (page.value === 1) {
        orders.value = res.list
      } else {
        orders.value.push(...res.list)
      }
      hasMore.value = res.list.length === pageSize
      page.value++

      // 更新统计
      if (res.stats) {
        statusTabs[1].count = res.stats.pendingPayment || 0
        statusTabs[2].count = res.stats.pendingShipment || 0
        statusTabs[3].count = res.stats.pendingReceipt || 0
        statusTabs[4].count = res.stats.pendingReview || 0
      }
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

// 切换状态标签
const handleTabChange = (index: number) => {
  const tab = statusTabs[index]
  currentStatus.value = tab.value
  router.replace({
    path: '/order/list',
    query: tab.value ? { status: tab.value } : undefined
  })
}

// 订单操作处理
const handlePay = (order: Order) => {
  router.push(`/order/pay?id=${order.id}`)
}

const handleCancel = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await cancelOrder(order.id, '用户主动取消')
    ElMessage.success('订单已取消')
    refreshOrders()
  } catch (error) {
    // 取消操作
  }
}

const handleConfirm = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'info'
    })

    await confirmReceipt(order.id)
    ElMessage.success('已确认收货')
    refreshOrders()
  } catch (error) {
    // 取消操作
  }
}

const handleDelete = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？删除后不可恢复', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteOrder(order.id)
    ElMessage.success('订单已删除')
    refreshOrders()
  } catch (error) {
    // 取消操作
  }
}

const handleRebuy = (order: Order) => {
  // 将订单中的商品重新加入购物车
  ElMessage.success('商品已加入购物车')
}

const handleReview = (order: Order) => {
  router.push(`/order/review/${order.id}`)
}

const handleRefund = (order: Order) => {
  router.push(`/order/refund/${order.id}`)
}

// 加载更多
const loadMore = () => {
  if (!loading.value && hasMore.value) {
    loadOrders()
  }
}
</script>

<template>
  <div class="order-list-page">
    <!-- 头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </div>
      <h1 class="title">我的订单</h1>
    </div>

    <!-- 状态筛选标签 -->
    <div class="status-tabs">
      <div
        v-for="(tab, index) in statusTabs"
        :key="tab.value"
        class="tab-item"
        :class="{ active: activeTabIndex === index }"
        @click="handleTabChange(index)"
      >
        <span class="tab-label">{{ tab.label }}</span>
        <span v-if="tab.count > 0" class="tab-badge">{{ tab.count > 99 ? '99+' : tab.count }}</span>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="order-list" v-infinite-scroll="loadMore" :infinite-scroll-disabled="!hasMore || loading">
      <template v-if="!isEmpty">
        <OrderCard
          v-for="order in orders"
          :key="order.id"
          :order="order"
          @pay="handlePay"
          @cancel="handleCancel"
          @confirm="handleConfirm"
          @delete="handleDelete"
          @rebuy="handleRebuy"
          @review="handleReview"
          @refund="handleRefund"
        />
      </template>

      <!-- 加载中 -->
      <div v-if="loading && orders.length === 0" class="loading-wrapper">
        <Loading />
      </div>

      <!-- 加载更多 -->
      <div v-if="loading && orders.length > 0" class="loading-more">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <!-- 没有更多 -->
      <div v-if="!loading && !hasMore && orders.length > 0" class="no-more">
        没有更多订单了
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="isEmpty" class="empty-state">
      <Empty
        description="暂无相关订单"
        :image-size="160"
      />
      <el-button type="primary" @click="$router.push('/home')">去购物</el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.order-list-page {
  min-height: 100vh;
  background: $bg-primary;
  padding-bottom: $spacing-lg;
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

// 状态标签
.status-tabs {
  display: flex;
  background: $bg-white;
  padding: 0 $spacing-md;
  border-bottom: 1px solid $border-light;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
  }

  .tab-item {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-md $spacing-lg;
    font-size: $font-md;
    color: $text-secondary;
    cursor: pointer;
    white-space: nowrap;
    position: relative;
    transition: all 0.2s;

    &:hover {
      color: $primary;
    }

    &.active {
      color: $primary;
      font-weight: 500;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 20%;
        right: 20%;
        height: 2px;
        background: $primary;
        border-radius: 1px;
      }
    }

    .tab-badge {
      min-width: 16px;
      height: 16px;
      padding: 0 4px;
      background: $error;
      color: $text-white;
      font-size: 10px;
      font-weight: bold;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}

// 订单列表
.order-list {
  padding: $spacing-md;

  .loading-wrapper {
    padding: $spacing-xl;
  }

  .loading-more {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-sm;
    padding: $spacing-lg;
    color: $text-tertiary;
    font-size: $font-sm;
  }

  .no-more {
    text-align: center;
    padding: $spacing-lg;
    color: $text-tertiary;
    font-size: $font-sm;
  }
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $spacing-xxl $spacing-md;

  :deep(.el-button) {
    margin-top: $spacing-lg;
    padding: $spacing-md $spacing-xl;
    font-size: $font-md;
  }
}
</style>
