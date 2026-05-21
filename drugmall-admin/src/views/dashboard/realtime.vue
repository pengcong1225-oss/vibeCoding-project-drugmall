<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDashboardRealtime } from '@/api/dashboard'
import type { DashboardRealtime } from '@/types/dashboard'

const loading = ref(false)
const realtimeData = ref<DashboardRealtime | null>(null)
let timer: number | null = null

const orderFlow = ref<Array<{ time: string, amount: number, status: string }>>([])
const onlineUsers = ref(0)
const onlineConsultations = ref(0)
const todayIncome = ref(0)
const todayOrders = ref(0)

async function fetchData() {
  loading.value = true
  try {
    const res = await getDashboardRealtime()
    realtimeData.value = res
    onlineUsers.value = res.onlineUsers
    onlineConsultations.value = res.onlineConsultations
    todayIncome.value = res.todayIncome
    todayOrders.value = res.todayOrders
    orderFlow.value = res.orderFlow || []
  } catch {
    ElMessage.error('获取实时数据失败')
  } finally {
    loading.value = false
  }
}

function startPolling() {
  timer = window.setInterval(() => {
    fetchData()
  }, 5000)
}

function stopPolling() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

onMounted(() => {
  fetchData()
  startPolling()
})

onUnmounted(() => {
  stopPolling()
})
</script>

<template>
  <div class="realtime-dashboard" v-loading="loading">
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #409EFF">
            <el-icon :size="32"><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ onlineUsers }}</div>
            <div class="stat-label">在线用户</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #67C23A">
            <el-icon :size="32"><ShoppingCart /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ todayOrders }}</div>
            <div class="stat-label">今日订单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #E6A23C">
            <el-icon :size="32"><Wallet /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">¥{{ todayIncome.toFixed(2) }}</div>
            <div class="stat-label">今日收入</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #F56C6C">
            <el-icon :size="32"><ChatDotRound /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ onlineConsultations }}</div>
            <div class="stat-label">在线问诊</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>实时订单流</span>
          <el-tag type="success" effect="dark">实时更新</el-tag>
        </div>
      </template>
      <el-table :data="orderFlow" border stripe max-height="400">
        <el-table-column prop="time" label="时间" width="180" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.amount.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'paid'" type="success">已支付</el-tag>
            <el-tag v-else-if="row.status === 'pending'" type="warning">待支付</el-tag>
            <el-tag v-else type="info">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 16px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
