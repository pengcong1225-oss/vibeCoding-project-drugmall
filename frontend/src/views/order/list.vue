<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import OrderCard from '@/components/OrderCard/index.vue'
import Empty from '@/components/Empty/index.vue'
import Loading from '@/components/Loading/index.vue'
import { getOrders, cancelOrder, deleteOrder, confirmReceipt } from '@/api/modules/order'
import { OrderStatus } from '@/constants'
import { ROUTES } from '@/constants/routes'
import type { Order } from '@/types'

const router = useRouter()
const route = useRoute()

const statusTabs = [
  { label: '全部', value: '', count: 0 },
  { label: '待付款', value: OrderStatus.PENDING, count: 0 },
  { label: '待发货', value: OrderStatus.PAID, count: 0 },
  { label: '待收货', value: OrderStatus.SHIPPED, count: 0 },
  { label: '待评价', value: OrderStatus.COMPLETED, count: 0 }
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
    // 兼容不同的返回格式
    if (Array.isArray(res)) {
      if (page.value === 1) {
        orders.value = res
      } else {
        orders.value.push(...res)
      }
      hasMore.value = res.length === pageSize
      if (res.length > 0) {
        page.value++
      }
    } else if (res && res.list && Array.isArray(res.list)) {
      if (page.value === 1) {
        orders.value = res.list
      } else {
        orders.value.push(...res.list)
      }
      hasMore.value = res.list.length === pageSize
      if (res.list.length > 0) {
        page.value++
      }

      // 更新状态统计
      if (res.stats) {
        statusTabs[1].count = res.stats.pendingPayment || 0
        statusTabs[2].count = res.stats.pendingShipment || 0
        statusTabs[3].count = res.stats.pendingReceipt || 0
        statusTabs[4].count = res.stats.pendingReview || 0
      }
    } else {
      // 如果返回格式不符合预期，初始化为空数组
      if (page.value === 1) {
        orders.value = []
      }
      hasMore.value = false
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    // 使用模拟数据作为fallback（开发阶段）
    if (page.value === 1) {
      orders.value = getMockOrders()
    }
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

// 模拟数据（开发阶段使用）
const getMockOrders = (): Order[] => {
  return [
    {
      id: 'ORD001',
      orderNo: 'DM20240410001',
      status: 'pending',
      items: [
        {
          id: 'ITEM001',
          drugId: 'DRUG001',
          name: '阿莫西林胶囊',
          specification: '0.25g*24粒',
          image: '',
          price: 25.8,
          quantity: 2
        }
      ],
      drugAmount: 51.6,
      deliveryFee: 6,
      discountAmount: 5,
      couponAmount: 0,
      payableAmount: 52.6,
      paidAmount: 0,
      receiverName: '张**',
      receiverPhone: '138****8888',
      receiverAddress: '北京市朝阳区建国路88号SOHO现代城1号楼1单元101室',
      createTime: new Date().toISOString(),
      remark: ''
    },
    {
      id: 'ORD002',
      orderNo: 'DM20240408002',
      status: 'shipped',
      items: [
        {
          id: 'ITEM002',
          drugId: 'DRUG002',
          name: '布洛芬缓释胶囊',
          specification: '0.3g*20粒',
          image: '',
          price: 35.0,
          quantity: 1
        }
      ],
      drugAmount: 35.0,
      deliveryFee: 6,
      discountAmount: 0,
      couponAmount: 3,
      payableAmount: 38.0,
      paidAmount: 38.0,
      receiverName: '张**',
      receiverPhone: '138****8888',
      receiverAddress: '北京市朝阳区建国路88号SOHO现代城1号楼1单元101室',
      createTime: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
      payTime: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
      deliveryTime: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
      remark: ''
    }
  ]
}

// 刷新订单列表
const refreshOrders = async () => {
  page.value = 1
  hasMore.value = true
  orders.value = []
  await loadOrders()
}

// 监听路由参数变化
watch(() => route.query.status, (newStatus) => {
  currentStatus.value = (newStatus as string) || ''
  refreshOrders()
}, { immediate: true })

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
  router.push(`${ROUTES.ORDER_PAY}?id=${order.id}`)
}

const handleCancel = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？取消后无法恢复', '提示', {
      confirmButtonText: '确定取消',
      cancelButtonText: '再想想',
      type: 'warning'
    })

    await cancelOrder(order.id, '用户主动取消')
    ElMessage.success('订单已取消')
    refreshOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('操作失败，请重试')
    }
  }
}

const handleConfirm = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？确认后订单将完成', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'info'
    })

    await confirmReceipt(order.id)
    ElMessage.success('已确认收货，感谢您的购买！')
    refreshOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      ElMessage.error('操作失败，请重试')
    }
  }
}

const handleDelete = async (order: Order) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？删除后不可恢复', '提示', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteOrder(order.id)
    ElMessage.success('订单已删除')
    refreshOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除订单失败:', error)
      ElMessage.error('操作失败，请重试')
    }
  }
}

const handleRebuy = (order: Order) => {
  try {
    // 将订单中的商品重新加入购物车
    ElMessage.success(`已将${order.items?.length || 0}件商品加入购物车`)
  } catch (error) {
    console.error('再次购买失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

const handleReview = (order: Order) => {
  ElMessage.info('评价功能开发中')
}

const handleRefund = (order: Order) => {
  ElMessage.info('退款功能开发中')
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
    <div class="order-list" v-infinite-scroll="loadMore" :infinite-scroll-disabled="!hasMore || loading" :infinite-scroll-distance="50">
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

      <!-- 首次加载中 -->
      <div v-if="loading && orders.length === 0" class="loading-wrapper">
        <Loading />
      </div>

      <!-- 加载更多 -->
      <div v-if="loading && orders.length > 0" class="loading-more">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载更多...</span>
      </div>

      <!-- 没有更多 -->
      <div v-if="!loading && !hasMore && orders.length > 0" class="no-more">
        <div class="no-more-line"></div>
        <span>没有更多订单了</span>
        <div class="no-more-line"></div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="isEmpty" class="empty-state">
      <Empty description="暂无相关订单" :image-size="160">
        <template #extra>
          <div class="empty-actions">
            <el-button type="primary" round @click="$router.push(ROUTES.HOME)">
              去逛逛
            </el-button>
            <el-button round @click="refreshOrders">
              刷新列表
            </el-button>
          </div>
        </template>
      </Empty>
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
  box-shadow: $shadow-sm;

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
    transition: all 0.2s;

    &:hover {
      background: $bg-gray;
      transform: scale(1.05);
    }

    &:active {
      transform: scale(0.95);
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
    transition: all 0.25s ease;
    font-weight: 500;

    &:hover {
      color: $primary;
    }

    &.active {
      color: $primary;
      font-weight: 600;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 20%;
        right: 20%;
        height: 3px;
        background: linear-gradient(90deg, $primary, $primary-light);
        border-radius: 2px;
        animation: slideIn 0.3s ease;
      }
    }

    .tab-badge {
      min-width: 18px;
      height: 18px;
      padding: 0 5px;
      background: $error;
      color: $text-white;
      font-size: 10px;
      font-weight: bold;
      border-radius: 9px;
      display: flex;
      align-items: center;
      justify-content: center;
      line-height: 1;
    }
  }
}

@keyframes slideIn {
  from {
    width: 0;
    left: 50%;
  }
  to {
    width: 60%;
    left: 20%;
  }
}

// 订单列表
.order-list {
  padding: $spacing-md;
  min-height: 300px;

  .loading-wrapper {
    padding: $spacing-xxl;
    display: flex;
    justify-content: center;
  }

  .loading-more {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-sm;
    padding: $spacing-xl;
    color: $text-tertiary;
    font-size: $font-sm;

    .el-icon {
      font-size: 16px;
    }
  }

  .no-more {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-md;
    padding: $spacing-xl;
    color: $text-tertiary;
    font-size: $font-sm;

    .no-more-line {
      width: 60px;
      height: 1px;
      background: $border-color;
    }
  }
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $spacing-xxl $spacing-md;
  min-height: 60vh;

  .empty-actions {
    display: flex;
    gap: $spacing-md;
    margin-top: $spacing-lg;

    .el-button {
      min-width: 120px;
    }
  }
}
</style>
