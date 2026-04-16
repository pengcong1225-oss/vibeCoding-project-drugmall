<template>
  <div class="order-list-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>订单列表</h2>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-section">
      <el-input
        v-model="searchQuery"
        placeholder="搜索订单号/用户名/药品名称"
        style="width: 300px"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select v-model="statusFilter" placeholder="订单状态" clearable style="width: 120px; margin-left: 12px;">
        <el-option label="全部" value="" />
        <el-option label="待付款" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="待收货" :value="2" />
        <el-option label="待评价" :value="3" />
        <el-option label="已完成" :value="4" />
        <el-option label="已取消" :value="-1" />
        <el-option label="退款中" :value="-2" />
      </el-select>

      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="margin-left: 12px"
      />

      <el-button type="primary" style="margin-left: 12px" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <el-card class="stat-card">
        <div class="stat-value">{{ stats.todayOrders }}</div>
        <div class="stat-label">今日订单</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">¥{{ stats.todaySales.toFixed(2) }}</div>
        <div class="stat-label">今日销售额</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ stats.pendingShipment }}</div>
        <div class="stat-label">待发货</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ stats.refunding }}</div>
        <div class="stat-label">退款/售后</div>
      </el-card>
    </div>

    <!-- 订单表格 -->
    <el-card class="table-card">
      <el-table :data="orderList" style="width: 100%" v-loading="loading">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNo" label="订单号" width="180">
          <template #default="{ row }">
            <el-link type="primary" @click="viewDetail(row)">{{ row.orderNo }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160" />
        <el-table-column label="用户信息" width="150">
          <template #default="{ row }">
            <div>{{ row.nickname || row.username }}</div>
            <div style="color: #999; font-size: 12px;">{{ row.phone }}</div>
          </template>
        </el-table-column>
        <el-table-column label="商品信息" min-width="200">
          <template #default="{ row }">
            <div class="product-info">
              <span>{{ row.items?.[0]?.productName || '-' }}</span>
              <span v-if="row.items && row.items.length > 1" style="color: #999;"> 等{{ row.items.length }}件商品</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="payAmount" label="订单金额" width="120">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 500;">¥{{ row.payAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 1" link type="primary" @click="handleShip(row)">发货</el-button>
            <el-button v-if="row.status === 0" link type="danger" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, cancelOrder, shipOrder } from '@/api/order'
import type { Order, OrderQueryParams } from '@/types/order'

const router = useRouter()
const loading = ref(false)
const searchQuery = ref('')
const statusFilter = ref<number | undefined>()
const dateRange = ref<string[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const orderList = ref<Order[]>([])

const stats = reactive({
  todayOrders: 0,
  todaySales: 0,
  pendingShipment: 0,
  refunding: 0
})

const statusMap: Record<number, { text: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  0: { text: '待付款', type: 'warning' },
  1: { text: '待发货', type: 'primary' },
  2: { text: '待收货', type: 'success' },
  3: { text: '待评价', type: 'info' },
  4: { text: '已完成', type: 'success' },
  [-1]: { text: '已取消', type: 'info' },
  [-2]: { text: '退款中', type: 'danger' },
  [-3]: { text: '已退款', type: 'info' }
}

const getStatusType = (status: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  return statusMap[status]?.type || 'info'
}

const getStatusText = (status: number) => {
  return statusMap[status]?.text || '未知'
}

const fetchOrderList = async () => {
  loading.value = true
  try {
    const params: OrderQueryParams = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value,
      status: statusFilter.value
    }
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    
    const res = await getOrderList(params)
    orderList.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const fetchOrderStats = async () => {
  try {
    const res = await fetch('/api/admin/orders/stats')
    const data = await res.json()
    if (data.code === 200) {
      Object.assign(stats, data.data)
    }
  } catch (error) {
    console.error('获取订单统计失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchOrderList()
}

const viewDetail = (row: Order) => {
  router.push(`/order/detail/${row.id}`)
}

const handleShip = async (row: Order) => {
  try {
    const { value: formValue } = await ElMessageBox.prompt('请输入物流单号', '发货', {
      confirmButtonText: '确认发货',
      cancelButtonText: '取消',
      inputPattern: /\S+/,
      inputErrorMessage: '物流单号不能为空'
    })
    
    await shipOrder({
      orderId: row.id,
      company: '顺丰快递',
      trackingNo: formValue
    })
    ElMessage.success('发货成功')
    fetchOrderList()
  } catch {
    // 取消操作
  }
}

const handleCancel = async (row: Order) => {
  try {
    await ElMessageBox.confirm(
      `确认取消订单 ${row.orderNo}？`,
      '确认取消',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning' as const
      }
    )
    
    await cancelOrder(row.id, '管理员取消')
    ElMessage.success('订单已取消')
    fetchOrderList()
  } catch {
    // 取消操作
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchOrderList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchOrderList()
}

onMounted(() => {
  fetchOrderList()
  fetchOrderStats()
})
</script>

<style scoped lang="scss">
.order-list-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;

  h2 {
    margin: 0;
    font-size: 20px;
    font-weight: 500;
    color: #333;
  }
}

.search-section {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;

  .stat-card {
    flex: 1;
    text-align: center;

    :deep(.el-card__body) {
      padding: 20px;
    }

    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: #333;
      margin-bottom: 8px;
    }

    .stat-label {
      font-size: 14px;
      color: #999;
    }
  }
}

.table-card {
  .product-info {
    display: flex;
    align-items: center;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>
